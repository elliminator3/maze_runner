package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

public class MapLoader {

    public static int[][] loadMap(String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        if (!file.exists()) {
            Gdx.app.error("MapLoader", "File not found: " + filePath);
            return null; // Or throw an exception if you prefer
        }

        String fileContent = file.readString();
        String[] lines = fileContent.split("\r\n|\r|\n");
        Map<String, Integer> tileMap = new HashMap<>();
        int rows = 0;
        int cols = 0;

        // Parse the file to determine the dimensions and the tile types
        for (String line : lines) {
            if (!line.startsWith("#") && !line.isEmpty()) {
                String[] parts = line.split("=");
                String[] coordinates = parts[0].split(",");
                int row = Integer.parseInt(coordinates[0]);
                int col = Integer.parseInt(coordinates[1]);
                int value = Integer.parseInt(parts[1]);
                tileMap.put(parts[0], value);

                rows = Math.max(row + 1, rows);
                cols = Math.max(col + 1, cols);
            }
        }

        // Create the array that will hold the map data
        int[][] mapData = new int[rows][cols];

        // Fill in the map data using the parsed values
        for (Map.Entry<String, Integer> entry : tileMap.entrySet()) {
            String[] coordinates = entry.getKey().split(",");
            int row = Integer.parseInt(coordinates[0]);
            int col = Integer.parseInt(coordinates[1]);
            mapData[row][col] = entry.getValue();
        }

        return mapData;
    }
}
