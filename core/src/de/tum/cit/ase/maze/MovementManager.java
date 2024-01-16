package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;

import java.awt.*;

public class MovementManager {
    private Character character;
    private GameMap maze;
    private Hud hud;
    private boolean isPaused;
    private Key key;

    public MovementManager(Character character, GameMap maze, Hud hud, Key key) {
        this.character = character;
        this.maze = maze;
        this.hud = hud;
        this.key = key;
        this.isPaused = false;

    }


    public void handleInput() {
        if (isPaused) return;
        float nextX = character.getX();
        float nextY = character.getY();
        character.checkForKeyCollision(key);

        boolean moved = false;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (!maze.isCellBlocked(nextX, nextY + 13)) {
                character.moveUp();

                if (maze.collusionWithTrap(nextX, nextY + 13)) { //if character steps on a spring he loses a live and starts again at his starting point
                    character.loseLife();   //ToDo looselive() method in character
                }
                if (maze.collusionWithEnemy(nextX, nextY + 13)) {
                    Point start = maze.findEntry();
                    character.setPosition(start.x, start.y);
                    character.loseLife();
                }
                if (maze.collusionWithKey(nextX, nextY + 13)) {
                    key.collect(maze);
                    hud.showKeyCollected();
                }
            }
        }
            else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { //13+1=7+7 just different division (looks wierd otherwise)
                if (!maze.isCellBlocked(nextX, nextY - 1)) {
                    character.moveDown();
                    if (maze.collusionWithTrap(nextX, nextY - 1)) {
                        character.loseLife();
                    }
                    if (maze.collusionWithEnemy(nextX, nextY - 1)) {
                        Point start = maze.findEntry();
                        character.setPosition(start.x, start.y);
                        character.loseLife();

                    }
                    if (maze.collusionWithKey(nextX, nextY - 1)) {
                        key.collect(maze);
                        hud.showKeyCollected();
                    }
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { //7 is two smaller than sprite size divided by two (origin point for collusion detection is the middle)
                if (!maze.isCellBlocked(nextX - 7, nextY)) {
                    character.moveLeft();
                    if (maze.collusionWithTrap(nextX - 7, nextY)) {
                        character.loseLife();
                    }
                    if (maze.collusionWithEnemy(nextX - 7, nextY)) {
                        Point start = maze.findEntry();
                        character.setPosition(start.x, start.y);
                        character.loseLife();
                    }
                    if (maze.collusionWithKey(nextX - 7, nextY)) {
                        key.collect(maze);
                        hud.showKeyCollected();

                    }
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (!maze.isCellBlocked(nextX + 7, nextY)) {
                    character.moveRight();
                    if (maze.collusionWithTrap(nextX + 7, nextY)) {
                        character.loseLife();
                    }
                    if (maze.collusionWithEnemy(nextX + 7, nextY)) {
                        Point start = maze.findEntry();
                        character.setPosition(start.x, start.y);
                        character.loseLife();
                    }
                    if (maze.collusionWithKey(nextX +7,  nextY)) {
                        key.collect(maze);
                        hud.showKeyCollected();
                    }
                }
            }

            if (moved) {
                character.updateAnimationStateTime(Gdx.graphics.getDeltaTime());
            } else {
                character.resetAnimationStateTime();
            } // Reset state time if not moving
        }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }
}