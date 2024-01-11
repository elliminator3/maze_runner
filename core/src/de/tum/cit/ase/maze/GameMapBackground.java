package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.io.IOException;
import java.util.Properties;

public class GameMapBackground {
    private GameObject[][] gameObjectsBackground;
    public GameMapBackground(String levelFilePath) {
        try {
            loadLevel(levelFilePath);
        } catch (IOException e) {
            Gdx.app.log("Level Load Error", "Failed to load level file: " + levelFilePath, e);
        }
    }
    private void loadLevel(String levelFilePath) throws IOException {
        Properties props = new Properties();
        props.load(Gdx.files.internal(levelFilePath).reader());

        // Calculate map size and initialize gameObjects with path objects
        gameObjectsBackground = calculateMapSize(props);

        for (int y = 0; y < gameObjectsBackground.length; y++) {
            for (int x = 0; x < gameObjectsBackground[y].length; x++) {
                gameObjectsBackground[x][y] = new Path(x, y, "basictiles.png");
            }
        }
    }
    private GameObject[][] calculateMapSize(Properties properties) {
        int maxX = 0;
        int maxY = 0;
        for (String key : properties.stringPropertyNames()) {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }
        return new GameObject [maxX + 1][maxY + 1]; // +1 because arrays are zero-based
    }


    public void render (SpriteBatch batch){
        int tileSize = 16;
        for (int y = 0; y < gameObjectsBackground.length; y++) {
            for (int x = 0; x < gameObjectsBackground[y].length; x++) {
                gameObjectsBackground[x][y].render(batch, x * tileSize, y * tileSize); //draws the background with path texture
            }
        }
    }
}
