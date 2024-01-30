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
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
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
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
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

    //initial camera position
    private void initializeCamera() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f; //zoom-level

        //start camera focus in the middle of the map
        camera.position.x = (gameMap.getWidth()*16) / 2;
        camera.position.y = (gameMap.getHeight()*16) / 2;

        camera.update();
    }

    //update camera according to viewport requirements
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


    // Screen interface methods with necessary functionality
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

    public void update(float dt){
        if(!hud.isGameOver()) {
            hud.update(dt);
            movementManager.handleInput();
        }
    }

    @Override
    public void pause() {
        movementManager.pause();
        hud.pauseTimer();
    }

    @Override
    public void resume() {
        movementManager.resume();
        hud.resumeTimer();
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

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

