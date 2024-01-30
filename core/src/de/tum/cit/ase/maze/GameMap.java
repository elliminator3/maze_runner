package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;


/**
 * The {@code GameMap} class is responsible for managing the game's map layout, including walls, paths, entry and exit points, traps, enemies, and collectible items. It loads the map configuration from a properties file and provides functionality to render the game objects, check for collisions, and manage game object interactions.
 */
public class GameMap {
    //class to hold the tiles and layout of a map
    private GameObject[][] gameObjects;
    private int width;
    private int height;
    private Path path;
    private MazeRunnerGame game;
    private ExtraLife extraLife;
    private Array<Enemy> enemies = new Array<>(); // Enemy movement
    private Array<Point> exitPoints = new Array<>(); // ExitPoints


    /**
     * Constructs a {@code GameMap} object and initializes the map layout based on the provided level file path. It also places extra lives on the map.
     *
     * @param levelFilePath The file path to the level configuration file.
     * @param textureManager The texture manager to load textures for the game objects.
     */
    public GameMap(String levelFilePath, TextureManager textureManager) {
        try {
            FileHandle fileHandle = Gdx.files.internal(levelFilePath);
            loadLevel(fileHandle, textureManager);
            placeExtraLives(textureManager);
        } catch (IOException e) {
            Gdx.app.log("Level Load Error", "Failed to load level file: " + levelFilePath, e);
        }
        path = new Path(0, 0, "basictiles.png", textureManager);
    }

    /**
     * Loads the level configuration from a properties file, creating game objects based on the file's contents.
     *
     * @param levelFilePath The file handle to the level configuration file.
     * @param textureManager The texture manager to load textures for the game objects.
     * @throws IOException If there is an error reading the properties file.
     */
    //loads the level configuration from a .properties file
    public void loadLevel(FileHandle levelFilePath, TextureManager textureManager) throws IOException {
        Properties props = new Properties();
        props.load(levelFilePath.reader());

        //calculate map size
        gameObjects = calculateMapSize(props);

        //extract tileType values and coordinates from .properties file
        for (String key : props.stringPropertyNames()) {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int tileType = Integer.parseInt(props.getProperty(key));

            //fill map with the respective tileType at the given coordinates
            if (tileType == 0|tileType == 1|tileType == 2|tileType == 3|tileType == 5) {
                gameObjects[x][y] = createTile(tileType, x, y, textureManager);
            }
            else if (tileType == 4) {
                Enemy enemy = new Enemy(x, y, "mobs.png", textureManager);
                enemies.add(enemy);
            }
        }
    }

