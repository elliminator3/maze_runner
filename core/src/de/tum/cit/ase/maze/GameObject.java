package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameObject {
    private float x;
    private float y;
    protected Texture texture; //represents an image loaded into memory that can be drawn or rendered onto the screen

    public GameObject(float x, float y, String texturePath) {
        this.x = x;
        this.y = y;
        this.texture = new Texture(Gdx.files.internal(texturePath));
    }

    //integral to drawing (visually representing) each game object on the screen
    public abstract void render(SpriteBatch batch);
    public abstract void render(SpriteBatch batch, float x, float y);

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

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
