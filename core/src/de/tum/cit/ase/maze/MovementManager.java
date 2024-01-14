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
        float nextX = character.getX();
        float nextY = character.getY();

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (!maze.isCellBlocked(nextX, nextY + 13)) {
                character.moveUp();
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) { //11+3=7+7 just different division (looks wierd otherwise)
            if (!maze.isCellBlocked(nextX, nextY - 1)) {
                character.moveDown();
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) { //7 is two smaller than sprite size divided by two (origin point for collusion detection is the middle)
            if (!maze.isCellBlocked(nextX - 7, nextY)) {
                character.moveLeft();
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!maze.isCellBlocked(nextX + 7, nextY)) {
                character.moveRight();
            }
        }
    }

}