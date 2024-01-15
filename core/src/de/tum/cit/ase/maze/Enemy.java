package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy extends GameObject{
    // Additional attributes to handle animation
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 12; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 8;

    //movement
    private static final float MOVE_INTERVAL = 1.0f; // Time interval in seconds for each move
    private float moveTimer = MOVE_INTERVAL;



    public Enemy(float x, float y, String texturePath) {
        super(x, y, texturePath);

        // Split the sprite sheet into individual frames
        Texture basicSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] tmp = TextureRegion.split(basicSheet,
                basicSheet.getWidth() / FRAME_COLS,
                basicSheet.getHeight() / FRAME_ROWS);

        //ghost is enemy
        currentFrame = tmp[5][7];
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, super.getX()*16, super.getY()*16);
    } //tileSize

    public void render(SpriteBatch batch, float x, float y) {
        batch.enableBlending();
        batch.draw(currentFrame, x, y);
    }

    //enemy movement
    public void update(float deltaTime, GameMap gameMap) {
        moveTimer -= deltaTime;
        if (moveTimer <= 0) {
            moveRandomly(gameMap);
            moveTimer = MOVE_INTERVAL;
        }
    }

    private void moveRandomly(GameMap gameMap) {
        int direction = (int) (Math.random() * 4); // Random direction: 0-3
        float newX = getX(), newY = getY();
        switch (direction) {
            case 0: newY++; break; // Up
            case 1: newY--; break; // Down
            case 2: newX--; break; // Left
            case 3: newX++; break; // Right
        }

        // Check if new position is valid
        if (!gameMap.isCellBlocked(newX*16, newY*16)) { //tileSize //custom for character!!
            setX(newX);
            setY(newY);
        }
    }


}