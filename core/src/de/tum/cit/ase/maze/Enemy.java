package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class Enemy extends GameObject {
    // Additional attributes to handle animation
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 12; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 8;

    //movement
    private static final float MOVE_INTERVAL = 3f; // Time interval in seconds for each move
    private float moveTimer = MOVE_INTERVAL;

    //new intelligent movement
    private List<Point> currentPath;
    private int pathIndex;


    public Enemy(float x, float y, String texturePath, TextureManager textureManager) {
        super(x, y, texturePath, textureManager);

        // Split the sprite sheet into individual frames
        Texture basicSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] tmp = TextureRegion.split(basicSheet,
                basicSheet.getWidth() / FRAME_COLS,
                basicSheet.getHeight() / FRAME_ROWS);

        //ghost is enemy
        currentFrame = tmp[5][7];
        this.currentPath = null;
        this.pathIndex = 0;
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, super.getX() * 16, super.getY() * 16);
    } //tileSize

    public void render(SpriteBatch batch, float x, float y) {
        batch.enableBlending();
        batch.draw(currentFrame, x, y);
    }

    //enemy movement
    public void update(float deltaTime, GameMap gameMap, Character character) {

        if (isCharInRange(character)) {
            moveTimer -= deltaTime;
            if (moveTimer <= 0) {
                //pathfinding algorithm with character center as endpoint
                currentPath = (List<Point>) AStarPathfinding.findPath(gameMap, new Point((int) getX(), (int) getY()),
                        new Point( ((int) (character.getX()/16+0.5f)) , ((int) (character.getY()/16+1))));
                pathIndex = 0;
                moveTimer = MOVE_INTERVAL;
            }
            followPath(deltaTime, gameMap);
        } else {
            moveTimer -= deltaTime;
            if (moveTimer <= 1) {
                moveRandomly(gameMap, deltaTime);
                moveTimer = MOVE_INTERVAL;
            }
        }

    }

    private void moveRandomly(GameMap gameMap, float deltaTime) {

        int direction = (int) (Math.random() * 4); // Random direction: 0-3
        float newX = getX(), newY = getY();
        switch (direction) {
            case 0:
                newY++;
                break; // Up
            case 1:
                newY--;
                break; // Down
            case 2:
                newX--;
                break; // Left
            case 3:
                newX++;
                break; // Right
        }
        //ToDo: make smoother
        /*// Only set the new target position if it's not blocked
        if (!gameMap.isCellBlocked(newX * 16, newY * 16)) { // Assuming tileSize is 16
            targetPosition = new Point((int)newX, (int)newY);
        }

        if(targetPosition != null){
            // Interpolate position
            float lerpFactor = deltaTime*5; // Adjust factor based on deltaTime and MOVE_INTERVAL
            setX(lerpX(getX(), targetPosition.x, lerpFactor));
            setY(lerpY(getY(), targetPosition.y, lerpFactor));
        }

            // Check if reached the next point (with some threshold)
            if (Math.abs(getX() - targetPosition.x) < 0.01f && Math.abs(getY() - targetPosition.y) < 0.01f) {
                setX(targetPosition.x); // Snap to the exact target position
                setY(targetPosition.y);
                targetPosition = null;
            }*/

        // Check if new position is valid
        if (!gameMap.isCellBlocked(newX * 16, newY * 16)) { //tileSize
            setX(newX);
            setY(newY);
        }
    }


    //new intelligent enemy movement
    private boolean isCharInRange(Character character) {
        int tileSize = 16; //size of our tiles
        float offsetX = (34 - tileSize) / 2f;
        float offsetY = (32 - tileSize) / 2f;

        // Calculate the offset to center the 16x16 collision box within the 34x32 sprite
        float tileX = (character.getX() + offsetX) / tileSize;
        float tileY = (character.getY() + offsetY) / tileSize;
        float distance = Vector2.dst(this.getX(), this.getY(), tileX, tileY);
        return distance <= 3; //only if just three fields away
    }

    private void followPath(float deltaTime, GameMap gameMap) {
        if (currentPath != null && pathIndex < currentPath.size()) {
            Point nextStep = currentPath.get(pathIndex);

            // Interpolate position
            if (!gameMap.isCellBlocked(nextStep.x * 16, nextStep.y * 16)){
                float lerpFactor = deltaTime * MOVE_INTERVAL; // Adjust factor based on deltaTime and MOVE_INTERVAL
                setX(lerpX(getX(), nextStep.x, lerpFactor));
                setY(lerpY(getY(), nextStep.y, lerpFactor));
            }

            // Check if reached the next point (with some threshold)
            if (Math.abs(getX() - nextStep.x) < 0.1f && Math.abs(getY() - nextStep.y) < 0.1f) {
                pathIndex++;
            }
        }
    }

    // Linear interpolation methods for x and y positions
    private float lerpX(float currentX, float nextX, float lerpFactor) {
        return currentX + (nextX - currentX) * lerpFactor;
    }

    private float lerpY(float currentY, float nextY, float lerpFactor) {
        return currentY + (nextY - currentY) * lerpFactor;
    }

}
