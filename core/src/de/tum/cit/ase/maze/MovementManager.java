package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.awt.*;

/**
 * The {@code MovementManager} class is responsible for managing the movement and interactions
 * of the player's character within the game. It handles input from the player, checks for collisions
 * with game objects such as keys, traps, and enemies, and manages the consequences of these interactions.
 */
public class MovementManager {
    private Character character;
    private GameMap gameMap;
    private Hud hud;
    private boolean isPaused;
    private Key key;
    private MazeRunnerGame game;
    private float timeSinceLastSound = 0;
    private boolean isCollidingWithHazard = false;
    private float timeSinceLastTrapSound = 0;
    private boolean isCollidingWithTrap = false;

    /**
     * Constructs a {@code MovementManager} with the game's character, map, HUD, key, and the main game class.
     *
     * @param character The player's character.
     * @param gameMap The game map.
     * @param hud The heads-up display showing game information.
     * @param key The key object within the game.
     * @param game The main game class, used to access shared resources and methods.
     */

    public MovementManager(Character character, GameMap gameMap, Hud hud, Key key, MazeRunnerGame game) {
        this.character = character;
        this.gameMap = gameMap;
        this.game = game;
        this.hud = hud;
        this.key = key;
        this.isPaused = false;

    }

    /**
     * Handles keyboard input from the player to move the character and performs actions
     * based on the character's interactions with the game environment, such as collecting
     * keys or reaching the game's exit point.
     */
    public void handleInput() {
        if (isPaused) return;
        float nextX = character.getX();
        float nextY = character.getY();
        checkWinCondition();

        boolean moved = false;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (!gameMap.isCellBlocked(nextX, nextY + 13)) {
                character.moveUp();
                moved = true;

                if (gameMap.collusionWithKey(nextX, nextY + 13)) {
                    key.collect(gameMap);
                    key.isCollected();
                    hud.showKeyCollected();
                    game.playKeyPickupSound();
                }
            }
        }


        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { //13+1=7+7 just different division (looks wierd otherwise)
            if (!gameMap.isCellBlocked(nextX, nextY - 1)) {
                character.moveDown();
                moved = true;

                if (gameMap.collusionWithKey(nextX, nextY - 1)) {
                    key.collect(gameMap);
                    key.isCollected();
                    hud.showKeyCollected();
                    game.playKeyPickupSound();
                }
            }

        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { //7 is two smaller than sprite size divided by two (origin point for collusion detection is the middle)
            if (!gameMap.isCellBlocked(nextX - 7, nextY)) {
                character.moveLeft();
                moved = true;

                if (gameMap.collusionWithKey(nextX - 7, nextY)) {
                    key.collect(gameMap);
                    key.isCollected();
                    hud.showKeyCollected();
                    game.playKeyPickupSound();
                }
            }

        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!gameMap.isCellBlocked(nextX + 7, nextY)) {
                character.moveRight();
                moved = true;

                if (gameMap.collusionWithKey(nextX +7,  nextY)) {
                    key.collect(gameMap);
                    key.isCollected();
                    hud.showKeyCollected();
                    game.playKeyPickupSound();
                }
            }
        }

        if (moved) {
            if (gameMap.collusionWithExit(character.getX(), character.getY())) {

            }

        } else {
            character.resetAnimationStateTime();
        } // Reset state time if not moving
    }

    /**
     * Checks if the winning conditions of the game are met, typically by the character
     * collecting a key and reaching the exit, and then displays the win screen.
     */
    public void checkWinCondition() {
        if ((character.hasKey() || key.isCollected()) && gameMap.collusionWithExit(character.getX(), character.getY())) {
            hud.showWinScreen();
        }
    }

    /**
     * Handles the character's collision with enemies within the game. If a collision is detected,
     * the character loses a life and a sound effect is played. This method also implements a cooldown
     * mechanism to avoid rapid life loss.
     *
     * @param deltaTime The time in seconds since the last frame.
     */
    public void handleEnemyCollusion(float deltaTime){
        if(gameMap.collusionWithEnemy(character.getX(), character.getY())) {
            if (!isCollidingWithHazard) {
                character.loseLife();
                game.enemySound.play(1.0f);
                timeSinceLastSound = 0; // Reset the timer
                isCollidingWithHazard = true;
            } else {
                timeSinceLastSound += deltaTime;
                if (timeSinceLastSound >= 3.0f) { // 3 seconds passed
                    game.enemySound.play(1.0f); // Play the sound again
                    character.loseLife();
                    timeSinceLastSound = 0; // Reset the timer
                }
            }
        }
        else {
            // No collision is happening
            isCollidingWithHazard = false;
        }

    }

    /**
     * Handles the character's collision with extra life objects within the game. If a collision is detected,
     * the character gains a life, the extra life object is removed from the game, and a sound effect is played.
     */
    public void handleExtraLifeCollision() {
        int tileSize = 16; // Assuming each tile is 16x16 pixels
        float offsetX = (34 - tileSize) / 2f; // Adjust based on your character's sprite size
        float offsetY = (32 - tileSize) / 2f; // Adjust based on your character's sprite size
        int tileX = (int) ((character.getX() + offsetX) / tileSize);
        int tileY = (int) ((character.getY() + offsetY) / tileSize);

        GameObject gameObject = gameMap.getGameObjectAt(tileX, tileY);
        if (gameObject instanceof ExtraLife) {
            ExtraLife extraLife = (ExtraLife) gameObject;
            if (!extraLife.isCollected()) { // Check if the heart has not been collected yet
                extraLife.collect(); // Mark the heart as collected
                game.playExtraLifeSound();
                character.gainLife();
                gameMap.removeGameObjectAt(tileX, tileY); // Remove the heart from the map
            }
        }
    }

    /**
     * Handles the character's collision with traps within the game. Similar to enemy collision,
     * this method detects collision with traps, causes the character to lose a life, plays a sound effect,
     * and implements a cooldown mechanism.
     *
     * @param deltaTime The time in seconds since the last frame.
     */
    public void handleTrapCollusion(float deltaTime){
        boolean currentlyCollidingWithTrap = gameMap.collusionWithTrap(character.getX(), character.getY());

        if (currentlyCollidingWithTrap) {
            if (!isCollidingWithTrap) {
                // This is the first frame of collision with a trap
                game.trapSound.play(); // Play the sound
                character.loseLife();
                timeSinceLastTrapSound = 0; // Reset the timer
                isCollidingWithTrap = true;
            } else {
                // The character is still colliding with a trap
                timeSinceLastTrapSound += deltaTime;
                if (timeSinceLastTrapSound >= 3.0f) { // 3 seconds passed
                    game.trapSound.play(); // Play the sound again
                    character.loseLife();
                    timeSinceLastTrapSound = 0; // Reset the timer
                }
            }
        } else {
            // No collision with a trap is happening
            isCollidingWithTrap = false;
        }
    }

    /**
     * Pauses the game's movement and interactions, typically used when the game menu is accessed
     * or the game is otherwise interrupted.
     */
    public void pause() {
        isPaused = true;
    }

    /**
     * Resumes the game's movement and interactions after being paused.
     */
    public void resume() {
        isPaused = false;
    }
}