package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Represents a key object within the game. This class extends {@link GameObject} and is used to render key
 * elements on the game map using a specific frame from a sprite sheet.
 * The key can be collected by the player to unlock exits and win the game.
 */
public class Key extends GameObject {
    // Attributes for handling the animation frame
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 33; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 20; // Number of rows in the sprite sheet

    // Additional attributes for key specific functionality
    private Rectangle boundingRectangle; // Bounding rectangle for collision detection
    private boolean isCollected = false; // Flag to check if the key has been collected

    /**
     * Constructs a new Key object with a specific position, texture path, and texture manager.
     * It selects a specific frame from the sprite sheet to represent the key.
     *
     * @param x              The x-coordinate of the key's location on the game map.
     * @param y              The y-coordinate of the key's location on the game map.
     * @param texturePath    The path to the texture file used for rendering the key.
     * @param textureManager The texture manager responsible for loading and managing game textures.
     */
    public Key(float x, float y, String texturePath, TextureManager textureManager) {
        super(x, y, texturePath, textureManager);
        // Split the sprite sheet into individual frames
        TextureRegion[][] tmp = TextureRegion.split(texture,
                texture.getWidth() / FRAME_COLS,
                texture.getHeight() / FRAME_ROWS);
        // Key is nine (looks a bit like a key)
        currentFrame = tmp[16][5];
        boundingRectangle = new Rectangle(x, y, getWidth(), getHeight());
    }

    /**
     * Collects the key, marking it as collected and removing it from the game map.
     * This method should be called when the player character intersects with the key's bounding rectangle.
     *
     * @param gameMap The {@link GameMap} object from which the key should be removed.
     */
    // Add a method to collect the key
    public void collect(GameMap gameMap) {
        // Remove the key from the map's gameObjects array
        isCollected = true;
        gameMap.removeKey(this);
    }

    /**
     * Renders the key at its stored location if it has not been collected.
     *
     * @param batch The {@link SpriteBatch} used for drawing the texture region on the screen.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (!isCollected) {
            batch.draw(currentFrame, super.getX(), super.getY());
        }
    }

    /**
     * Renders the key at a specified position. This allows for the key to be drawn
     * at a different location than its stored position, useful for effects or UI elements.
     *
     * @param batch The {@link SpriteBatch} used for drawing the texture region.
     * @param x     The x-coordinate where the key should be rendered.
     * @param y     The y-coordinate where the key should be rendered.
     */
    public void render(SpriteBatch batch, float x, float y) {
        batch.draw(currentFrame, x, y);
    }

    //Getter and setter
    /**
     * Gets the width of the current frame representing the key.
     *
     * @return The width of the key's texture region.
     */
    public float getWidth() {
        return currentFrame.getRegionWidth();
    }

    /**
     * Gets the height of the current frame representing the key.
     *
     * @return The height of the key's texture region.
     */
    public float getHeight() {
        return currentFrame.getRegionHeight();
    }

    /**
     * Gets the bounding rectangle for collision detection.
     *
     * @return The {@link Rectangle} object representing the key's bounding box.
     */
    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    /**
     * Checks if the key has been collected.
     *
     * @return {@code true} if the key has been collected, {@code false} otherwise.
     */
    public boolean isCollected() {
        return isCollected;
    }

}