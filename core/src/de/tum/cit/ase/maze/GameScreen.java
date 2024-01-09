package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;


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
    private static final float CHARACTER_WIDTH = 64; // Example width
    private static final float CHARACTER_HEIGHT = 128; // Example height

    private CustomMapRenderer customMapRenderer;
    private int[][] loadedMap;
    private Tileset tileset; // The Tileset class instance
    private static final int TILE_SIZE = 16; // The size of each tile

    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;

        loadedMap = MapLoader.loadMap("maps/level-1.properties");
        tileset = new Tileset("basictiles.png", TILE_SIZE, TILE_SIZE);
        customMapRenderer = new CustomMapRenderer(loadedMap, tileset); // Corrected to use MapRenderer

        // Initialize your character and movement manager here
        character = new Character(0, 0, "character.png", 3);
        movementManager = new MovementManager(character);

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

        // Move text in a circular path to have an example of a moving object
        sinusInput += delta;
        float textX = (float) (camera.position.x + Math.sin(sinusInput) * 100);
        float textY = (float) (camera.position.y + Math.cos(sinusInput) * 100);

        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);

        game.getSpriteBatch().begin(); // Important to call this before drawing anything
        // Render the text
        font.draw(game.getSpriteBatch(), "Press ESC to go to menu", textX, textY);



        // Render the map
        customMapRenderer.render(game.getSpriteBatch());
        // Draw the character
        character.render(game.getSpriteBatch());


        /* Draw the character next to the text :) / We can reuse sinusInput here
        game.getSpriteBatch().draw(
                game.getCharacterDownAnimation().getKeyFrame(sinusInput, true),
                textX - 96,
                textY - 64,
                64,
                128
        );*/

        game.getSpriteBatch().end(); // Important to call this after drawing everything
    }




    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
       loadedMap = MapLoader.loadMap("maps/level-1.properties");
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        //dispose assets like textures when you're done with them
        character.getTexture().dispose();
        tileset.dispose();
    }

    // Additional methods and logic can be added as needed for the game screen
}
