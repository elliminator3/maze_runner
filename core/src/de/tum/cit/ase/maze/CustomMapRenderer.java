package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;

public class CustomMapRenderer {
    private int[][] map;
    private Tileset tileset;
    private static final int TILE_SIZE = 16; // Assuming this is the size of each tile

    public CustomMapRenderer(int[][] map, Tileset tileset) {
        this.map = map;
        this.tileset = tileset;
    }

    public void render(SpriteBatch batch) {
        // Render each tile
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                int tileCode = map[row][col];
                TextureRegion tile = tileset.getTile(tileCode);
                if (tile != null) {
                    int renderY = (map.length - 1 - row) * TILE_SIZE; // Flipping the y-axis
                    batch.draw(tile, col * TILE_SIZE, renderY, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }
}
