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
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.MazeRunnerGame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import java.awt.*;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
    private TextureRegion fullHeart;
    private TextureRegion emptyHeart;
    private Texture objectsTexture;
    private Table table;
    private Table heartTable;
private boolean isTimerPaused;
private boolean hasKey;
    private Character character; // Pass the Character object to the Hud
    private Key key;

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;



    public Hud(SpriteBatch sb) {
        worldTimer = 300;
        timeCount = 0;
        score = 1;
        viewport = new FitViewport(MazeRunnerGame.V_WIDTH, MazeRunnerGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);
        isGameOver = false;
        hasKey = false;
        this.character = character;
        this.key = key;

        table = new Table();
        table.right().top();
        table.setFillParent(true);


        objectsTexture = new Texture(files.internal("assets/objects.png"));

        fullHeart = new TextureRegion(objectsTexture, 64, 0, 16, 16); // Replace with actual coordinates and size
        emptyHeart = new TextureRegion(objectsTexture, 128, 0, 16, 16); // Replace with actual coordinates and size

        heartTable = new Table();
       heartTable.center().top(); // Position the heart table at the top center
        heartTable.setFillParent(true);
        updateHearts(score);

        BitmapFont font = new BitmapFont();
        font.getData().setScale(0.5f); // Scale to 50% of original size

// Create a LabelStyle with the scaled font
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

// Create the Label with the custom style
        timeLabel = new Label(" T I M E ", labelStyle);
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

        stage.addActor(heartTable);
        stage.addActor(gameOverTable);
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
                    gameOverTable.setVisible(true);
                }
            }
        }

        /*
        if (!hasKey && character.getBoundingRectangle().overlaps(key.getBoundingRectangle())) {
            hasKey = true;
            // Optionally, you can perform additional actions when the key is obtained
        } */
    }

    public void setScore(int newScore) {
        score = newScore;
        updateHearts(score); // Update hearts display whenever the score changes
    }
    public void updateHearts(int health) {
        heartTable.clear();
        for (int i = 0; i < 5; i++) {
            // For each heart, check if it should be full or empty
            Drawable heartDrawable = i < health ? new TextureRegionDrawable(fullHeart) : new TextureRegionDrawable(emptyHeart);
            Image heartImage = new Image(heartDrawable);
            heartTable.add(heartImage).padTop(2);
        }
        stage.draw();
    }



    public boolean isGameOver(){
        return isGameOver;
    }

 //wofür genau brauche ich das?
    public void dispose() {
        objectsTexture.dispose();
        stage.dispose();}
}
