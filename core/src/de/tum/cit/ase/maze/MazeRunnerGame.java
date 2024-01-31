package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;


/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It is responsible for the lifecycle of the game, managing screens, assets, and music,
 * as well as handling the game's rendering loop.
 * It extends the {@link Game} class from libGDX, which is the core of any libGDX game application.
 */
public class MazeRunnerGame extends Game {
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;
    private ShapeRenderer shapeRenderer;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;
    private Music backgroundMusic;
    private Music menuMusic;
    private Music winMusic;
    public Sound keyPickupSound;
    public Sound extraLifeSound;
    public Sound enemySound;
    public Sound trapSound;
    public Sound gameOverSound;

    // Character animation downwards
    private Animation<TextureRegion> characterDownAnimation;

    // File Chooser
    private final NativeFileChooser fileChooser;
    private String mapFilePath;
    //Texture Manager
    private TextureManager textureManager;
    private boolean showNoGameStartedMessage = false;



    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     * @param textureManager The texture manager stores all maze textures for better performance of the game
     */
    public MazeRunnerGame(NativeFileChooser fileChooser, TextureManager textureManager) {
        super();
        this.fileChooser = fileChooser;
        this.textureManager = textureManager;
    }


    /**
     * Called when the game is first created. Sets up global resources like the SpriteBatch, Skin, and music,
     * and navigates to the initial menu screen.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        shapeRenderer = new ShapeRenderer();
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        textureManager = new TextureManager();
        this.loadCharacterAnimation(); // Load character animation


        // Play some background music
        // Background sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/Juhani Junkala [Chiptune Adventures] 1. Stage 1.ogg"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f);
        backgroundMusic.play();


        goToMenu(); // Navigate to the menu screen
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/8bit Bossa.mp3")); // Replace with your menu music file
        menuMusic.setLooping(true);
        menuMusic.play();

        winMusic = Gdx.audio.newMusic(Gdx.files.internal("music/background.mp3")); // Replace with your menu music file
        winMusic.setLooping(true);


        keyPickupSound = Gdx.audio.newSound(Gdx.files.internal("music/sfx_sounds_button2.wav"));
        extraLifeSound = Gdx.audio.newSound(Gdx.files.internal("music/sfx_sounds_powerup2.wav"));
        enemySound = Gdx.audio.newSound(Gdx.files.internal("music/sfx_sounds_error9.wav"));
        trapSound = Gdx.audio.newSound(Gdx.files.internal("music/sfx_sounds_interaction1.wav"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("music/sfx_sounds_negative1.wav"));
    }

    /**
     *  It first checks if the special "no game started" message needs to be displayed, which occurs if the player
     *  attempts to resume a game without having started one. If the flag is set, it displays a centered message
     *  on a black background.
     *  Renders the current screen, delegated from the game's main render loop.
     */
    @Override
    public void render(){
        if (showNoGameStartedMessage) {
            // Clear the screen
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Ensure any previous shapeRenderer usage is properly ended before starting spriteBatch
            shapeRenderer.end();

            spriteBatch.begin();
            BitmapFont font = skin.getFont("font");
            if (font != null) {
                font.getData().setScale(1.5f); // Adjust the scale to fit the screen
                font.setColor(Color.TEAL); // Set font color to white

                String message = "YOU HAVE NOT STARTED A GAME YET";
                GlyphLayout layout = new GlyphLayout(font, message);
                float width = layout.width;
                float height = layout.height;

                // Center the message on the screen
                font.draw(spriteBatch, layout, (Gdx.graphics.getWidth() - width), (Gdx.graphics.getHeight() + height)/1.5f);
            }
            spriteBatch.end();
            // Check for Escape key press to return to the menu
            if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
                showNoGameStartedMessage = false; // Reset the flag
                goToMenu(); // Change to the menu screen
            }
        } else {
            super.render();
        }
    }

    /**
     * Plays the sound associated with picking up a key.
     */
    // Sound methods
    public void playKeyPickupSound() { keyPickupSound.play(); }

    /**
     * Plays the sound associated with encountering an enemy.
     */
    public void playEnemySound() { enemySound.play(1.0f);}

    /**
     * Plays the sound associated with encountering a trap.
     */
    public void playTrapSound() { trapSound.play();}

    /**
     * Plays the sound associated with encountering an extra life.
     */
    public void playExtraLifeSound() { extraLifeSound.play();}

    /**
     * Plays the sound associated with game over.
     */
    public void playGameOverSound() {
        if (gameOverSound != null) {
            gameOverSound.play(1.0f); // 1.0f for full volume
        }
    }

    /**
     * Plays the music associated with winning the game.
     */
    public void playWinMusic() {
        if (winMusic != null) {
            winMusic.play(); // 1.0f for full volume
        }
    }

    /**
     * plays the background music of the game.
     */
    public void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.play(); // 1.0f for full volume
        }
    }

    /**
     * Stops the background music of the game.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    /**
     * Stops the win music of the game.
     */
    public void stopWinMusic() {
        if (winMusic != null && winMusic.isPlaying()) {
            winMusic.stop();
        }
    }

    /**
     * Stops the menu music of the game.
     */
    public void stopMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
    }

    /**
     * Transitions the game to the menu screen, stopping any game-specific music and
     * playing the menu music.
     */
    public void goToMenu() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.play();
        }
        stopWinMusic();
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen

