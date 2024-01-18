package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;

public class GameMap {
    //class to hold the tiles and layout of a map
    private GameObject[][] gameObjects;
    private int width;
    private int height;
    private Path path;
    private Array<Enemy> enemies = new Array<>(); //enemy movement
    private Array<Point> exitPoints = new Array<>(); //exitPoints

    public GameMap(String levelFilePath) {
        try {
            FileHandle fileHandle = Gdx.files.internal(levelFilePath);
            loadLevel(fileHandle);
        } catch (IOException e) {
            Gdx.app.log("Level Load Error", "Failed to load level file: " + levelFilePath, e);
        }
        path = new Path(0, 0, "basictiles.png");
    }

    //loads the level configuration from a .properties file
    public void loadLevel(FileHandle levelFilePath) throws IOException {
        Properties props = new Properties();
        props.load(levelFilePath.reader());

        // Calculate map size
        gameObjects = calculateMapSize(props);

        //extract tileType values and coordinates from .properties file
        for (String key : props.stringPropertyNames()) {
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int tileType = Integer.parseInt(props.getProperty(key));

            //fill map with the respective tileType at the given coordinates
            if (tileType == 0|tileType == 1|tileType == 2|tileType == 3|tileType == 5) {
                gameObjects[x][y] = createTile(tileType, x, y);
            }
            else if (tileType == 4) {
                Enemy enemy = new Enemy(x, y, "mobs.png");
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
    public GameObject createTile(int tileType, float x, float y) {
        return switch (tileType) {
            case 0 -> new Wall(x, y, "basictiles.png");
            case 1 -> new Entry(x, y, "basictiles.png");
            case 2 -> new Exit(x, y, "things.png");
            case 3 -> new Trap(x, y, "basictiles.png");
            case 4 -> new Path(x, y, "basictiles.png"); //tileType enemy is handled separately
            case 5 -> new Key(x, y, "objects.png");
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


    //method prevent character from moving through walls
    public boolean isCellBlocked(float x, float y){
        int tileSize = 16; // size of our tiles
        float offsetX = (34 - tileSize) / 2f;
        float offsetY = (32 - tileSize) / 2f;

        // Calculate the offset to center the 16x16 collision box within the 34x32 sprite
        int tileX = (int)((x + offsetX) / tileSize);
        int tileY = (int)((y + offsetY) / tileSize);

        //check maze bounds
        if (tileX < 0 || tileX > gameObjects.length || tileY < 0 || tileY > gameObjects[0].length) {
            return true;
            }

        //check for walls / other non-walkable objects?(enemy)
        GameObject gameObject = gameObjects[tileX][tileY];
        if(gameObject instanceof Wall || gameObject instanceof Entry){
            return true;
        }
        return false;
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
    //ToDo: auf tatsächliche Berührung anpassen
    public boolean collusionWithEnemy(float x, float y){
        Rectangle characterRect = new Rectangle((int)x, (int)y + 6, 16, 16); //tileSize //center

        for (Enemy enemy : enemies) {
            Rectangle enemyRect = new Rectangle((int) enemy.getX()*16, (int) enemy.getY()*16, 16, 16); //tileSize //repositioning
            if (characterRect.overlaps(enemyRect)) {
                return true;
            }
        }
        return false;
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
}
