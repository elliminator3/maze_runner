package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import java.awt.*;
import java.util.List;

/**
 * Represents an enemy in the game, extending the {@link GameObject} class. This class handles the enemy's
 * appearance, movement, and pathfinding behavior. Enemies can move randomly around the game map or follow
 * a path to the player character using A* pathfinding when the character is within a certain range.
 */
public class Enemy extends GameObject {
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 12; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 8; // Number of rows in the sprite sheet

    //movement
    private static final float MOVE_INTERVAL = 3f; // Time interval in seconds for each move
    private float moveTimer = MOVE_INTERVAL; // Timer to track movement intervals

    // Intelligent movement
    private List<Point> currentPath; // The shortest path to the player's character
    private int pathIndex; // The current index in the path being followed

    /**
     * Constructs a new Enemy object with the specified position, texture, and texture manager.
     *
     * @param x              The x-coordinate of the enemy's initial position.
     * @param y              The y-coordinate of the enemy's initial position.
     * @param texturePath    The path to the texture file for the enemy sprite.
     * @param textureManager The {@link TextureManager} responsible for managing game textures.
     */
    public Enemy(float x, float y, String texturePath, TextureManager textureManager) {
        super(x, y, texturePath, textureManager);

        // Split the sprite sheet into individual frames
        Texture basicSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] tmp = TextureRegion.split(basicSheet,
                basicSheet.getWidth() / FRAME_COLS,
                basicSheet.getHeight() / FRAME_ROWS);

        // Ghost is enemy
        currentFrame = tmp[5][7];
        this.currentPath = null;
        this.pathIndex = 0;
    }

    /**
     * Renders the enemy at its current position using the provided {@link SpriteBatch}.
     *
     * @param batch The {@link SpriteBatch} used for drawing the enemy on the screen.
     */
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(currentFrame, super.getX() * 16, super.getY() * 16);
    }

    /**
     * Not used for enemy but could render the enemy at a specified location.
     *
     * @param batch The {@link SpriteBatch} used for drawing the texture region.
     * @param x     The x-coordinate where the enemy should be rendered.
     * @param y     The y-coordinate where the enemy should be rendered.
     */
    public void render(SpriteBatch batch, float x, float y) {
        batch.enableBlending();
        batch.draw(currentFrame, x, y);
    }

    // Enemy movement
    /**
     * Updates the enemy's state with the passage of time. This includes moving randomly, following a path to the
     * player character if in range, and updating the move timer. This method gets called every frame with
     * the delta time since the last frame to ensure smooth movement and behavior.
     *
     * @param deltaTime The time in seconds that has elapsed since the last update.
     * @param gameMap   The {@link GameMap} object representing the game map, used for collision checks and pathfinding.
     * @param character The player's {@link Character} object, used for pathfinding towards the player when in range.
     */
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
            followPath(deltaTime, gameMap); // Intelligent movement

        } else {
            moveTimer -= deltaTime;
            if (moveTimer <= 1) {
                moveRandomly(gameMap, deltaTime); // Random movement
                moveTimer = MOVE_INTERVAL;
            }
        }

    }

    /**
     * Moves the enemy randomly in one of four directions (up, down, left, right). This method is used when the
     * enemy is not actively following a path to the player character.
     *
     * @param gameMap   The {@link GameMap} object representing the game map, used for collision checks.
     * @param deltaTime The time in seconds that has elapsed since the last update.
     */
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

        // Check if new position is valid
        if (!gameMap.isCellBlocked(newX * 16, newY * 16)) { //tileSize
            setX(newX);
            setY(newY);
        }
    }


    /**
     * Checks whether the player character is within a specified range of the enemy. This is used to determine
     * whether the enemy should start following a path to the player character using A* pathfinding.
     *
     * @param character The player's {@link Character} object.
     * @return {@code true} if the character is within the specified range, {@code false} otherwise.
     */
    private boolean isCharInRange(Character character) {
        int tileSize = 16; //size of our tiles
        float offsetX = (34 - tileSize) / 2f;
        float offsetY = (32 - tileSize) / 2f;

        // Calculate the offset to center the 16x16 collision box within the 34x32 sprite
        float tileX = (character.getX() + offsetX) / tileSize;
        float tileY = (character.getY() + offsetY) / tileSize;
        float distance = Vector2.dst(this.getX(), this.getY(), tileX, tileY);
        return distance <= 3; // Only start following if just three fields away
    }

    /**
     * Follows the current path to the player character, moving to the next point in the path. This method is used
     * when the enemy is actively pathfinding towards the player character.
     *
     * @param deltaTime The time in seconds that has elapsed since the last update.
     * @param gameMap   The {@link GameMap} object representing the game map, used for collision checks.
     */
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

    /**
     * Linearly interpolates between the current x-coordinate and the next x-coordinate in the path.
     *
     * @param currentX   The current x-coordinate of the enemy.
     * @param nextX      The next x-coordinate in the path.
     * @param lerpFactor The interpolation factor.
     * @return The interpolated x-coordinate.
     */
    private float lerpX(float currentX, float nextX, float lerpFactor) {
        return currentX + (nextX - currentX) * lerpFactor;
    }

    /**
     * Linearly interpolates between the current y-coordinate and the next y-coordinate in the path.
     *
     * @param currentY     The current y-coordinate of the enemy.
     * @param nextY        The next y-coordinate in the path.
     * @param lerpFactor   The interpolation factor.
     * @return The interpolated y-coordinate.
     */
    private float lerpY(float currentY, float nextY, float lerpFactor) {
        return currentY + (nextY - currentY) * lerpFactor;
    }

}
