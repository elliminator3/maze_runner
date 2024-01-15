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

import java.awt.*;

public class Character extends GameObject{
    private int lives;
    private boolean hasKey;
    private GameObject gameObject;
    private float width;
    private float height;
    private Key key;
    private Rectangle boundingRectangle;


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
        currentFrame = tmp[0][0];
    }

    //specifies how to draw the character on the screen using a SpriteBatch
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, super.getX(), super.getY());
    }

    @Override
    public void render(SpriteBatch batch, float x, float y) {
        //not needed?
    }

    //Movement, missing: collusion detection and continuous movement
    public void moveUp(){
        super.setY(super.getY()+1);
    }
    public void moveDown(){super.setY(super.getY()-1);}
    public void moveRight(){
        super.setX(super.getX()+1);
    }
    public void moveLeft(){
        super.setX(super.getX()-1);
    }

    public int getLives() {
        return lives;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void setLives(int lives) {
        this.lives = lives;
        //zum Testen ob error kommt, wenn drei Leben verlohren werden
        /*if(getLives()<=0){
            throw new RuntimeException("no lives left");
        }*/
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