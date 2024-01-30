package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a trap in the game. This class extends {@link GameObject} and is used to render trap
 * elements on the game map using a specific frame from a sprite sheet.
 */
public class Trap extends GameObject{
    // Additional attributes to handle animation
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 8; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 15; // Number of rows in the sprite sheet

    /**
     * Constructs a new Trap object with a specific position, texture path, and texture manager.
     * It selects a specific frame from the sprite sheet to represent the trap.
     *
     * @param x              The x-coordinate of the trap's position.
     * @param y              The y-coordinate of the trap's position.
     * @param texturePath    The file path to the texture image.
     * @param textureManager The texture manager for handling game textures.
     */
    public Trap(float x, float y, String texturePath, TextureManager textureManager) {
        super(x, y, texturePath, textureManager);
        // Split the sprite sheet into individual frames
        TextureRegion[][] tmp = TextureRegion.split(texture,
                texture.getWidth() / FRAME_COLS,
                texture.getHeight() / FRAME_ROWS);

        // Spring is trap
        currentFrame = tmp[3][7];
    }

    /**
     * Renders the trap at its position.
     *
     * @param batch The {@link SpriteBatch} used for drawing the texture region.
     */
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, super.getX(), super.getY());
    }

    /**
     * Renders the trap at a specified position. This allows for the trap to be drawn
     * at a different location than its stored position, useful for effects or UI elements.
     *
     * @param batch The {@link SpriteBatch} used for drawing the texture region.
     * @param x     The x-coordinate where the trap should be rendered.
     * @param y     The y-coordinate where the trap should be rendered.
     */
    public void render(SpriteBatch batch, float x, float y) {
        batch.enableBlending();
        batch.draw(currentFrame, x, y);
    }

}
