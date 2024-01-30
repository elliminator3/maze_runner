package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game. It is responsible for
 * rendering the menu UI, handling input events, and transitioning to other screens such as the game screen or map loader.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private Texture backgroundTexture;
    private Image backgroundImage;
    private OrthographicCamera camera; // Moved camera to a field
    private Viewport viewport; // Moved viewport to a field

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        camera = new OrthographicCamera(); // Initialization of camera with zoom and viewport with camera
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.1f; // Set camera zoom for a closer view

        viewport = new FillViewport(Gdx.graphics.getWidth()*1.25f, Gdx.graphics.getHeight()*1.25f, camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        // Background image setup to fit the menu screen
        backgroundTexture = new Texture(Gdx.files.internal("Menu3.gif")); // Load the background image
        backgroundImage = new Image(backgroundTexture); // Create an Image actor for the background
        backgroundImage.setFillParent(true);
        backgroundImage.setBounds(0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        backgroundImage.setScaling(Scaling.fill);

        stage.addActor(backgroundImage);  // Add the background image to the stage

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage
        Gdx.input.setInputProcessor(stage);
        // Add a label as a title
        table.add(new Label("Welcome to Maze Runner!", game.getSkin(), "title")).padBottom(80).row();


        //load map button //fileChooser
        TextButton loadMapButton = new TextButton("Load Map", game.getSkin());
        table.add(loadMapButton).width(500).row(); // Adjust the width as necessary
        loadMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.chooseMazeFile(); // Open the file chooser when the button is clicked
            }
        });

        // Create and add button to continue the game
        TextButton continueButton = new TextButton("Continue Game", game.getSkin());
        table.add(continueButton).width(500).row(); // makes button appear and sets width
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.resumeGame(); // Change to the game screen when button is pressed
            }
        });

        // Create and add button to exit the game
        TextButton exitButton = new TextButton("Exit", game.getSkin());
        table.add(exitButton).width(500).row(); // makes button appear and sets width
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); // Exit the application
            }
        });

    }

    /**
     * Renders the menu screen and updates its UI elements.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage

    }

    /**
     * Resizes the menu screen viewport in response to window size changes.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        backgroundImage.setSize(width,height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }

    /**
     * Disposes of the resources used by the menu screen to free up memory.
     */
    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
        backgroundTexture.dispose();
    }

    /**
     * Called when the menu screen becomes the current screen for a {@link com.badlogic.gdx.Game}.
     */
    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    /**
     * Pauses the menu screen. This method is called when the application is paused.
     */
    @Override
    public void pause() {
    }

    /**
     * Resumes the menu screen. This method is called when the application is resumed.
     */
    @Override
    public void resume() {
    }

    /**
     * Hides the menu screen. This method is called when the screen is no longer the current screen.
     */
    @Override
    public void hide() {
    }
}