// Only hide the game screen, do not dispose of it
        if (gameScreen != null) {
            gameScreen.pause();
            // Do not dispose of or nullify gameScreen here
        }
        if (menuScreen == null) {
            menuScreen = new MenuScreen(this);
        }
        setScreen(menuScreen);
    }

    /**
     * Switches to the game screen. //ToDo delete
     */
    public void goToGame() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }

    /**
     * Resumes a paused game. If no game has been started (indicated by a null {@code mapFilePath}),
     * it displays a message to the user for a set duration, indicating that no game has been initiated.
     */
    public void resumeGame() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
        if (gameScreen == null) {

            showNoGameStartedMessage = true;
            // Set a timer to return to the menu and reset the flag
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.postRunnable(() -> {
                        goToMenu();
                        showNoGameStartedMessage = false; // Reset the flag
                    });
                }
            }, 5); // Delay in seconds

        }
        else {
            setScreen(gameScreen);
            gameScreen.resume();
            showNoGameStartedMessage = false; // Ensure the flag is reset if not showing the message
        }
    }

    /**
     * Loads the character animation from the character.png file.
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));

        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);

        // Add all frames to the animation
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkFrames);
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
        if (backgroundMusic != null) { backgroundMusic.dispose();}
        if (menuMusic != null) { menuMusic.dispose();}
        keyPickupSound.dispose();
        if (enemySound != null) enemySound.dispose();
        if (gameOverSound != null) {gameOverSound.dispose();}
        //if(textureManager != null) textureManager.dispose();
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public String getMapFilePath() {
        return mapFilePath;
    }

    // Setter method
    public void setMapFilePath(String mapFilePath) {
        this.mapFilePath = mapFilePath;
    }

    // Getter method
    public TextureManager getTextureManager() { return textureManager;}

    /**
     * Opens the file chooser interface for the user to select a maze file to play.
     * This method utilizes the native file chooser dialog to allow the user to select a file
     * with a '.properties' extension, typically used to define the layout and properties of a maze.
     *
     * Once a file is chosen, the game proceeds to load the selected maze. If the user cancels the
     * file selection or an error occurs during the file selection process, the game will handle these
     * events appropriately — by doing nothing in the case of cancellation, or logging an error message
     * if an error occurs.
     */
    public void chooseMazeFile() {
        var fileChooserConfig = new NativeFileChooserConfiguration();
        fileChooserConfig.title = "Pick a maze file";
        fileChooserConfig.intent = NativeFileChooserIntent.OPEN;
        fileChooserConfig.nameFilter = (file, name) -> name.endsWith("properties");


        FileHandle initialDirectory = Gdx.files.absolute(System.getProperty("user.home"));
        fileChooserConfig.directory = initialDirectory;
        fileChooser.chooseFile(fileChooserConfig, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle fileHandle) {
                setMapFilePath(fileHandle.path());
                goToGameWithNewMap(fileHandle.path());
            }

            @Override
            public void onCancellation() {
            }

            @Override
            public void onError(Exception exception) {
                System.err.println("Error picking maze file: " + exception.getMessage());            }
        });
    }

    /**
     * Starts a new game with a maze map selected by the user.
     *
     * @param mapFilePath The file path to the selected maze map.
     */
    public void goToGameWithNewMap(String mapFilePath) {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
        }
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose of the current game screen if it exists
        }
        gameScreen = new GameScreen(this); // Create a new game screen with the chosen map
        setScreen(gameScreen); // Set the new game screen
    }


}