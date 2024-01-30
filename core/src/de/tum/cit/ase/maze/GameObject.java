package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Represents a generic game object in the Maze Runner game. This class provides
 * basic properties and functionalities that all game objects share, such as position
 * and texture handling.
 */

public abstract class GameObject {
    private float x;
    private float y;

    protected Texture texture; //represents an image loaded into memory that can be drawn or rendered onto the screen

    /**
     * Constructs a new GameObject at the specified position with a texture. This constructor
     * initializes the object's coordinates and associates a texture from the texture manager
     * based on the provided texture path. It is intended for use by subclasses representing
     * different elements within the game.
     *
     * @param x              The x-coordinate of the object's position on the game map.
     * @param y              The y-coordinate of the object's position on the game map.
     * @param texturePath    The path to the texture file that visually represents this game object.
     *                       The path is used by the texture manager to load and store the texture
     *                       for efficient reuse.
     * @param textureManager The manager responsible for loading, storing, and providing textures
     *                       for game objects. This ensures that textures are managed efficiently
     *                       and are reused where possible, minimizing memory usage and load times.
     */
    public GameObject(float x, float y, String texturePath, TextureManager textureManager) {
        this.x = x;
        this.y = y;
        this.texture = textureManager.getTexture(texturePath);

    }

    /**
     * Renders this game object onto the screen.
     * @param batch The SpriteBatch used for drawing.
     */
    public abstract void render(SpriteBatch batch);

    /**
     * Renders this game object at a specific location on the screen.
     * This method allows rendering the object with a different position than the one defined by its x and y fields.
     * Used in GameMap's render(SpriteBatch batch) to draw the maze objects according to the .properties file while taking into account the tile size (16x16)
     *
     * @param batch The SpriteBatch that's used for drawing the texture.
     * @param x The x-coordinate where the game object should be rendered on the screen.
     * @param y The y-coordinate where the game object should be rendered on the screen.
     */
    public abstract void render(SpriteBatch batch, float x, float y);


    /**
     * Disposes of the texture when it is no longer needed.
     * This method should be called to free up resources and prevent memory leaks when the texture associated with the game object is no longer in use.
     */
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    // Getter and setter
    public float getX() { return x; }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() { return y;}

    public void setY(float y) {
        this.y = y;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

}