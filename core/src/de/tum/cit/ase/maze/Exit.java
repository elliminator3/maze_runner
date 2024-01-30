package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Exit extends GameObject{
    // Additional attributes to handle animation
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 12; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 8; // Number of rows in the sprite sheet
    public Exit(float x, float y, String texturePath, TextureManager textureManager) {
        super(x, y, texturePath, textureManager);
        // Split the sprite sheet into individual frames
        TextureRegion[][] tmp = TextureRegion.split(texture,
                texture.getWidth() / FRAME_COLS,
                texture.getHeight() / FRAME_ROWS);
        //grey door is exit
        currentFrame = tmp[0][3];
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, super.getX(), super.getY());
    }

    public void render(SpriteBatch batch, float x, float y) {
        batch.enableBlending();
        batch.draw(currentFrame, x, y);
    }

}