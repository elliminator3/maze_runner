package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;

import java.awt.*;

public class Character extends GameObject{
    private int lives;
    private boolean hasKey;
    private Key key;
    private Rectangle boundingRectangle;
    private Hud hud;
    private Animation<TextureRegion> upAnimation, downAnimation, leftAnimation, rightAnimation;
    private Animation<TextureRegion> currentAnimation;
    private GameMap maze;
    private float stateTime;
    private float speed = 90;
    private TextureRegion keyFrame;
    private float trapCooldownTime = 0; //cooldown
    private final float trapCooldownDuration = 0.7f; // 1 second cooldown
private MazeRunnerGame game;
    private Animation<TextureRegion> standingDownAnimation, standingRightAnimation, standingUpAnimation,standingLeftAnimation;
   private MovementState currentMovementState = MovementState.STANDING;

    // Additional attributes to handle animations
    private TextureRegion currentFrame;
    private static final int FRAME_COLS = 17; // Number of columns in the sprite sheet
    private static final int FRAME_ROWS = 8; // Number of rows in the sprite sheet


    // Helper method to extract frames from the given row
    public enum Direction {
        DOWN, UP, LEFT, RIGHT
    }

    // Current direction the character is facing
    private Direction currentDirection = Direction.DOWN;


    public Character(float x, float y, String texturePath, int lives, GameMap maze) {
        super(x, y, texturePath);
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

    public enum MovementState {
        STANDING, MOVING_UP, MOVING_DOWN, MOVING_LEFT, MOVING_RIGHT
    }


    // Current movement state


    private Array<TextureRegion> getFrames(TextureRegion[][] frames, int startRow, int startCol, int frameCount) {
        Array<TextureRegion> animationFrames = new Array<>();
        int row = startRow; // No loop needed if only one row is used
        for (int col = startCol; col < startCol + frameCount; col++) {
            animationFrames.add(frames[row][col]);
        }
        return animationFrames;
    }

    private Array<TextureRegion> getStandingFrames(TextureRegion[][] frames, int startRow, int startCol, int frameCount) {
        Array<TextureRegion> animationFrames = new Array<>();
        int row = startRow; // No loop needed if only one row is used
        for (int col = startCol; col < startCol + frameCount; col++) {
            animationFrames.add(frames[row][col]);
        }
        return animationFrames;
    }

    public void checkForStop() {
        if (!Gdx.input.isKeyPressed(Input.Keys.UP) &&
                !Gdx.input.isKeyPressed(Input.Keys.DOWN) &&
                !Gdx.input.isKeyPressed(Input.Keys.LEFT) &&
                !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            currentMovementState = MovementState.STANDING;
        }
    }
    //specifies how to draw the character on the screen using a SpriteBatch
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


    @Override
    public void render(SpriteBatch batch, float x, float y) {
        //not needed?
    }


    // Movement methods
    public void moveUp() {
        setY(getY() + speed * Gdx.graphics.getDeltaTime());
        currentAnimation = upAnimation;
        currentMovementState = MovementState.MOVING_UP;
        currentDirection = Direction.UP;
    }
    public void moveDown() {
        setY(getY() - speed * Gdx.graphics.getDeltaTime());
        currentAnimation = downAnimation;
        currentMovementState = MovementState.MOVING_DOWN;
        currentDirection = Direction.DOWN;
    }
    public void moveRight() {
        setX(getX() + speed * Gdx.graphics.getDeltaTime());
        currentAnimation = rightAnimation;
        currentMovementState = MovementState.MOVING_RIGHT;
        currentDirection = Direction.RIGHT;
    }
    public void moveLeft() {
        setX(getX() - speed * Gdx.graphics.getDeltaTime());
        currentAnimation = leftAnimation;
        currentMovementState = MovementState.MOVING_LEFT;
        currentDirection = Direction.LEFT;
    }


    public void resetAnimationStateTime() {
        stateTime = 0f;
    }

    public int getLives() {
        return lives;
    }
    public void setHud(Hud hud) {
        this.hud = hud;
    }

    //ToDo: why does sometimes more than one live get subtracted?
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

    public float getWidth() {
        if (keyFrame != null) {
            return keyFrame.getRegionWidth();
        }
        return 0; // Return a default width if keyFrame is null
    }

    public float getHeight() {
        if (keyFrame != null) {
            return keyFrame.getRegionHeight();
        }
        return 0; // Return a default height if keyFrame is null
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

    public void checkForKeyCollision(Key key) {
        if (!key.isCollected() && getBoundingRectangle().overlaps(key.getBoundingRectangle())) {
            key.collect(maze);
            //character collects key
            hasKey = true;
            if (hud != null) {
                hud.showKeyCollected();
            }
        }
    }


    //cooldown
    public void update(float deltaTime) {
        stateTime += deltaTime;
        checkForStop();
        if (trapCooldownTime > 0) {
            trapCooldownTime -= deltaTime;
        }
        if (maze.collusionWithKey(getX(), getY()) && !hasKey){
            hasKey = true;
        }
        boundingRectangle.setPosition(getX(), getY());
    }
}