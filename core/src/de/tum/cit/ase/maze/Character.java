package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;

/**
 * Represents the player's character in the game, extending the {@link GameObject} class.
 * This class handles the character's movement, animation, collision detection with keys, and life management.
 */
public class Character extends GameObject{
    private int lives; // The number of lives the character has
    private boolean hasKey; // Flag indicating whether the character has collected a key
    private Key key; // Reference to the key object if collected
    private Rectangle boundingRectangle; // Bounding rectangle for collision detection
    private Hud hud; // Reference to the game HUD for displaying information
    private Animation<TextureRegion> upAnimation, downAnimation, leftAnimation, rightAnimation; // Movement animations
    private Animation<TextureRegion> currentAnimation; // The current animation being played
    private GameMap maze;  // Reference to the game map for collision and interaction checks
    private float stateTime; // Timer for animation state
    private float speed = 90; // Movement speed of the character
    private TextureRegion keyFrame; // The frame representing the key when collected
    private float trapCooldownTime = 0; // Timer for trap interaction cooldown
    private final float trapCooldownDuration = 0.7f; // Duration of the trap interaction cooldown
    private Animation<TextureRegion> standingDownAnimation, standingRightAnimation, standingUpAnimation,standingLeftAnimation; // Standing animations
    private MovementState currentMovementState = MovementState.STANDING; // The current movement state of the character
    private Direction currentDirection = Direction.DOWN; // Current direction the character is facing

    // Additional attributes to handle animations
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 17; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 8; // Number of rows in the sprite sheet


    /**
     * Constructs a new Character object with specified position, texture, number of lives, and game map reference.
     *
     * @param x              The x-coordinate of the character's initial position.
     * @param y              The y-coordinate of the character's initial position.
     * @param texturePath    The path to the texture file for the character sprite.
     * @param lives          The initial number of lives the character has.
     * @param maze           The {@link GameMap} object representing the game map.
     * @param textureManager The {@link TextureManager} responsible for managing game textures.
     */
    public Character(float x, float y, String texturePath, int lives, GameMap maze, TextureManager textureManager) {
        super(x, y, texturePath, textureManager);
        this.hasKey = false;
        this.key = null;
        this.lives = lives;
        this.maze = maze;

        // Split the sprite sheet into individual frames
        Texture characterSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] tmp = TextureRegion.split(characterSheet,
                characterSheet.getWidth() / FRAME_COLS,
                characterSheet.getHeight() / FRAME_ROWS);

        Texture objectSheet = new Texture(Gdx.files.internal(texturePath));
        TextureRegion[][] tmp2 = TextureRegion.split(characterSheet,
                objectSheet.getWidth() / 33,
                objectSheet.getHeight() / 20);
        keyFrame = tmp2[16][5];

        boundingRectangle = new Rectangle(x, y, getWidth(), getHeight());
        // Assuming you want the first frame of the first animation row
        upAnimation = new Animation<>(0.1f, getFrames(tmp, 2, 0, 4)); // First four frames of the third row
        downAnimation = new Animation<>(0.1f, getFrames(tmp, 0, 0, 4)); // First four frames of the first row
        leftAnimation = new Animation<>(0.1f, getFrames(tmp, 3, 0, 4)); // First four frames of the fourth row
        rightAnimation = new Animation<>(0.1f, getFrames(tmp, 1, 0, 4)); // First four frames of the second row
        currentAnimation = downAnimation;
        standingDownAnimation = new Animation<>(0.1f, getStandingFrames(tmp, 0, 5, 3));
        standingRightAnimation = new Animation<>(0.1f, getStandingFrames(tmp, 1, 7, 3));
        standingUpAnimation = new Animation<>(0.1f, getStandingFrames(tmp, 2, 5, 3));
        standingLeftAnimation = new Animation<>(0.1f, getStandingFrames(tmp, 3, 7, 3));

