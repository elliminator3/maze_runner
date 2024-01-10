package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
public class MovementManager {
    private Character character;

    public MovementManager(Character character) {
        this.character = character;
    }

    public void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            character.moveUp();
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            character.moveDown();
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            character.moveLeft();
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            character.moveRight();
        }
    }
} //10.01.
