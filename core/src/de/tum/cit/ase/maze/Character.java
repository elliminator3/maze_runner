package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Character extends GameObject{
    private int lives;
    private boolean hasKey;

    // Additional attributes to handle animations
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 16; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 7; // Number of rows in the sprite sheet

    public Character(float x, float y, String texturePath, int lives) {
        super(x, y, texturePath);
        this.lives = lives;
        this.hasKey = false;

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
        batch.draw(currentFrame, super.getX(), super.getY());
    }

    //Movement, missing: collusion detection and continuous movement
    public void moveUp(){
        super.setY(super.getY()+1);
    }
    public void moveDown(){
        super.setY(super.getY()-1);
    }
    public void moveRight(){
        super.setX(super.getX()+1);
    }
    public void moveLeft(){
        super.setX(super.getX()-1);
    }

} //10.01.
