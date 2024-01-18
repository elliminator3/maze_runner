package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

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


    public MovementManager(Character character, GameMap gameMap, Hud hud, Key key, MazeRunnerGame game) {
        this.character = character;
        this.gameMap = gameMap;
        this.game = game;
        this.hud = hud;
        this.key = key;
        this.isPaused = false;

    }


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
                //ToDo anpassen!
                //character.updateAnimationStateTime(Gdx.graphics.getDeltaTime());
            } else {
                character.resetAnimationStateTime();
            } // Reset state time if not moving

    }
    public void checkWinCondition() {
        if (character.hasKey() || key.isCollected() && gameMap.collusionWithExit(character.getX(), character.getY())){
            hud.showWinScreen();
        }
    }

    public void handleEnemyCollusion(float deltaTime){
        if(gameMap.collusionWithEnemy(character.getX(), character.getY())) {
            if (!isCollidingWithHazard) {
                character.loseLife();
                game.enemySound.play(1.0f);
                timeSinceLastSound = 0; // Reset the timer
                isCollidingWithHazard = true;
            /*//only to test if it works
            Point entry = gameMap.findEntry();
            character.setPosition(entry.x, entry.y);*/
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



    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }
}