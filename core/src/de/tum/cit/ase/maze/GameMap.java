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

    public void renderBackground(SpriteBatch batch){
        int tileSize = 16;
        for (int y = 0; y < gameObjects.length; y++) {
            for (int x = 0; x < gameObjects[y].length; x++) {
                path.render(batch, x * tileSize, y * tileSize); //draws the background with path texture
            }
        }
    }

    public GameObject getGameObjectAt(int x, int y) {
        return gameObjects[x][y];
    }

    public void removeGameObjectAt(int x, int y) {
        gameObjects[x][y] = null;
    }

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

    //ToDo: does this fit the visualization
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
        /*Rectangle characterRect = new Rectangle((int)x, (int)y, 10, 15); //tileSize //adjustments to fit visualization
        for (Enemy enemy : enemies) {
            Rectangle enemyRect = new Rectangle(((int) enemy.getX()*16), (int) enemy.getY()*16, 8, 10); //tileSize //8 fits better to visualization
            if (characterRect.overlaps(enemyRect)) {
                return true;
            }
        }
        return false;*/
    }

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

    public void removeKey(Key key) {
        // Find the position of the key in the gameObjects array and set it to null
        int tileSize = 16;
        int x = (int) (key.getX() / tileSize);
        int y = (int) (key.getY() / tileSize);
        gameObjects[x][y] = null;
    }

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

    //viewport
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    //enemy movement
    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(Array<Enemy> enemies) {
        this.enemies = enemies;
    }

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
