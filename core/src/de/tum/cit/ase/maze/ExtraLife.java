package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class ExtraLife extends GameObject {

    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 33; // Adjust as per your sprite sheet
    private static final int FRAME_ROWS = 20; // Adjust as per your sprite sheet
    private Rectangle boundingRectangle;
    private boolean isCollected = false;
    private static final String EXTRA_LIFE_TEXTURE_PATH = "objects.png"; // Update with actual path

    public ExtraLife(float x, float y) {
        super(x, y, EXTRA_LIFE_TEXTURE_PATH);
        Texture extraLifeSheet = new Texture(Gdx.files.internal(EXTRA_LIFE_TEXTURE_PATH));
        TextureRegion[][] tmp = TextureRegion.split(extraLifeSheet, extraLifeSheet.getWidth() / FRAME_COLS, extraLifeSheet.getHeight() / FRAME_ROWS);
        currentFrame = tmp[0][4]; // Assuming the extra life is at the first row and fifth column
        boundingRectangle = new Rectangle(x, y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }
    public void collect() {
        this.isCollected = true;
    }
    public boolean isCollected() {
        return isCollected;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!isCollected) {
            batch.draw(currentFrame, super.getX(), super.getY());
        }
    }

    public void render(SpriteBatch batch, float x, float y) {
        batch.draw(currentFrame, x, y);
    }



    // Additional methods like getters/setters, collision detection, etc.
}
