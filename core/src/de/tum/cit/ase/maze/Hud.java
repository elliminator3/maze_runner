
package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import static com.badlogic.gdx.Gdx.files;

public class Hud {
    public Stage stage;
    private Viewport viewport;
    private Integer worldTimer;
    private float timeCount;
    private Integer score;
    private boolean isGameOver;
    private Table gameOverTable;
    private Label gameOverLabel;
    private boolean isWin;
    private Table winTable;
    private Label winLabel;
    private TextureRegion fullHeart;
    private TextureRegion emptyHeart;
    private Texture objectsTexture;
    private Table table;
    private Table heartTable;
    private boolean isTimerPaused;
    private boolean gameOverSoundPlayed = false;
    private Character character; // Pass the Character object to the Hud
    private GameMap maze;
    private MazeRunnerGame game;
    private MovementManager movementManager;
    private Image keyImage; // Image for the collected key
    private TextureRegion keyGraphic; // Texture for the key graphic
    private Image blackBar;

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;



    public Hud(SpriteBatch sb, Character character, MazeRunnerGame game) {
        worldTimer = 200;
        timeCount = 0;
        score = 5;
        viewport = new ScreenViewport(new OrthographicCamera());
        //new FitViewport(MazeRunnerGame.V_WIDTH, MazeRunnerGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
        isGameOver = false;
        isWin = false;
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

        fullHeart = new TextureRegion(objectsTexture, 64, 0, 16, 16);
        emptyHeart = new TextureRegion(objectsTexture, 128, 0, 16, 16);

        heartTable = new Table();
        heartTable.center().top(); // Position the heart table at the top center
        heartTable.setFillParent(true);
        updateHearts(score);

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.3f); // Scale to 50% of original size

// Create a LabelStyle with the scaled font
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

// Create the Label with the custom style
        timeLabel = new Label(" TIME ", labelStyle);
        countdownLabel = new Label(String.format("%03d", worldTimer), labelStyle);
        scoreLabel = new Label(String.format("%02d", score), labelStyle);


        table.add(timeLabel).padTop(1).padRight(20);
        table.row();
        table.add(countdownLabel).padTop(1).padRight(20);


        gameOverTable = new Table();
        gameOverTable.setFillParent(true); // Make the table fill the stage
        gameOverTable.center(); // Center contents in the table
// Create a Pixmap, color it red, and then create a Texture from it
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

        winTable = new Table();
        winTable.setFillParent(true); // Make the table fill the stage
        winTable.center(); // Center contents in the table
// Create a Pixmap, color it blue, and then create a Texture from it
        Pixmap pixmapwin = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapwin.setColor(Color.BLUE);
        pixmapwin.fill();
        Texture blueTexture = new Texture(pixmapwin); // don't forget to dispose of this later
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
        keyGraphic = new TextureRegion(objectsTexture, 80, 256, 16, 16); // Replace with actual coordinates and size

        // Initialize the key image but keep it hidden initially
        keyImage = new Image(keyGraphic);
        keyImage.setVisible(false);
        keyImage.setScale(1.5f);
        keyImage.setPosition(10, stage.getHeight() - keyImage.getHeight()-20); // Position at the upper left corner

        stage.addActor(blackBar);

        stage.addActor(heartTable);
        stage.addActor(keyImage);
        stage.addActor(gameOverTable);
        stage.addActor(winTable);
        stage.addActor(table);
    }


    public void pauseTimer(){
        isTimerPaused = true;
    }

    public void resumeTimer(){
        isTimerPaused = false;
    }
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
    public void showGameOverScreen() {
        if (!gameOverSoundPlayed) {
            game.playGameOverSound(); // Play the sound
            gameOverSoundPlayed = true; // Set the flag to true
        }
        isGameOver = true;
        gameOverTable.setVisible(true);
    }
    public void showWinScreen() {
        isWin = true;
        game.stopBackgroundMusic();
        game.playWinMusic();
        winTable.setVisible(true);
    }

    public void showKeyCollected() {
        keyImage.setVisible(true);
    }

    public int setScore(int newScore) {
        score = newScore;
        updateHearts(score); // Update hearts display whenever the score changes
        return newScore;
    }

    public void updateHearts(int health) {
        heartTable.clear();
        for (int i = 0; i < health; i++) {
            //Drawable heartDrawable = new TextureRegionDrawable(fullHeart);
            //Image heartImage = new Image(heartDrawable);
            Image fullHeartImage = new Image(fullHeart);
            fullHeartImage.setScale(2f);
            heartTable.add(fullHeartImage).padTop(20).padRight(5);
        }
        for (int i = health; i < 5; i++) {
            //Drawable heartDrawable = new TextureRegionDrawable(emptyHeart);
            //Image heartImage = new Image(heartDrawable);
            Image emptyHeartImage = new Image(emptyHeart);
            emptyHeartImage.setScale(2f);
            heartTable.add(emptyHeartImage).padTop(20).padRight(5);
        }
        stage.draw();
    }


    public boolean isGameOver(){
        return isGameOver;
    }
    public boolean isWin(){
        return isWin;
    }
    public void dispose() {
        objectsTexture.dispose();
        stage.dispose();}

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
    public void updatePositions() {
        //update size and position of the background
        blackBar.setSize(stage.getWidth(), 50);
        blackBar.setPosition(0, stage.getHeight() - 50);
        // Adjust the position of the key image
        keyImage.setPosition(10, stage.getHeight() - keyImage.getHeight() - 20);
    }
}