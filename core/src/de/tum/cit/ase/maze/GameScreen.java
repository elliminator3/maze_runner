package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
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
        gamePort = new FitViewport(MazeRunnerGame.V_WIDTH, MazeRunnerGame.V_HEIGHT); //Mario
        hud = new Hud(game.getSpriteBatch()); //Mario

        // Initialize character and movement manager
        character = new Character(30, 30, "character.png", 3);
        background = new GameMapBackground("maps/level-1.properties");
        gameMap = new GameMap("maps/level-1.properties");
        movementManager = new MovementManager(character, gameMap);

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.75f;

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");
    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

        // Handle user input
        movementManager.handleInput();

        camera.update(); // Update the camera


        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteBatch().begin(); // Important to call this before drawing anything


        // Draw the character
        background.render(game.getSpriteBatch());
        gameMap.render(game.getSpriteBatch());

        character.render(game.getSpriteBatch());


        game.getSpriteBatch().end(); // Important to call this after drawing everything
        hud.stage.act(delta);
        hud.stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
        gamePort.update(width,height); //Mario
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
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