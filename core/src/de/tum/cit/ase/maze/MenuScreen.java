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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private Texture backgroundTexture;
    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        var camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements
        backgroundTexture = new Texture(Gdx.files.internal("Menu3.gif")); // Load the background image
        Image backgroundImage = new Image(backgroundTexture); // Create an Image actor for the background
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Set size to screen dimensions
        stage.addActor(backgroundImage); // Add the background image to the stage


        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage
        Gdx.input.setInputProcessor(stage);
        // Add a label as a title
        table.add(new Label("Welcome to Maze Runner!", game.getSkin(), "title")).padBottom(80).row();

        // Create and add a button to go to the game screen
        TextButton goToGameButton = new TextButton("New Game", game.getSkin());
        table.add(goToGameButton).width(500).row();
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame(); // Change to the game screen when button is pressed
            }
        });

        //load map button //fileChooser
        TextButton loadMapButton = new TextButton("Load Map", game.getSkin());
        table.add(loadMapButton).width(500).row(); // Adjust the width as necessary
        loadMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.chooseMazeFile(); // Open the file chooser when the button is clicked
            }
        });


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

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
        stage.getCamera().update();
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
        backgroundTexture.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}