package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Key extends GameObject{

    // Additional attributes to handle animation
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 33; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 20; // Number of rows in the sprite sheet
    public Key(float x, float y, String texturePath) {
        super(x, y, texturePath);
        // Split the sprite sheet into individual frames
        Texture basicSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] tmp = TextureRegion.split(basicSheet,
                basicSheet.getWidth() / FRAME_COLS,
                basicSheet.getHeight() / FRAME_ROWS);
        //key is nine (looks a bit like a key)
        currentFrame = tmp[16][5];
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, super.getX(), super.getY());
    }
    public void render(SpriteBatch batch, float x, float y) {
        batch.draw(currentFrame, x, y);
    }
} //10.01.
