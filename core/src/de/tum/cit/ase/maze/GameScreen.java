package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.*;

/**
 * The {@code GameScreen} class handles the rendering and updating of the gameplay screen in Maze Runner.
 * It encapsulates game logic, manages the rendering of game elements such as the map, the character, and HUD,
 * and processes user input for character movement and game controls.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private OrthographicCamera camera;
    private Character character;
    private MovementManager movementManager;
    private GameMap gameMap;
    private Hud hud;
    private Key key;
    private Viewport gamePort;
    private Viewport hudPort; //new
    private Music music;
    private TextureManager textureManager;


    /**
     * Constructs the GameScreen with reference to the main game class for resource access.
     * It sets up the game environment, including the map, character, HUD, and camera.
     *
     * @param game The main {@link MazeRunnerGame} class, providing access to shared resources.
     */
    public GameScreen(MazeRunnerGame game) {

        this.game = game;
        this.textureManager = game.getTextureManager();

        //default values
        String mapPath = game.getMapFilePath();
        if (mapPath != null && !mapPath.isEmpty()) {
            gameMap = new GameMap(mapPath, textureManager);
        } else {
            gameMap = new GameMap("maps/level-1.properties", textureManager); //ToDo: how should we handle this?
        }

        //initialize gamePort as ScreenViewport /necessary for viewport requirements
        //gamePort = new ScreenViewport(camera);

        //find entry and key of the gameMap
        Point entryPoint = gameMap.findEntry();
        Point keyPoint = gameMap.findKey();

        //initialize character and camera
        character = new Character(entryPoint.x, entryPoint.y, "character.png", 5, gameMap, textureManager);
        hud = new Hud(game.getSpriteBatch(), character, game);
        key = new Key(keyPoint.x, keyPoint.y,"objects.png", textureManager); // Create the Key instance
        movementManager = new MovementManager(character, gameMap, hud, key, game);
        initializeCamera();

        //Viewport for viewport requirements
        gamePort = new ScreenViewport(camera);
        hudPort = new ScreenViewport(new OrthographicCamera());

        character.setHud(hud);
        // Get the font from the game's skin
        BitmapFont font = game.getSkin().getFont("font");

    }

    /**
     * Initializes the camera to the starting position and sets the zoom level.
     */
    private void initializeCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f; //zoom-level

        //start camera focus in the middle of the map
        camera.position.x = (gameMap.getWidth()*16) / 2;
        camera.position.y = (gameMap.getHeight()*16) / 2;

        camera.update();
    }

    /**
     * Updates the camera position to follow the character while staying within the defined safe zone margins.
     * This ensures the character remains visible and centered during movement.
     */
    private void updateCameraPosition(){
        final float LERP_FACTOR = 0.009f; //has to catch up with character speed

        //'safe zone' margins
        // visible in the middle 80 percent of the screen as minimum requirement
        // decided for middle 60 percent because it gives a better overview of the map
        float safeZoneMarginX = camera.viewportWidth * camera.zoom * 0.2f;
        float safeZoneMarginY = camera.viewportHeight * camera.zoom * 0.2f;

        // Calculate the boundaries of the safe zone
        float leftBoundary = camera.position.x - camera.viewportWidth * camera.zoom / 2 + safeZoneMarginX;
        float rightBoundary = camera.position.x + camera.viewportWidth * camera.zoom / 2 - safeZoneMarginX;
        float bottomBoundary = camera.position.y - camera.viewportHeight * camera.zoom / 2 + safeZoneMarginY;
        float topBoundary = camera.position.y + camera.viewportHeight * camera.zoom / 2 - safeZoneMarginY;

        // Character's position //ToDo: center?
        float playerX = character.getX();
        float playerY = character.getY();

        // Determine if the camera needs to move to keep the character in the safe zone
        float targetX = camera.position.x;
        float targetY = camera.position.y;

        if (playerX < leftBoundary) {
            targetX = playerX - safeZoneMarginX;
        } else if (playerX > rightBoundary) {
            targetX = playerX + safeZoneMarginX;
        }

        if (playerY < bottomBoundary) {
            targetY = playerY - safeZoneMarginY;
        } else if (playerY > topBoundary) {
            targetY = playerY + safeZoneMarginY;
        }

        // Interpolate camera position for smoother movement
        camera.position.x += (targetX - camera.position.x) * LERP_FACTOR;
        camera.position.y += (targetY - camera.position.y) * LERP_FACTOR;

        camera.update();
    }


    /**
     * Renders the game elements including the map, character, and HUD.
     * It also handles game logic updates and user input every frame.
     *
     * @param delta The time in seconds since the last render call.
     */
    @Override
    public void render(float delta) {
        // Check for escape key press to go back to the menu

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen
        hud.stage.act(delta);
        hud.stage.draw();
        hud.update(delta);

        // Handle user input
        movementManager.handleInput();
        movementManager.handleEnemyCollusion(delta);
        movementManager.handleTrapCollusion(delta);
        movementManager.handleExtraLifeCollision();

        //cooldown
        character.update(delta);
//update(delta); makes character go faster but timer as well :/
        //viewport
        updateCameraPosition();

        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteBatch().begin(); // Important to call this before drawing anything


        //draw the maze
        gameMap.renderBackground(game.getSpriteBatch());
        gameMap.render(game.getSpriteBatch());


        //enemy movement
        for (Enemy enemy : gameMap.getEnemies()) {
            enemy.update(delta, gameMap,character); // Update enemy position //new argument character
            enemy.render(game.getSpriteBatch()); // Render enemy
        }
        //draw character
        character.render(game.getSpriteBatch());

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.pause();
            pause();
            // Pause the game and go to the menu
            // Ensure gameScreen is not disposed
            game.goToMenu();
        }

        game.getSpriteBatch().end(); // Important to call this after drawing everything
        hud.stage.act(delta);
        hud.stage.draw();

    }

    /**
     * Adjusts the camera and HUD viewports in response to the screen resizing, maintaining the game's aspect ratio.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        //update the camera with the new width and height
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        // Update hud viewport //new
        hud.getViewport().update(width, height, true);
        hud.updatePositions();

    }

    /**
     * Updates the game logic based on the time elapsed since the last frame.
     * This method is responsible for updating the HUD and handling character movement.
     *
     * @param dt The time in seconds since the last update.
     */
    public void update(float dt){
        if(!hud.isGameOver()) {
            hud.update(dt);
            movementManager.handleInput();
        }
    }

    /**
     * Pauses the game, stopping character movement and the HUD timer.
     */
    @Override
    public void pause() {
        movementManager.pause();
        hud.pauseTimer();
    }

    /**
     * Resumes the game from a paused state, reactivating character movement and the HUD timer.
     */
    @Override
    public void resume() {
        movementManager.resume();
        hud.resumeTimer();
    }

    /**
     * Called when the GameScreen becomes the current screen for the game.
     * Typically used to set up resources and begin music or animations.
     */
    @Override
    public void show() {

    }

    /**
     * Called when the GameScreen is no longer the current screen.
     * Used to pause animations and music or save game state.
     */
    @Override
    public void hide() {
    }

    /**
     * Disposes of all the resources used by the GameScreen to free up memory.
     * This includes textures, sounds, and any other assets specific to the GameScreen.
     */
    @Override
    public void dispose() {
        // Dispose assets like textures when you're done with them
        if (character != null) character.getTexture().dispose();
        if (key != null) key.getTexture().dispose();
        if (hud != null) hud.dispose();
        if (gameMap != null) gameMap.dispose();
        if (textureManager != null) textureManager.dispose();
        // Dispose other assets if necessary
    }



}

