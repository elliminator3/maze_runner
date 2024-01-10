package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tileset {
    private Texture tilesetTexture;
    private TextureRegion[][] tiles;
    private int tilesAcross;

    public Tileset(String filename, int tileWidth, int tileHeight) {
        tilesetTexture = new Texture(Gdx.files.internal(filename));
        int tilesAcross = tilesetTexture.getWidth() / tileWidth;
        this.tilesAcross = tilesAcross;
        tiles = TextureRegion.split(tilesetTexture, tileWidth, tileHeight);
    }

    public TextureRegion getTile(int tileCode) {
        // Calculate the index of the tile in the TextureRegion array
        int row = tileCode / tilesAcross;
        int col = tileCode % tilesAcross;
        return tiles[row][col];
    }

    public void dispose() {
        tilesetTexture.dispose();
    }
}
