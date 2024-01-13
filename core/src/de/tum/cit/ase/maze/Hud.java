package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.maze.MazeRunnerGame;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import java.awt.*;

public class Hud {
    public Stage stage;
    private Viewport viewport;
    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label mazeLabel;

    public Hud(SpriteBatch sb) {
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        viewport = new FitViewport(MazeRunnerGame.V_WIDTH, MazeRunnerGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);
        table.setBounds(10, 0, 5, 5);


        BitmapFont font = new BitmapFont();
        font.getData().setScale(0.5f); // Scale to 50% of original size

// Create a LabelStyle with the scaled font
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

// Create the Label with the custom style
        timeLabel = new Label(" TIME ", labelStyle);
        levelLabel = new Label("1-1", labelStyle);
        worldLabel = new Label(" WORLD ", labelStyle);
        mazeLabel = new Label(" MAZE ", labelStyle);

        countdownLabel = new Label(String.format("%03d", worldTimer), labelStyle);
        scoreLabel = new Label(String.format("%02d", score), labelStyle);



        table.add(mazeLabel).padTop(5); // Reduced top padding
        table.add(worldLabel).padTop(5);
        table.add(timeLabel).padTop(5);
        table.row();
        table.add(scoreLabel).padTop(1); // Reduced top padding for scores
        table.add(levelLabel).padTop(1);
        table.add(countdownLabel).padTop(1);

        stage.addActor(table);
    }
}
