package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.awt.*;

public class MovementManager {
    private Character character;
    private GameMap gameMap;
    private Hud hud;
    private boolean isPaused;
    private Key key;

    public MovementManager(Character character, GameMap gameMap, Hud hud, Key key) {
        this.character = character;
        this.gameMap = gameMap;
        this.hud = hud;
        this.key = key;
        this.isPaused = false;

    }


    public void handleInput() {
        if (isPaused) return;
        float nextX = character.getX();
        float nextY = character.getY();
        character.checkForKeyCollision(key);
        checkWinCondition();

        boolean moved = false;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (!gameMap.isCellBlocked(nextX, nextY + 13)) {
                character.moveUp();
                moved = true;

                if (gameMap.collusionWithTrap(nextX, nextY + 13)) { //if character steps on a spring he loses a live and starts again at his starting point
                    character.loseLife();
                }
                if (gameMap.collusionWithKey(nextX, nextY + 13)) {
                    key.collect(gameMap);
                    key.isCollected();
                    hud.showKeyCollected();

                }
            }
        }

            else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { //13+1=7+7 just different division (looks wierd otherwise)
                if (!gameMap.isCellBlocked(nextX, nextY - 1)) {
                    character.moveDown();
                    moved = true;

                    if (gameMap.collusionWithTrap(nextX, nextY - 1)) {
                        character.loseLife();
                    }
                    if (gameMap.collusionWithKey(nextX, nextY - 1)) {
                        key.collect(gameMap);
                        key.isCollected();
                        hud.showKeyCollected();

                    }
                }

            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { //7 is two smaller than sprite size divided by two (origin point for collusion detection is the middle)
                if (!gameMap.isCellBlocked(nextX - 7, nextY)) {
                    character.moveLeft();
                    moved = true;

                    if (gameMap.collusionWithTrap(nextX - 7, nextY)) {
                        character.loseLife();
                    }
                    if (gameMap.collusionWithKey(nextX - 7, nextY)) {
                        key.collect(gameMap);
                        key.isCollected();
                        hud.showKeyCollected();

                    }
                }

            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (!gameMap.isCellBlocked(nextX + 7, nextY)) {
                    character.moveRight();
                    moved = true;

                    if (gameMap.collusionWithTrap(nextX + 7, nextY)) {
                        character.loseLife();
                    }
                    if (gameMap.collusionWithKey(nextX +7,  nextY)) {
                        key.collect(gameMap);
                        key.isCollected();
                        hud.showKeyCollected();

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


    public void handleEnemyCollusion(){
        if(gameMap.collusionWithEnemy(character.getX(), character.getY())){
            character.loseLife();
            /*//only to test if it works
            Point entry = gameMap.findEntry();
            character.setPosition(entry.x, entry.y);*/
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }
}