        stateTime = 0f;
    }

    /**
     * Enum representing the possible movement states of the character.
     */
    public enum MovementState {
        STANDING, MOVING_UP, MOVING_DOWN, MOVING_LEFT, MOVING_RIGHT
    }

    /**
     * Enum representing the possible directions the character can face.
     */
    public enum Direction {
        DOWN, UP, LEFT, RIGHT
    }

    /**
     * Extracts animation frames from a specified row and column range in a 2D array of {@link TextureRegion}.
     *
     * @param frames    The 2D array of {@link TextureRegion} containing the sprite sheet frames.
     * @param startRow  The row in the sprite sheet from which to start extracting frames.
     * @param startCol  The column in the sprite sheet from which to start extracting frames.
     * @param frameCount The number of frames to extract from the specified row.
     * @return An {@link Array} of {@link TextureRegion} containing the extracted frames.
     */
    private Array<TextureRegion> getFrames(TextureRegion[][] frames, int startRow, int startCol, int frameCount) {
        Array<TextureRegion> animationFrames = new Array<>();
        int row = startRow; // No loop needed if only one row is used
        for (int col = startCol; col < startCol + frameCount; col++) {
            animationFrames.add(frames[row][col]);
        }
        return animationFrames;
    }

    /**
     * Extracts standing animation frames from a specified row and column range in a 2D array of {@link TextureRegion}.
     * Standing frames are used when the character is not moving.
     *
     * @param frames     The 2D array of {@link TextureRegion} containing the sprite sheet frames.
     * @param startRow   The row in the sprite sheet from which to start extracting frames.
     * @param startCol   The column in the sprite sheet from which to start extracting frames.
     * @param frameCount The number of frames to extract from the specified row for the standing animation.
     * @return An {@link Array} of {@link TextureRegion} containing the extracted frames for standing animation.
     */
    private Array<TextureRegion> getStandingFrames(TextureRegion[][] frames, int startRow, int startCol, int frameCount) {
        Array<TextureRegion> animationFrames = new Array<>();
        int row = startRow; // No loop needed if only one row is used
        for (int col = startCol; col < startCol + frameCount; col++) {
            animationFrames.add(frames[row][col]);
        }
        return animationFrames;
    }

    /**
     * Checks if any movement keys are being pressed and updates the character's movement state to standing if none are pressed.
     * This method is used to transition the character's animation from a moving state to a standing state.
     */
    public void checkForStop() {
        if (!Gdx.input.isKeyPressed(Input.Keys.UP) &&
                !Gdx.input.isKeyPressed(Input.Keys.DOWN) &&
                !Gdx.input.isKeyPressed(Input.Keys.LEFT) &&
                !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            currentMovementState = MovementState.STANDING;
        }
    }


    // Movement methods
    /**
     * Moves the character upwards by increasing its y-coordinate based on the character's speed and the time elapsed since the last frame.
     * It also updates the character's current animation to the up animation and sets the movement state and direction accordingly.
     */
    public void moveUp() {
        setY(getY() + speed * Gdx.graphics.getDeltaTime());
        currentAnimation = upAnimation;
        currentMovementState = MovementState.MOVING_UP;
        currentDirection = Direction.UP;
    }

    /**
     * Moves the character downwards by decreasing its y-coordinate based on the character's speed and the time elapsed since the last frame.
     * It also updates the character's current animation to the down animation and sets the movement state and direction accordingly.
     */
    public void moveDown() {
        setY(getY() - speed * Gdx.graphics.getDeltaTime());
        currentAnimation = downAnimation;
        currentMovementState = MovementState.MOVING_DOWN;
        currentDirection = Direction.DOWN;
    }

    /**
     * Moves the character to the right by increasing its x-coordinate based on the character's speed and the time elapsed since the last frame.
     * It also updates the character's current animation to the right animation and sets the movement state and direction accordingly.
     */
    public void moveRight() {
        setX(getX() + speed * Gdx.graphics.getDeltaTime());
        currentAnimation = rightAnimation;
        currentMovementState = MovementState.MOVING_RIGHT;
        currentDirection = Direction.RIGHT;
    }

    /**
     * Moves the character to the left by decreasing its x-coordinate based on the character's speed and the time elapsed since the last frame.
     * It also updates the character's current animation to the left animation and sets the movement state and direction accordingly.
     */
    public void moveLeft() {
        setX(getX() - speed * Gdx.graphics.getDeltaTime());
        currentAnimation = leftAnimation;
        currentMovementState = MovementState.MOVING_LEFT;
        currentDirection = Direction.LEFT;
    }

    /**
     * Renders the character at its current position using the provided {@link SpriteBatch}.
     *
     * @param batch The {@link SpriteBatch} used for drawing the character on the screen.
     */
    @Override
    public void render(SpriteBatch batch) {
        checkForStop();
        if (currentMovementState == MovementState.STANDING) {
            switch (currentDirection) {
                case DOWN:
                    currentAnimation = standingDownAnimation;
                    break;
                case UP:
                    currentAnimation = standingUpAnimation;
                    break;
                case LEFT:
                    currentAnimation = standingLeftAnimation;
                    break;
                case RIGHT:
                    currentAnimation = standingRightAnimation;
                    break;
            }
        }
        currentFrame = currentAnimation.getKeyFrame(stateTime, true);  // Get current frame based on the state time
        batch.draw(currentFrame, getX(), getY());  // Draw at character's current position

    }

    /**
     * Not used for character but could render the character at a specified location.
     *
     * @param batch The SpriteBatch that's used for drawing the texture.
     * @param x The x-coordinate where the character should be rendered on the screen.
     * @param y The y-coordinate where the character should be rendered on the screen.
     */
    @Override
    public void render(SpriteBatch batch, float x, float y) {
    }

    /**
     * Resets the animation state time to 0. This is typically used when the character's animation needs to be restarted.
     */
    public void resetAnimationStateTime() {
        stateTime = 0f;
    }

    /**
     * Decreases the character's life count by one, provided the character is not in a cooldown period from a previous life loss.
     * This method is called when the character collides with an enemy or a trap. If the character's life count reaches zero,
     * the game over screen is shown. The method also enforces a cooldown period to prevent immediate consecutive life loss.
     */
    public void loseLife() {
        if (trapCooldownTime <= 0 &&lives > 0) { //cooldown
            lives--;
            trapCooldownTime = trapCooldownDuration; //cooldown
            if (hud != null) {
                hud.setScore(lives);  // This will also update the hearts on the HUD
                if (lives == 0) {
                    hud.showGameOverScreen();
                }
            }
        }
    }

    /**
     * Increases the character's number of lives by one, up to a maximum limit. This is typically invoked when the character picks up a life power-up.
     */
    public void gainLife() {
        if(hud.getScore()<5){
            lives++;
            if (hud != null) {
                hud.setScore(lives);  // This will also update the hearts on the HUD
                if (lives == 0) {
                    hud.showGameOverScreen();
                }
            }
        }
    }

    /**
     * Checks for a collision between the character and a key. If a collision is detected and the key has not been collected,
     * the key is collected, and the character's key flag is set to true. If applicable, updates the HUD to show the key has been collected.
     *
     * @param key The {@link Key} object to check for a collision with.
     */
    public void checkForKeyCollision(Key key) {
        if (!key.isCollected() && getBoundingRectangle().overlaps(key.getBoundingRectangle())) {
            key.collect(maze);
            //character collects key
            hasKey = true;
            if (hud != null) {
                hud.showKeyCollected();
            }
        }
    } //ToDo delete?

    /**
     * Updates the character's state with the passage of time, including animation progress and cooldowns from interactions with traps.
     * This method should be called every frame with the delta time since the last frame to ensure smooth animation and accurate timing.
     *
     * @param deltaTime The time in seconds that has elapsed since the last update.
     */
    public void update(float deltaTime) {
        stateTime += deltaTime;
        checkForStop();
        if (trapCooldownTime > 0) {
            trapCooldownTime -= deltaTime; //cooldown
        }
        if (maze.collusionWithKey(getX(), getY()) && !hasKey){
            hasKey = true;
        }
        boundingRectangle.setPosition(getX(), getY());
    }

    //Getter and setter
    /**
     * Retrieves the width of the character's current key frame. If the key frame is not set, returns a default width of 0.
     *
     * @return The width of the character's current key frame, or 0 if the key frame is null.
     */
    public float getWidth() {
        if (keyFrame != null) {
            return keyFrame.getRegionWidth();
        }
        return 0; // Return a default width if keyFrame is null
    }

    /**
     * Retrieves the height of the character's current key frame. If the key frame is not set, returns a default height of 0.
     *
     * @return The height of the character's current key frame, or 0 if the key frame is null.
     */
    public float getHeight() {
        if (keyFrame != null) {
            return keyFrame.getRegionHeight();
        }
        return 0; // Return a default height if keyFrame is null
    }

    /**
     * Checks whether the character has collected a key.
     *
     * @return {@code true} if the character has collected a key, {@code false} otherwise.
     */
    public boolean hasKey() {
        return hasKey;
    }

    /**
     * Gets the bounding rectangle of the character used for collision detection.
     *
     * @return The {@link Rectangle} representing the character's bounding box.
     */
    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }


    /**
     * Retrieves the number of lives remaining for the character.
     *
     * @return The number of lives the character currently has.
     */
    public int getLives() {
        return lives;
    }

    /**
     * Sets the HUD (Heads-Up Display) reference for this character. The HUD is used to display various game information
     * such as the character's remaining lives, collected keys, and other status indicators. Setting the HUD allows the character
     * to update the display based on its current state, such as decrementing life count or showing that a key has been collected.
     *
     * @param hud The {@link Hud} instance to be associated with this character.
     */
    public void setHud(Hud hud) {
        this.hud = hud;
    }

}