package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    //map to hold the textures, using the file path as the key
    private Map<String, Texture> textures;

    public TextureManager() {
        textures = new HashMap<>();
    }

    /**
     * Retrieves a texture based on its file path. If the texture is not already loaded,
     * it loads the texture into memory and stores it in the map for future use.
     *
     * @param path The file path of the texture.
     * @return The Texture object corresponding to the given path.
     */
    public Texture getTexture(String path) {
        if (!textures.containsKey(path)) {
            //load the texture and put it in the map
            textures.put(path, new Texture(Gdx.files.internal(path)));
        }
        return textures.get(path);
    }

    /**
     * Disposes of all loaded textures. This should be called when the game is closing
     * to free up resources.
     */
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();
    }

}
