package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.graphics.g2d.Animation;

import javax.swing.plaf.nimbus.State;
import java.awt.*;

public class Character extends GameObject{
    private int lives;
    private boolean hasKey;
    private GameObject gameObject;
    private float width;
    private float height;
    private Key key;
    private Rectangle boundingRectangle;
    private Animation<TextureRegion> upAnimation, downAnimation, leftAnimation, rightAnimation;
    private Animation<TextureRegion> currentAnimation;

    private float stateTime;
    private float speed = 60;




    // Additional attributes to handle animations
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 16; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 8; // Number of rows in the sprite sheet

    public Character(float x, float y, String texturePath, int lives) {
        super(x, y, texturePath);
        this.lives = lives;
        this.hasKey = false;
        this.key = null;

        //boundingRectangle = new Rectangle(x, y, getWidth(), getHeight());

        // Split the sprite sheet into individual frames
        Texture characterSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] tmp = TextureRegion.split(characterSheet,
                characterSheet.getWidth() / FRAME_COLS,
                characterSheet.getHeight() / FRAME_ROWS);

        // Assuming you want the first frame of the first animation row
        upAnimation = new Animation<>(0.1f, tmp[2]); // Assuming the idle frame for 'up' is the first frame of the third row
        downAnimation = new Animation<>(0.1f, tmp[0]);
        leftAnimation = new Animation<>(0.1f, tmp[3]);
        rightAnimation = new Animation<>(0.1f, tmp[1]);
        currentAnimation = downAnimation;

        stateTime = 0f;
    }

    //specifies how to draw the character on the screen using a SpriteBatch
    @Override
    public void render(SpriteBatch batch) {
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);  // Get current frame based on the state time
        batch.draw(currentFrame, getX(), getY());  // Draw at character's current position
    }

    @Override
    public void render(SpriteBatch batch, float x, float y) {
        //not needed?
    }

    public void updateAnimationStateTime(float deltaTime) {
        stateTime += deltaTime;
    }

    // Movement methods, collusion missing?
    public void moveUp() {
        setY(getY() + speed * Gdx.graphics.getDeltaTime());
        currentAnimation = upAnimation;
    }
    public void moveDown() {
        setY(getY() - speed * Gdx.graphics.getDeltaTime());
        currentAnimation = downAnimation;
    }
    public void moveRight() {
        setX(getX() + speed * Gdx.graphics.getDeltaTime());
        currentAnimation = rightAnimation;
    }
    public void moveLeft() {
        setX(getX() - speed * Gdx.graphics.getDeltaTime());
        currentAnimation = leftAnimation;

    }

    public void resetAnimationStateTime() {
        stateTime = 0f;
    }

    public int getLives() {
        return lives;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    public void setPosition(int x, int y) {
        super.setX(x);
        super.setY(y);
    }

    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }
    /*
    public void update(float dt) {
        // Other character update code
        boundingRectangle.setPosition(getX(), getY());

        // Check if the character has collected the key
        if (!hasKey && getBoundingRectangle().overlaps(key.getBoundingRectangle())) {
            hasKey = true;
            key.collect(); // Assuming you have a method to collect the key
            key = null;
              }
    }*/
}