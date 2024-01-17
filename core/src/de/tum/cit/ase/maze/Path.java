package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.IOException;
import java.util.Properties;

public class Path extends GameObject{
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 8; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 15; // Number of rows in the sprite sheet
    public Path(float x, float y, String texturePath) {
        super(x, y, texturePath);
        // Split the sprite sheet into individual frames
        Texture basicSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] tmp = TextureRegion.split(basicSheet,
                basicSheet.getWidth() / FRAME_COLS,
                basicSheet.getHeight() / FRAME_ROWS);

        //grass with flowers is path
        currentFrame = tmp[1][4];
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, super.getX(), super.getY());
    }


    public void render(SpriteBatch batch, float x, float y) {
        batch.draw(currentFrame, x, y);
    }
}
