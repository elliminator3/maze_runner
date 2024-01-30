
package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import static com.badlogic.gdx.Gdx.files;

/**
 * The {@code Hud} class represents the heads-up display (HUD) of the game,
 * which includes information such as the timer, character lives, the collected key and game state indicators like game over or win screens.
 * It manages the on-screen stage and its visual elements to provide feedback to the player about their progress and status.
 */

public class Hud {
    public Stage stage;
    private Viewport viewport;
    private Integer worldTimer;
    private float timeCount;
    private Integer score;
    private boolean isGameOver;
    private Table gameOverTable;
    private Label gameOverLabel;
    private Table winTable;
    private Character character; // Pass the Character object to the Hud

    private Label winLabel;
    private TextureRegion fullHeart;
    private TextureRegion emptyHeart;
    private Texture objectsTexture;
    private Table table;
    private Table heartTable;
    private boolean isTimerPaused;
    private boolean gameOverSoundPlayed = false;
    private MazeRunnerGame game;
    private Image keyImage; // Image for the collected key
    private TextureRegion keyGraphic; // Texture for the key graphic
    private Image blackBar;

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;

    /**
     * Constructs a new Hud instance, which sets up the game's HUD, including the timer, collected key,
     * and heart indicators, as well as the game over and win messages.
     *
     * @param sb    the {@link SpriteBatch} used for drawing the HUD elements
     * @param character the {@link Character} whose stats are displayed in the HUD
     * @param game  the instance of the {@link MazeRunnerGame} this HUD is associated with
     *
     * The HUD uses {@link Pixmap} to create textures for background bars and tables. It also
     * utilizes assets like the heart images and key graphics from the game's texture atlas.
     * Labels are styled with {@link BitmapFont} and scaled accordingly to fit the HUD's design.</p>
     *
     * Actors are added to the stage to be displayed during the game. The stage is set with a
     * {@link ScreenViewport} based on the {@link OrthographicCamera}.</p>
     */

    public Hud(SpriteBatch sb, Character character, MazeRunnerGame game) {
        worldTimer = 200;
        timeCount = 0;
        score = 5;
        viewport = new ScreenViewport(new OrthographicCamera());
        stage = new Stage(viewport, sb);
        isGameOver = false;
        this.character = character;
        this.game = game;
        table = new Table();
        table.right().top();
        table.setFillParent(true);

        //Pixmap for hud background
        Pixmap pixmapBackground = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmapBackground.setColor(new Color(0, 0, 0, 0.8f));
        pixmapBackground.fill();

        //Texture from Pixmap
        Texture backgroundTexture = new Texture(pixmapBackground);
        pixmapBackground.dispose(); // Dispose pixmap as it's no longer needed
        Drawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        blackBar = new Image(backgroundDrawable);
        blackBar.setSize(stage.getWidth(), 50); // Set the desired height
        blackBar.setPosition(0, stage.getHeight() - 50); // Position at the top of the stage
        blackBar.setColor(1, 1, 1, 0.8f);

        objectsTexture = new Texture(files.internal("assets/objects.png"));

        // Assign heart graphics
        fullHeart = new TextureRegion(objectsTexture, 64, 0, 16, 16);
        emptyHeart = new TextureRegion(objectsTexture, 128, 0, 16, 16);

        // Create heartTable to display health score of character
        heartTable = new Table();
        heartTable.center().top(); // Position the heart table at the top center
        heartTable.setFillParent(true);
        updateHearts(score);

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.3f); // Scale to 50% of original size

        // Create a LabelStyle with the scaled font
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        // Create the time Label, countdown Label and score Label with custom style
        timeLabel = new Label(" TIME ", labelStyle);
        countdownLabel = new Label(String.format("%03d", worldTimer), labelStyle);
        scoreLabel = new Label(String.format("%02d", score), labelStyle);


        table.add(timeLabel).padTop(1).padRight(20);
        table.row();
        table.add(countdownLabel).padTop(1).padRight(20);

        //Create
        gameOverTable = new Table();
        gameOverTable.setFillParent(true); // Make the table fill the stage
        gameOverTable.center(); // Center contents in the table