    /**
     * Calculates the size of the map based on the properties configuration, determining the maximum width and height based on the coordinates specified in the file.
     *
     * @param properties The properties loaded from the level configuration file.
     * @return A 2D array of {@link GameObject}s representing the map layout.
     */
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
        width = maxX + 1; //viewport
        height = maxY + 1; //viewport
        return gameObjects = new GameObject [width][height];
    }

    /**
     * Creates a game object based on the tile type and coordinates. It maps tile types to specific game object classes.
     *
     * @param tileType The type of tile to create.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @param textureManager The texture manager to load textures for the game objects.
     * @return A {@link GameObject} instance representing the specified tile type.
     */
    //helper method to return the right object texture (wall, entry ...) for each tile type / not efficient?
    public GameObject createTile(int tileType, float x, float y, TextureManager textureManager) {
        return switch (tileType) {
            case 0 -> new Wall(x, y, "basictiles.png", textureManager);
            case 1 -> new Entry(x, y, "basictiles.png", textureManager);
            case 2 -> new Exit(x, y, "things.png", textureManager);
            case 3 -> new Trap(x, y, "basictiles.png", textureManager);
            case 4 -> new Path(x, y, "basictiles.png", textureManager); //tileType enemy is handled separately
            case 5 -> new Key(x, y, "objects.png", textureManager);
            default -> null;
        };
    }

    /**
     * Renders the game objects on the map, including walls, paths, traps, and other elements.
     *
     * @param batch The {@link SpriteBatch} used for drawing.
     */
    //method for drawing the maze
    public void render (SpriteBatch batch){
        int tileSize = 16;
        for (int y = 0; y < gameObjects.length; y++) {
            for (int x = 0; x < gameObjects[y].length; x++) {
                GameObject gameObject = gameObjects[x][y];
                if (gameObject != null) {
                    gameObject.render(batch, x * tileSize, y * tileSize);
                }
            }
        }
    }

    /**
     * Renders the background of the game map, typically the path texture.
     *
     * @param batch The {@link SpriteBatch} used for drawing.
     */
    public void renderBackground(SpriteBatch batch){
        int tileSize = 16;
        for (int y = 0; y < gameObjects.length; y++) {
            for (int x = 0; x < gameObjects[y].length; x++) {
                path.render(batch, x * tileSize, y * tileSize); //draws the background with path texture
            }
        }
    }

    /**
     * Retrieves the game object located at the specified coordinates.
     *
     * @param x The x-coordinate of the desired location.
     * @param y The y-coordinate of the desired location.
     * @return The {@link GameObject} at the specified coordinates, or {@code null} if the location is empty.
     */
    public GameObject getGameObjectAt(int x, int y) {
        return gameObjects[x][y];
    }


    /**
     * Removes the game object located at the specified coordinates, effectively setting the tile to null.
     *
     * @param x The x-coordinate of the location to clear.
     * @param y The y-coordinate of the location to clear.
     */
    public void removeGameObjectAt(int x, int y) {
        gameObjects[x][y] = null;
    }

    /**
     * Places extra life objects on the map at random locations.
     *
     * @param textureManager The texture manager to load textures for the extra life objects.
     */
    public void placeExtraLives(TextureManager textureManager) {
        List<Point> freeTiles = new ArrayList<>();

        // Iterate over the game map to find free tiles
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Consider a tile free if it's a Path or not initialized (null)
                if (gameObjects[x][y] == null || gameObjects[x][y] instanceof Path) {
                    freeTiles.add(new Point(x, y));
                }
            }
        }

        Random random = new Random();
        for (int i = 0; i < 2; i++) { // Place 2 extra lives
            if (!freeTiles.isEmpty()) {
                int index = random.nextInt(freeTiles.size());
                Point point = freeTiles.remove(index); // Remove to avoid placing multiple items on the same tile

                // Place an ExtraLife object at the chosen free tile
                gameObjects[point.x][point.y] = new ExtraLife(point.x, point.y, textureManager);
            }
        }
    }

    /**
     * Checks if the specified cell is blocked by a non-walkable game object.
     * This method is crucial for collision detection and movement mechanics, ensuring characters do not walk through walls or other barriers.
     *
     * @param x X-coordinate in the game world.
     * @param y Y-coordinate in the game world.
     * @return true if the cell is blocked, false otherwise.
     */
    //method prevent character from moving through walls
    public boolean isCellBlocked(float x, float y){
        int tileSize = 16; // size of our tiles
        float offsetX = (34 - tileSize) / 2f;
        float offsetY = (32 - tileSize) / 2f;

        // Calculate the offset to center the 16x16 collision box within the 34x32 sprite
        float tileX = (x + offsetX) / tileSize;
        float tileY = (y + offsetY) / tileSize;

        //check maze bounds
        if (tileX <= 0 || tileX >= gameObjects.length || tileY <= 0|| tileY >= gameObjects[0].length) {
            return true;
        }

        //check for non-walkable objects
        GameObject gameObject = gameObjects[(int)tileX][(int)tileY];
        if(gameObject instanceof Wall || gameObject instanceof Entry){
            return true;
        }
        return false;
    }

    /**
     * Checks if the specified cell is free of any non-walkable game objects.
     *
     * @param x X-coordinate in the game object grid.
     * @param y Y-coordinate in the game object grid.
     * @return true if the cell is free, false otherwise.
     */
    public boolean isCellfree(int x, int y){
        if (x <= 0 || x >= gameObjects.length || y <= 0|| y >= gameObjects[0].length) {
            return false;
        }
        //check for non-walkable objects
        GameObject gameObject = gameObjects[x][y];
        if(gameObject instanceof Wall || gameObject instanceof Entry){
            return false;
        }
        return true;
    }

    /**
     * Detects collisions with trap objects at the specified game world coordinates.
     * This method is essential for implementing gameplay mechanics where traps affect the character's health or status.
     *
     * @param x The x-coordinate in the game world.
     * @param y The y-coordinate in the game world.
     * @return {@code true} if there is a collision with a trap at the specified coordinates, {@code false} otherwise.
     */
    //collusion detection with trap and enemy
    public boolean collusionWithTrap(float x, float y){
        int tileSize = 16; // size of our tiles
        float offsetX = (34 - tileSize) / 2f;
        float offsetY = (32 - tileSize) / 2f;
        int tileX = (int)((x + offsetX) / tileSize);
        int tileY = (int)((y + offsetY) / tileSize);
        GameObject gameObject = gameObjects[tileX][tileY];
        if(gameObject instanceof Trap){
            return true;
        }
        return false;
    }

    /**
     * Detects collisions with the key object at the specified game world coordinates.
     * This method is used to determine when the character has collected a key, a common gameplay element in many games.
     *
     * @param x The x-coordinate in the game world.
     * @param y The y-coordinate in the game world.
     * @return {@code true} if there is a collision with a key at the specified coordinates, {@code false} otherwise.
     */
    public boolean collusionWithKey(float x, float y){
        int tileSize = 16; // size of our tiles
        float offsetX = (34 - tileSize) / 2f;
        float offsetY = (32 - tileSize) / 2f;
        int tileX = (int)((x + offsetX) / tileSize);
        int tileY = (int)((y + offsetY) / tileSize);
        GameObject gameObject = gameObjects[tileX][tileY];
        if(gameObject instanceof Key){
            return true;
        }
        return false;
    }

    /**
     * Checks for collisions with the exit object at the specified game world coordinates.
     * This function is vital for level progression, allowing the game to transition to a new level or state when the character reaches the exit.
     *
     * @param x The x-coordinate in the game world.
     * @param y The y-coordinate in the game world.
     * @return {@code true} if there is a collision with an exit at the specified coordinates, {@code false} otherwise.
     */
    public boolean collusionWithExit(float x, float y){
        int tileSize = 16; // size of our tiles
        float offsetX = (34 - tileSize) / 2f;
        float offsetY = (32 - tileSize) / 2f;
        int tileX = (int)((x + offsetX) / tileSize);
        int tileY = (int)((y + offsetY) / tileSize);
        GameObject gameObject = gameObjects[tileX][tileY];
        if(gameObject instanceof Exit){
            return true;
        }
        return false;
    }

    /**
     * Detects collisions between the character and any enemy objects in the game world.
     * This method is crucial for implementing interactions between the player's character and enemies, such as combat or health deduction.
     *
     * @param x The x-coordinate in the game world where the character is located.
     * @param y The y-coordinate in the game world where the character is located.
     * @return {@code true} if the character is colliding with an enemy, {@code false} otherwise.
     */
    public boolean collusionWithEnemy(float x, float y){
        int tileSize = 16; // size of our tiles
        float characterWidth = 8; // Width of the character's collision box
        float characterHeight = 10; // Height of the character's collision box
        float enemyWidth = 8; // Slightly larger width for the enemy's collision box
        float enemyHeight = 10; // Slightly larger height for the enemy's collision box

        // Adjustments to center the character's collision box
        float characterOffsetX = (tileSize - characterWidth) / 2f;
        float characterOffsetY = (tileSize - characterHeight) / 2f;

        // Create the character's bounding box
        Rectangle characterRect = new Rectangle(
                x + characterOffsetX,
                y + characterOffsetY,
                characterWidth,
                characterHeight
        );

        // Check each enemy for a potential collision
        for (Enemy enemy : enemies) {
            // Calculate the enemy's position in pixels
            float enemyPixelX = enemy.getX() * tileSize;
            float enemyPixelY = enemy.getY() * tileSize;

            // Adjustments to center the enemy's collision box
            float enemyOffsetX = (tileSize - enemyWidth) / 2f;
            float enemyOffsetY = (tileSize - enemyHeight) / 2f;

            // Create the enemy's bounding box
            Rectangle enemyRect = new Rectangle(
                    enemyPixelX + enemyOffsetX,
                    enemyPixelY + enemyOffsetY,
                    enemyWidth,
                    enemyHeight
            );

            // Check for an overlap between the character's and enemy's bounding boxes
            if (characterRect.overlaps(enemyRect)) {
                return true; // Collision detected
            }
        }

        return false; // No collision detected

    }

    /**
     * Finds the entry point in the game map and returns its coordinates.
     * This method is typically used at the start of a level to position the character at the correct starting location.
     *
     * @return A {@code Point} object representing the coordinates of the entry point, or {@code null} if not found.
     */
    public Point findEntry() {
        int tileSize = 16;
        for (int y = 0; y < gameObjects.length; y++) {
            for (int x = 0; x < gameObjects[y].length; x++) {
                if (gameObjects[x][y] instanceof Entry) {
                    return new Point(x * tileSize, y * tileSize); // Point is a simple class holding x and y integers
                }
            }
        }
        return null; //if not found
    }

    /**
     * Locates the key object within the game map and returns its coordinates.
     * This is essential for games where collecting a key is necessary to unlock doors or solve puzzles.
     *
     * @return A {@code Point} object representing the coordinates of the key, or {@code null} if not found.
     */
    public Point findKey() {
        int tileSize = 16;
        for (int y = 0; y < gameObjects.length; y++) {
            for (int x = 0; x < gameObjects[y].length; x++) {
                if (gameObjects[x][y] instanceof Key) {
                    return new Point(x * tileSize, y * tileSize); // Point is a simple class holding x and y integers
                }
            }
        }
        return null; //if not found
    }

    /**
     * Removes the key object from the game map once it has been collected by the character.
     * This method ensures that the key object is no longer rendered or interactable after collection.
     *
     * @param key The {@code Key} object to remove from the game map.
     */
    public void removeKey(Key key) {
        // Find the position of the key in the gameObjects array and set it to null
        int tileSize = 16;
        int x = (int) (key.getX() / tileSize);
        int y = (int) (key.getY() / tileSize);
        gameObjects[x][y] = null;
    }

    /**
     * Identifies all exit points within the game map and returns their coordinates.
     * This function is useful in games with multiple exits or goals.
     *
     * @return An {@code Array} of {@code Point} objects representing the coordinates of all exit points.
     */
    //ExitPoints
    public Array<Point> findExitPoints() {
        for (int y = 0; y < gameObjects.length; y++) {
            for (int x = 0; x < gameObjects[y].length; x++) {
                if (gameObjects[x][y] instanceof Exit) {
                    exitPoints.add(new Point(x , y));
                }
            }
        }
        return exitPoints;
    }

    /**
     * Returns the width of the game map in tiles.
     *
     * @return The width of the game map.
     */
    //viewport
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the game map in tiles.
     *
     * @return The width of the game map.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retrieves the list of enemies currently present in the game map.
     * This method is crucial for game logic that involves enemy interactions, such as AI movement or combat.
     *
     * @return An {@code Array} of {@code Enemy} objects representing all the enemies in the game map.
     */
    //enemy movement
    public Array<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * Sets the list of enemies in the game map.
     * This method can be used to initialize or update the enemy population within the game, affecting the game's difficulty and dynamics.
     *
     * @param enemies An {@code Array} of {@code Enemy} objects to be set as the current enemies in the game map.
     */
    public void setEnemies(Array<Enemy> enemies) {
        this.enemies = enemies;
    }

    /**
     * Disposes of all resources used by the {@code GameMap}, including textures and other assets associated with game objects and enemies.
     * This method ensures clean up and resource management, preventing memory leaks when the game map is no longer in use.
     */
    public void dispose() {
        // Dispose of textures or other disposable assets used by gameObjects
        for (int y = 0; y < gameObjects.length; y++) {
            for (int x = 0; x < gameObjects[y].length; x++) {
                if (gameObjects[x][y] != null) {
                    gameObjects[x][y].dispose();
                }
            }
        }

        // Dispose of textures or other disposable assets used by enemies
        for (Enemy enemy : enemies) {
            enemy.dispose();
        }

        // Dispose any other assets if necessary
    }

    /**
     * Determines the neighboring cells of a given position that are not blocked and can be moved to.
     * This method is used for pathfinding algorithms, particularly for implementing intelligent enemy movement that avoids obstacles.
     *
     * @param position The {@code Point} representing the current position for which neighbors are to be found.
     * @return A {@code List} of {@code Point} objects representing the accessible neighboring cells.
     */
    //new intelligent enemy movement
    public List<Point> getNeighbors(Point position) {
        List<Point> neighbors = new ArrayList<>();
        // Add neighbors (up, down, left, right)
        // Check bounds and if neighbor is not blocked
        if (position.x > 0 && isCellfree(position.x - 1, position.y)) { //left
            neighbors.add(new Point(position.x - 1, position.y));
        }
        if (position.x < width - 1 && isCellfree(position.x + 1, position.y)) { //right
            neighbors.add(new Point(position.x + 1, position.y));
        }
        if (position.y > 0 && isCellfree(position.x, position.y - 1)) { //up
            neighbors.add(new Point(position.x, position.y - 1));
        }
        if (position.y < height - 1 && isCellfree(position.x, position.y + 1)) { //down
            neighbors.add(new Point(position.x, position.y + 1));
        }

        return neighbors;
    }


}
