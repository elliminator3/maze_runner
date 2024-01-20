package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Rectangle;

public class Key extends GameObject {

    // Additional attributes to handle animation
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 33; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 20; // Number of rows in the sprite sheet
    // Add a bounding rectangle for collision detection
    private Rectangle boundingRectangle;
    private boolean isCollected = false;

private Character character;

    public Key(float x, float y, String texturePath) {
        super(x, y, texturePath);
        // Split the sprite sheet into individual frames
        Texture basicSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] tmp = TextureRegion.split(basicSheet,
                basicSheet.getWidth() / FRAME_COLS,
                basicSheet.getHeight() / FRAME_ROWS);
        //key is nine (looks a bit like a key)
        currentFrame = tmp[16][5];
        boundingRectangle = new Rectangle(x, y, getWidth(), getHeight());
    }

    // Add this method to get the width of the key
    public float getWidth() {
        return currentFrame.getRegionWidth();
    }

    // Add this method to get the height of the key
    public float getHeight() {
        return currentFrame.getRegionHeight();
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public boolean isCollected() {
        return isCollected;
    }

    // Add a method to collect the key
    public void collect(GameMap gameMap) {
        // Remove the key from the map's gameObjects array
        isCollected = true;
        gameMap.removeKey(this);
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
}