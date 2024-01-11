package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
public class MovementManager {
    private Character character;
    private GameMap maze;

    public MovementManager(Character character, GameMap maze) {
        this.character = character;
        this.maze = maze;
    }

    public void handleInput() {
        int tileSize = 16;
        float characterWidth = character.getWidth();
        float characterHeight = character.getHeight();
        float nextX = character.getX();
        float nextY = character.getY();

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            nextY += tileSize;
            if (!maze.isCellBlocked(nextX, nextY + characterHeight)) {
                character.moveUp();
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            if (!maze.isCellBlocked(nextX, nextY - tileSize)) {
                character.moveDown();
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (!maze.isCellBlocked(nextX - tileSize, nextY)) {
                character.moveLeft();
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            nextX += tileSize;
            if (!maze.isCellBlocked(nextX + characterWidth, nextY)) {
                character.moveRight();
            }
        }
    }

}