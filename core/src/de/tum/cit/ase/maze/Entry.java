package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents an entry in the game. This class extends {@link GameObject} and is used to render entry
 * elements on the game map using a specific frame from a sprite sheet.
 */
public class Entry extends GameObject{
    // Attributes for handling the animation frame
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 8; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 15; // Number of rows in the sprite sheet

    /**
     * Constructs a new Entry object with a specific position, texture path, and texture manager.
     * It selects a specific frame from the sprite sheet to represent the entry.
     *
     * @param x              The x-coordinate of the entry's position.
     * @param y              The y-coordinate of the entry's position.
     * @param texturePath    The file path to the texture image.
     * @param textureManager The texture manager for handling game textures.
     */
    public Entry(float x, float y, String texturePath, TextureManager textureManager) {
        super(x, y, texturePath, textureManager);
        // Split the sprite sheet into individual frames
        TextureRegion[][] tmp = TextureRegion.split(texture,
                texture.getWidth() / FRAME_COLS,
                texture.getHeight() / FRAME_ROWS);
        // Entry is grass
        currentFrame = tmp[1][3];
    }

    /**
     * Renders the entry at its position.
     *
     * @param batch The {@link SpriteBatch} used for drawing the texture region.
     */
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, super.getX(), super.getY());
    }

    /**
     * Renders the entry at a specified position. This allows for the entry to be drawn
     * at a different location than its stored position, useful for effects or UI elements.
     *
     * @param batch The {@link SpriteBatch} used for drawing the texture region.
     * @param x     The x-coordinate where the entry should be rendered.
     * @param y     The y-coordinate where the entry should be rendered.
     */
    public void render(SpriteBatch batch, float x, float y) {
        batch.draw(currentFrame, x, y);
    }

}