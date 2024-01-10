package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.io.IOException;
import java.util.Properties;

public class GameMap {
    //class to hold the tiles and layout of a map
    private GameObject[][] gameObjects;

    public GameMap(String levelFilePath) {
        try {
            loadLevel(levelFilePath);
        } catch (IOException e) {
            Gdx.app.log("Level Load Error", "Failed to load level file: " + levelFilePath, e);
        }
    }

    //loads the level configuration from a .properties file
    private void loadLevel(String levelFilePath) throws IOException {
        Properties props = new Properties();
        props.load(Gdx.files.internal(levelFilePath).reader());

        gameObjects = calculateMapSize(props);
        for (String key : props.stringPropertyNames()) {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int tileType = Integer.parseInt(props.getProperty(key));
            gameObjects[x][y] = createTile(tileType, x, y);
        }
    }

    //helper method to calculate map size
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
        return gameObjects = new GameObject [maxX + 1][maxY + 1]; // +1 because arrays are zero-based
    }

    //helper method to return the right object texture (wall, entry ...) for each tile type / not efficient
    public GameObject createTile(int tileType, float x, float y) {
        switch (tileType) {
            case 0:
                return new Wall(x, y, "basictiles.png");
            case 1:
                return new Entry(x, y, "basictiles.png");
            case 2:
                return new Exit(x, y, "things.png");
            case 3:
                return new Trap(x, y, "basictiles.png");
            case 4:
                return new Enemy(x, y, "mobs.png");
            case 5:
                return new Key(x, y, "objects.png");
            default:
                return new Path(x,y,"basictiles.png"); // default has to be path
        }
    }


        public void render (SpriteBatch batch){
        int tileSize = 16;
            for (int y = 0; y < gameObjects.length; y++) {
                for (int x = 0; x < gameObjects[y].length; x++) {
                    if (gameObjects[x][y] != null) {
                        float renderX = x * tileSize;
                        float renderY = y * tileSize;
                        gameObjects[x][y].render(batch, renderX, renderY);
                    }
                }
            }
        }

    } //10.01.
