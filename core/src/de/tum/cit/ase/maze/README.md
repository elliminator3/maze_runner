<h3>README for Maze Runner Game</h3> 

**<p>Code structure</p>**
The Maze Runner Game is a project built using the LibGDX framework, designed to provide an engaging maze navigation experience. The project's code structure is organized into several key classes, each responsible for different aspects of the game, from rendering the game world to managing player input.

_MazeRunnerGame_
Acts as the central hub of the game, managing the game loop, screens, and global resources like music, sounds, and texture assets.

_MenuScreen_
Displays the main menu of the game, allowing players to start a new game, continue a previous game, or exit.

_GameScreen_
The core gameplay screen where the maze, player character, and game elements like keys, traps, and enemies are rendered, updated and interacted with.

_Hud class_
Represents the Heads-Up Display, showing the player's score, timer, collected keys, and game state messages (game over, win screen).

_GameObject class_
This abstract class acts as base for interactive elements in the game world, such as character, traps, keys, and extra lives. It handles shared functionalities like position, texture handling and rendering.

_TextureManager class_
Manages textures efficiently, ensuring that used textures are loaded only once and reused throughout the game.

_GameMap_
Loads and renders the maze layout from a .properties file, detects collision and interactions with maze elements like walls, paths, traps, and keys.

_Entry, Exit, Key, Trap, Wall, and Path class_
Extend the GameObject class to represent specific elements within the maze, each specifying a texture region for rendering.

_ExtraLife class_
Extends the GameObject class, specifies a texture region for rendering, and contains additional functionality for its state (collected or not)

_Enemy class_
Extends the GameObject class, specifies a texture region for rendering, and handles the movement of an enemy (random or intelligent)

_Node class_
Represents a node in our A* pathfinding algorithm which is necessary for intelligent enemy movement. It therefore implements the comparable interface.

_AStarPathfinding class_
Finds the shortest distance between two points on the game map and reconstructs the path which is used for intelligent enemy movement.

_Character class_
Represents the plyers character navigating through the maze. It extends the GameObject class, specifies the texture regions for animation, handles movement, collision detection with keys, and life management.

_MovementManager class_
Is responsible for processing the input of the player, checks for resulting interactions (collisions) of the character within the game and manages the consequences.

**<p>How to Run and Use the Game</p>**
1.	Setup: Ensure you have a Java Development Kit (JDK) installed and your IDE is set up for LibGDX development.
2.	Running the Game: Open the project in your IDE and run the main class (MazeRunnerGame). The game should start, displaying the menu.
3.	Navigating the Menu: Use the mouse to click on menu options to select a game level, continue a previous game, or exit the game completely.
4.	Playing the Game: Use the arrow keys (↑, ↓, ←, →) to move the character through the maze. The goal is to collect the key and reach the exit before the timer runs out. The player will encounter static traps (fountain) and dynamic enemies (ghosts) which will cause the character to lose lives. The lives are displayed as hearts on the upper center of the screen. The game must be completed within the time limit which is displayed on the upper right corner of the screen. Once the key is collected a key symbol appears on the upper left corner of the screen.

**<p>Game Mechanics</p>**
* Health: The player has a finite number of lives represented by hearts in the HUD. Interacting with traps or enemies will reduce the player's lives. Losing all lives results in a game over.
* Keys and Exits: To win, the player must collect a key and then reach the maze's exit. The HUD displays whether the key has been collected.
* Enemies and Traps: Enemies patrol the maze, and traps are hidden throughout. Once the character is in range to the ghosts, they will start chasing the character! Colliding with either results in a loss of life.

**<p>Beyond Requirements</p>**
+ Heart Display for HUD: The current health score of the character is displayed via full and empty heart images in the HUD
+ Timer: The player has a limited amount of time to navigate the maze, collect the key, and find the exit. Running out of time results in a game over. The Timer is also displayed on the HUD.
* Extra Lives: Scattered throughout the maze are extra lives that the player can collect to increase his amount of lives (to a max of 5) and thus have a higher chance of survival.
* Smart Enemy Movements: If the character is close to one of the enemies, the enemies will start chasing the character following an A* pathfinding algorithm.
* Menu Display Background: For a nicer visual game experience the background in the menu is an image that fits the general aesthetic of our game.
* "You have not stated a game yet" message: If the player tries to continue a game without ever having started one he gets this massage to understand that he has to start a game first.
* Character animation: To make the characters movement more pleasing the game includes animations for up, down, left, and right movement.

