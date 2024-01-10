package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.IOException;
import java.util.Properties;

public class GameMap {
    //class to hold the tiles and layout of a map and for methods to load levels from files and render them
    private TextureRegion[][] tiles;
    private int[][] mapLayout;
    public GameMap(String levelFilePath) {
        try {
            loadLevel(levelFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        splitTileset();
    }

    private void loadLevel(String levelFilePath) throws IOException {
        Properties props = new Properties();
            props.load(Gdx.files.internal(levelFilePath).reader()); //loads the level configuration from a .properties file /

        // Assume a fixed map size or calculate based on properties
        mapLayout = calculateMapSize(props);
        for (String key : props.stringPropertyNames()) { //initializes and populates the mapLayout array
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int tileType = Integer.parseInt(props.getProperty(key));
            mapLayout[x][y] = tileType;
        }
    }
    private int[][] calculateMapSize(Properties properties) {
        int maxX = 0;
        int maxY = 0;
        for (String key : properties.stringPropertyNames()) {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }
        return mapLayout = new int[maxX + 1][maxY + 1]; // +1 because arrays are zero-based
    }

    private void splitTileset() {
        //loads the tileset image and uses TextureRegion.split to divide this image into smaller regions, each representing a tile
        Texture tilesetTexture = new Texture(Gdx.files.internal("basictiles.png"));
        tiles = TextureRegion.split(tilesetTexture, 7, 13);
    }

    public void render(SpriteBatch batch) {
        for (int y = 0; y < mapLayout.length; y++) {
            for (int x = 0; x < mapLayout[y].length; x++) {
                int tileType = mapLayout[x][y];
                TextureRegion tileRegion = tiles[tileType / tiles[0].length][tileType % tiles[0].length];
                batch.draw(tileRegion, x * 7, y * 13);
            }
        }
    }

}
