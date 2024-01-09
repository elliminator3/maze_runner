package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Character extends GameObject{
    private int lives;
    private boolean hasKey;

    public Character(float x, float y, String texturePath, int lives) {
        super(x, y, texturePath);
        this.lives = lives;
        this.hasKey = false;
    }

    //specifies how to draw the character on the screen using a SpriteBatch
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, super.getX(), super.getY());
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

}
