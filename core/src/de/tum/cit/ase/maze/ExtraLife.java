package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Represents an extra life object within the game. An extra life can be collected by the player's character to increase their life count.
 * This class extends {@link GameObject} and manages the extra life's texture, state (collected or not), and rendering.
 */
public class ExtraLife extends GameObject {

    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 33; // Adjust as per your sprite sheet
    private static final int FRAME_ROWS = 20; // Adjust as per your sprite sheet
    private Rectangle boundingRectangle;
    private boolean isCollected = false;
    private static final String EXTRA_LIFE_TEXTURE_PATH = "objects.png"; // Update with actual path

    /**
     * Constructs an ExtraLife object at the specified location.
     *
     * @param x              The x-coordinate where the extra life will be placed.
     * @param y              The y-coordinate where the extra life will be placed.
     * @param textureManager The {@link TextureManager} instance to manage game textures.
     */
    public ExtraLife(float x, float y, TextureManager textureManager) {
        super(x, y, EXTRA_LIFE_TEXTURE_PATH, textureManager);
        Texture extraLifeSheet = new Texture(Gdx.files.internal(EXTRA_LIFE_TEXTURE_PATH));
        TextureRegion[][] tmp = TextureRegion.split(extraLifeSheet, extraLifeSheet.getWidth() / FRAME_COLS, extraLifeSheet.getHeight() / FRAME_ROWS);
        currentFrame = tmp[0][4]; // Assuming the extra life is at the first row and fifth column
        boundingRectangle = new Rectangle(x, y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }

    /**
     * Marks the extra life as collected, changing its state to prevent it from being rendered or collected again.
     */
    public void collect() {
        this.isCollected = true;
    }

    /**
     * Checks whether the extra life has been collected.
     *
     * @return {@code true} if the extra life has been collected, {@code false} otherwise.
     */
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Renders the extra life object on the screen if it has not been collected.
     *
     * @param batch The {@link SpriteBatch} used for drawing the object.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (!isCollected) {
            batch.draw(currentFrame, super.getX(), super.getY());
        }
    }

    /**
     * Renders the extra life object at the specified coordinates. This can be used to render the extra life at a different location than its current position.
     *
     * @param batch The {@link SpriteBatch} used for drawing the object.
     * @param x     The x-coordinate where the extra life will be drawn.
     * @param y     The y-coordinate where the extra life will be drawn.
     */
    public void render(SpriteBatch batch, float x, float y) {
        batch.draw(currentFrame, x, y);
    }


}
