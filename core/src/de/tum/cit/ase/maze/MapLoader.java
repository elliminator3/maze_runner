package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;

public class MapLoader {

    private static int[][] loadedMap; // 2D array to store map data

    public static void loadMap(String mapFilePath) {
        FileHandle fileHandle = Gdx.files.internal(mapFilePath);

        if (fileHandle.exists()) {
            String fileContent = fileHandle.readString();
            parseMapData(fileContent);
        } else {
            System.err.println("Map file does not exist: " + mapFilePath);
        }
    }

    private static void parseMapData(String fileContent) {
        // Split the file content into lines
        String[] lines = fileContent.split("\n");

        // Determine the size of the map based on the number of lines
        int rows = lines.length;
        int columns = 15;

        loadedMap = new int[rows][columns];

        // Find the maximum number of columns in any line
        for (int row = 0; row < rows; row++) {
            String[] tokens = lines[row].split("=");
            for (String token : tokens) {
                String[] pair = token.split(",");
                if (pair.length > 1) {
                    int column = Integer.parseInt(pair[0].trim());
                    int value = Integer.parseInt(pair[1].trim());
                    loadedMap[row][column] = value;
                }
            }
        }
    }

    public static int[][] getLoadedMap() {
        return loadedMap;
    }
}