        // Create a Pixmap for GameOver Screen, color it red, and then create a Texture from it
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.RED);
        pixmap.fill();
        Texture redTexture = new Texture(pixmap); // don't forget to dispose of this later
        pixmap.dispose(); // Dispose pixmap as it's no longer needed

        Drawable redBackground = new TextureRegionDrawable(new TextureRegion(redTexture));
        gameOverTable.setBackground(redBackground);
        gameOverTable.setBackground(redBackground);
        Label.LabelStyle largeLabelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        largeLabelStyle.font.getData().setScale(2); // Make the font larger
        gameOverLabel = new Label("GAME OVER", largeLabelStyle);
        gameOverTable.add(gameOverLabel).expand().center();
        gameOverTable.setVisible(false); // Initially hidden

        // Table for win screen
        winTable = new Table();
        winTable.setFillParent(true); // Make the table fill the stage
        winTable.center(); // Center contents in the table

        // Create a Pixmap, color it blue, and then create a Texture from it
        Pixmap pixmapwin = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapwin.setColor(Color.BLUE);
        pixmapwin.fill();
        Texture blueTexture = new Texture(pixmapwin);
        pixmapwin.dispose(); // Dispose pixmap as it's no longer needed
        Drawable blueBackground = new TextureRegionDrawable(new TextureRegion(blueTexture));
        winTable.setBackground(blueBackground);
        winTable.setBackground(blueBackground);
        Label.LabelStyle largeLabelStyleWin = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        largeLabelStyleWin.font.getData().setScale(2); // Make the font larger
        winLabel = new Label("YOU WIN", largeLabelStyleWin);
        winTable.add(winLabel).expand().center();
        winTable.setVisible(false); // Initially hidden

        //key is nine (looks a bit like a key)
        keyGraphic = new TextureRegion(objectsTexture, 80, 256, 16, 16);
        // Initialize the key image but keep it hidden initially
        keyImage = new Image(keyGraphic);
        keyImage.setVisible(false);
        keyImage.setScale(2.4f);
        keyImage.setPosition(10, stage.getHeight() - keyImage.getHeight()-25); // Position at the upper left corner

        // Adds all elements to the stage.
        stage.addActor(blackBar);
        stage.addActor(heartTable);
        stage.addActor(keyImage);
        stage.addActor(gameOverTable);
        stage.addActor(winTable);
        stage.addActor(table);
    }

    /**
     * Pauses the countdown timer of the HUD.
     */
    public void pauseTimer(){
        isTimerPaused = true;
    }

    /**
     * Resumes the countdown timer of the HUD.
     */
    public void resumeTimer(){
        isTimerPaused = false;
    }

    /**
     * Updates the HUD's timer and checks for game over conditions. If the timer reaches 0 for example the gameOver screen will be shown.
     * This method should be called every frame to ensure the HUD reflects the current game state.
     *
     * @param dt the time in seconds since the last frame
     */
    public void update(float dt) {
        if (!isGameOver && !isTimerPaused) {
            timeCount += dt;
            if (timeCount >= 1) {
                worldTimer--;
                countdownLabel.setText(String.format("%03d", worldTimer));
                timeCount = 0;

                if (worldTimer <= 0) {
                    isGameOver = true;
                    showGameOverScreen();
                }
            }
        }

    }

    /**
     * Displays the game over screen, stops the timer, and plays the game over sound.
     */
    public void showGameOverScreen() {
        if (!gameOverSoundPlayed) {
            game.playGameOverSound(); // Play the sound
            gameOverSoundPlayed = true; // Set the flag to true
        }
        isGameOver = true;
        gameOverTable.setVisible(true);
    }
    /**
     * Displays the win screen, stops the background music, pauses the timer, and plays the win music.
     */
    public void showWinScreen() {
        game.stopBackgroundMusic();
        isTimerPaused = true;
        game.playWinMusic();
        winTable.setVisible(true);
    }

    /**
     * Updates the HUD to show that the key has been collected by the player.
     */
    public void showKeyCollected() {
        keyImage.setVisible(true);
    }

    /**
     * Sets the new score and updates the heart display according to the new score.
     *
     * @param newScore the new score value
     * @return the updated score value
     */
    public int setScore(int newScore) {
        score = newScore;
        updateHearts(score); // Update hearts display whenever the score changes
        return newScore;
    }

    /**
     * Updates the heart display in the HUD based on the current health value.
     *
     * @param health the current health value to display
     */
    public void updateHearts(int health) {
        heartTable.clear();
        int displayedHealth = Math.min(health, 5);
        for (int i = 0; i < displayedHealth; i++) {
            Image fullHeartImage = new Image(fullHeart);
            fullHeartImage.setScale(2.2f);
            heartTable.add(fullHeartImage).padTop(25).padRight(5);
        }
        for (int i = displayedHealth; i < 5; i++) {
            Image emptyHeartImage = new Image(emptyHeart);
            emptyHeartImage.setScale(2.2f);
            heartTable.add(emptyHeartImage).padTop(25).padRight(5);
        }
        stage.draw();
    }

    /**
     * Returns whether the game is over.
     *
     * @return {@code true} if the game is over, {@code false} otherwise
     */
    public boolean isGameOver(){
        return isGameOver;
    }

    /**
     * Disposes of all assets used by the HUD to free up resources.
     */
    public void dispose() {
        objectsTexture.dispose();
        stage.dispose();}

    /**
     * Gets the viewport used by the HUD, which dictates the projection and size of the HUD elements.
     *
     * @return the viewport of the HUD
     */
    public Viewport getViewport() {
        return viewport;
    }


    /**
     * Updates the positions of HUD elements to reflect changes in the stage size or orientation.
     */
    public void updatePositions() {
        //update size and position of the background
        blackBar.setSize(stage.getWidth(), 50);
        blackBar.setPosition(0, stage.getHeight() - 50);
        // Adjust the position of the key image
        keyImage.setPosition(10, stage.getHeight() - keyImage.getHeight() - 20);
    }

    /**
     * Gets the current score displayed on the HUD.
     *
     * @return the current score
     */
    public Integer getScore() {
        return score;
    }
}