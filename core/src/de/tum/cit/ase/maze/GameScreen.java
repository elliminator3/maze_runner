package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
    private final BitmapFont font;
    private Character character;
    private MovementManager movementManager; // The movement manager
    private float sinusInput = 0f;
    private GameMap gameMap;
    private GameMapBackground background;
    private Hud hud;

    private Viewport gamePort;


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {

        this.game = game;

        //initialize background and gameMap
        background = new GameMapBackground("maps/level-1.properties");
        gameMap = new GameMap("maps/level-1.properties");
        //find entry of the gameMap
        Point entryPoint = gameMap.findEntry();

        //initialize character and camera
        character = new Character(entryPoint.x, entryPoint.y, "character.png", 3);
        movementManager = new MovementManager(character, gameMap);
        initializeCamera();
        camera.position.set(character.getX(), character.getY(), 0); //viewport //tileSize
        //screenViewport for viewport requirements
        gamePort = new ScreenViewport(camera);

        //hud //ToDo (look in resize method)
        hud = new Hud(game.getSpriteBatch()); //Mario

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");
    }

    //viewport requirements 1
    private void initializeCamera() {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f; //zoom-level

        //start camera with frame that contains the character
        camera.position.x = character.getX();
        camera.position.y = character.getY();;

        camera.update();
    }
    //viewport requirements 2
    private void updateCameraPosition(){
        final float LERP_FACTOR = 0.5f;

        //limits within which the camera shall follow the player's movement
        float cameraMoveThresholdX = camera.viewportWidth * camera.zoom * 0.2f; // 10% von jeder Seite
        float cameraMoveThresholdY = camera.viewportHeight * camera.zoom * 0.2f; // 10% von jeder Seite

        //maximum permitted position of the camera based on the map
        float maxCameraX = gameMap.getWidth() * 16 - camera.viewportWidth * camera.zoom / 2;
        float maxCameraY = gameMap.getHeight() * 16 - camera.viewportHeight * camera.zoom / 2;

        //coordinates of the character /ToDo: center!!
        float playerCenterX = character.getX();
        float playerCenterY = character.getY();

        //set camera position to the target position
        float targetX = playerCenterX;
        float targetY = playerCenterY;

        //has character crossed the camera boundaries? -> update the target position
        if (playerCenterX < camera.position.x - cameraMoveThresholdX) {
            targetX = camera.position.x - cameraMoveThresholdX;
        } else if (playerCenterX > camera.position.x + cameraMoveThresholdX) {
            targetX = camera.position.x + cameraMoveThresholdX;
        }

        if (playerCenterY < camera.position.y - cameraMoveThresholdY) {
            targetY = camera.position.y - cameraMoveThresholdY;
        } else if (playerCenterY > camera.position.y + cameraMoveThresholdY) {
            targetY = camera.position.y + cameraMoveThresholdY;
        }

        //clamping
        targetX = Math.max(camera.viewportWidth * camera.zoom / 2, Math.min(targetX, maxCameraX));
        targetY = Math.max(camera.viewportHeight * camera.zoom / 2, Math.min(targetY, maxCameraY));

        //interpolation
        camera.position.x += (targetX - camera.position.x) * LERP_FACTOR;
        camera.position.y += (targetY - camera.position.y) * LERP_FACTOR;

        camera.update();
    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen
        hud.stage.act(delta);
        hud.stage.draw();

        // Handle user input
        movementManager.handleInput();

        //enemy movement

        //viewport
        updateCameraPosition();

        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteBatch().begin(); // Important to call this before drawing anything


        //draw the maze
        background.render(game.getSpriteBatch());
        gameMap.render(game.getSpriteBatch());

        //enemy movement
        for (Enemy enemy : gameMap.getEnemies()) {
            enemy.update(delta, gameMap); // Update enemy position
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

        //ToDo: Hud
        /*// Aktualisiere das Viewport des HUD mit der neuen Breite und Höhe
        hud.getViewport().update(width, height, true);
        hud.getViewport().apply();

        // Stelle sicher, dass das HUD oben auf dem Bildschirm zentriert ist
        hud.stage.getViewport().getCamera().position.set(width / 2f, height, 0);
        hud.stage.getViewport().getCamera().update();

        // Dies ist notwendig, damit das Layout korrekt neu berechnet wird
        hud.stage.getViewport().update(width, height, true);
        hud.stage.getViewport().apply();
        hud.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        hud.stage.getViewport().getCamera().update();
    }

        /* unser altes Zeug: viewport
        gamePort.update(width, height);
        updateCamera();
        updateCameraBounds();

        /*camera.setToOrtho(false);
        gamePort.update(width,height); //Mario
        camera.update();*/
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
        //dispose assets like textures when you're done with them
        character.getTexture().dispose();
    }

    // Additional methods and logic can be added as needed for the game screen
}