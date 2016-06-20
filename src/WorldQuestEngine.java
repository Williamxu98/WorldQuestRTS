import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Code for the main engine in the game that includes the constant framerate
 * process and the paint component
 * 
 * @author William Xu & Tony Wu
 * @version December 28, 2014
 *
 */

public class WorldQuestEngine extends JPanel implements Runnable,
		MouseListener, MouseMotionListener, KeyListener
{

	// Width and height of the screen
	public final int SCREEN_WIDTH = 1024;
	public final int SCREEN_HEIGHT = 768;

	// Name of the current player
	public String name;
	Player[] players = new Player[100];

	boolean isMute;

	// The music to use, music to use determines which music to play (0 is none,
	// 1 is menu music, 2 is game music)
	File mainMenuMusic;
	Clip mainMenuMusicClip;
	File gameMusic;
	Clip gameMusicClip;

	// Set which screen is currently active
	// 0 = Main Menu
	// 1 = Game
	// 2 = Leaderboard
	// 3 = Enter Name
	// 4 = Game Mode
	private int currentScreen = -1;

	// Game mode set
	// 0 = Endless
	// 1 = Competitive
	private int gameMode;

	private boolean isPaused = false;

	// Number of milliseconds between frames
	public final int DELAY = 30;

	// Set the font styles to be used in the program
	Font arial48Bold = new Font("Arial", Font.BOLD, 48);
	Font arial36Bold = new Font("Arial", Font.BOLD, 36);
	Font arial36 = new Font("Arial", Font.PLAIN, 36);
	Font arial24Bold = new Font("Arial", Font.BOLD, 24);
	Font arial24Italic = new Font("Arial", Font.ITALIC, 24);
	Font arial24 = new Font("Arial", Font.PLAIN, 24);
	Font arial20Italic = new Font("Arial", Font.ITALIC, 20);
	Font arial20 = new Font("Arial", Font.PLAIN, 20);

	FontMetrics currentFontMetrics;

	// The x and y coordinate of the mouse at a given time
	private int mouseX, mouseY;
	private boolean mouseMoved;
	private boolean mousePressed;

	// Images to be imported and used later
	private Image transparent, customCursor, customCursorPressed,
			demolishCursor, skyBackground, building, buildingGhost,
			buildingGhostError, dirt, fadeRight, fadeLeft, fade;

	// Grass is an array to make way for more grass textures in the future
	private Image[] grass = new Image[1];
	private Image[] clouds = new Image[4];

	Image[] images = new Image[50];

	// Variables to store each image
	private Image homeButton, homeButtonPressed, demolishButton,
			demolishButtonPressed, harvestButton, harvestButtonPressed,
			collectLumberButton, collectLumberButtonPressed,
			collectStoneButton, collectStoneButtonPressed,
			pauseButton, pauseButtonPressed;

	private Image menuBase, menuSlideBase;
	private Image menuLeft, menuLeftPressed, menuRight, menuRightPressed;

	private Image newGameButton, newGameButtonPressed;
	private Image leaderButton, leaderButtonPressed;
	private Image backButton, backButtonPressed;
	private Image muteButton, muteButtonPressed;
	private Image title;
	private Image endlessButton, endlessButtonPressed;
	private Image competitiveButton, competitiveButtonPressed;

	private Image dirtHouseTile, dirtHouseTilePressed;
	private Image woodHouseTile, woodHouseTilePressed;
	private Image watchTowerTile, watchTowerTilePressed;
	private Image wheatFieldTile, wheatFieldTilePressed;
	private Image stoneMineTile, stoneMineTilePressed;
	private Image lumberMillTile, lumberMillTilePressed;
	private Image tavernTile, tavernTilePressed;
	private Image black;

	// This was originally an array of earth images animated in the background,
	// with current Image being the one that is currently being draw in the
	// background
	private Image[] earths = new Image[1];

	public Thread animator;

	// Array of building objects for placement
	Building[] buildings = new Building[500];
	private boolean canPlace;

	// Array of buttons
	Button[] buttons = new Button[50];

	// Background objects and array of them
	public final int NO_OF_DIRT = 20;
	public final int NO_OF_GRASS = 20;
	public final int NO_OF_CLOUDS = 100;

	// Wind direction for blowing the clouds
	private int windDirection;

	Background[] backgrounds = new Background[NO_OF_DIRT + NO_OF_GRASS
			+ NO_OF_CLOUDS];

	private String buildingSelected = "dirtHouse";
	private int selectedWidth;
	private int selectedHeight;

	// Menu tile area coordinates
	private int scrollAreax1 = 83;
	private int scrollAreay1 = 23;
	private int scrollAreax2 = 745;
	private int scrollAreay2 = 174;

	// Position of the screen in the world and its scroll speed (positive for
	// right, negative for left, 0 for still)
	public int worldX;
	private int worldSpeed;
	private boolean canScroll;

	// Speed at which the menu scrolls. A negative value for left and a positive value for right.
	private int menuScroll;

	// Limiting the view of the screen to only what the town hall and
	// watchtowers can see
	public int viewLimitx1, viewLimitx2;
	private boolean doesLimitView;

	// The x position of home in the world, whether the home button was pressed,
	// and whether the home location was set
	private int homeX;
	private boolean goHome;
	private boolean homeSet;

	private boolean buttonPressedThisTurn;

	private boolean canDemolish;

	// Y coordinate of the ground
	private int groundHeight;

	// Stops premature town hall placement
	private boolean firstClick;

	// Stores whether or not the keyboard is scrolling the screen so it doesn't
	// interfere with the mouse scrolling the screen
	private boolean keyboardIsScrolling;

	// Resources on hand
	public int currentGold;
	public int currentLumber;
	public int currentStone;

	public int currentFood;
	public boolean isStarving;

	public int harvestAmount;

	public int currentPopulation;

	// Happiness is expressed as a percentage out of 100 to the player
	public int currentHappiness;
	public int totalHappiness;

	public int currentDay;
	public int currentHours;
	public int currentFrames;

	public int incomePerCitizen = 10;

	// The messages being displayed
	Text[] texts = new Text[1000];

	// Scrolling the leaderboard
	int leaderBoardScroll = 0;
	int nextLeaderBoardLine;

	/**
	 * Initiate all the variables and arrays when the game begins
	 * 
	 * @throws IOException To catch errors out of the programmer's control, such
	 *             as tampering with the game files
	 * @throws UnsupportedAudioFileException To catch errors due to missing
	 *             audio files or files unsupported by java
	 * @throws LineUnavailableException To catch errors in the text file
	 */
	public void gameStart() throws UnsupportedAudioFileException, IOException,
			LineUnavailableException
	{

		currentScreen = -1;
		setBackground(Color.black);
		setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		setDoubleBuffered(true);

		// Add mouse listeners and Key Listeners to the game
		addMouseListener(this);
		setFocusable(true);
		addKeyListener(this);
		requestFocusInWindow();

		// Check for movement of the mouse
		addMouseMotionListener(this);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		loadImages();

		isMute = false;
		leaderBoardScroll = 0;
		nextLeaderBoardLine = 0;
		importMusic();
		toggleMusic(1);

		currentScreen = 0;

		// Initialize the buttons and player objects
		for (int index = 0; index < buttons.length; index++)
		{
			buttons[index] = new Button();
		}

		for (int index = 0; index < players.length; index++)
		{
			players[index] = new Player();
		}

		loadLeaderBoard();

		// Create menu buttons
		buttons[31].create(360, 470, newGameButton, newGameButtonPressed);
		buttons[32].create(900, 50, muteButtonPressed, muteButton);
		buttons[33].create(410, 630, leaderButton, leaderButtonPressed);
		buttons[37].create(80, 500, endlessButton, endlessButtonPressed);
		buttons[38].create(550, 500, competitiveButton,
				competitiveButtonPressed);

		// Make the cursor transparent and draw over it
		Cursor cursor = toolkit.createCustomCursor(transparent, new Point(
				getX(), getY()), "img");
		setCursor(cursor);

	}

	/**
	 * Loads the leaderboard text file from the folder and uses it to display
	 * the leaderboard of players
	 * 
	 * @throws FileNotFoundException
	 */
	public void loadLeaderBoard() throws FileNotFoundException
	{

		Scanner inFile = new Scanner(new File("leaderBoard.txt"));

		int noOfPlayers = 0;

		// Counting the number of lines in the file
		if (inFile.hasNextInt())
		{
			noOfPlayers = inFile.nextInt();
		}

		if (noOfPlayers > 0)
		{
			inFile.nextLine();
		}

		// Going through each line
		for (int index = 0; index < noOfPlayers; index++)
		{
			// Reading in the next line from the text file
			// and creating a scanner that can go through each integer or string
			String line = inFile.nextLine();
			Scanner scan = new Scanner(line);

			String scannedName = scan.nextLine();
			scan.close();

			line = inFile.nextLine();
			scan = new Scanner(line);

			// Get the score (population) and days, and create a player object
			// with them
			int scannedScore = scan.nextInt();
			int scannedDays = scan.nextInt();
			players[index].setPlayer(scannedName, scannedScore, scannedDays);

			if (scan.hasNextLine())
			{
				scan.nextLine();
			}
			scan.close();
		}

		inFile.close();

	}

	/**
	 * Saves the leaderboard of players to an external text file
	 * 
	 * @throws IOException To catch errors from file changing or corruption
	 */
	public void saveLeaderBoard() throws IOException
	{
		PrintWriter outFile = new PrintWriter(new FileWriter("leaderBoard.txt"));

		int noOfPlayers = 0;

		for (int index = 0; index < players.length; index++)
		{
			if (players[index].exists)
			{
				noOfPlayers++;
			}
		}

		outFile.println(noOfPlayers);

		for (int index = 0; index < players.length; index++)
		{
			if (players[index].exists)
			{
				outFile.println(players[index].name);
				outFile.println(players[index].score + " "
						+ players[index].days);
			}
		}

		outFile.close();

	}

	/**
	 * Load the music from files
	 * @throws UnsupportedAudioFileException if audio file type is wrong
	 * @throws IOException if the files have been changed
	 * @throws LineUnavailableException if there is not a new line when it scans
	 *             for one
	 */
	public void importMusic() throws UnsupportedAudioFileException,
			IOException, LineUnavailableException
	{
		mainMenuMusic = new File("childrenOfAiur.wav");
		gameMusic = new File("garden.wav");

		AudioInputStream audioStream = AudioSystem
				.getAudioInputStream(mainMenuMusic);
		AudioFormat format = audioStream.getFormat();

		DataLine.Info info = new DataLine.Info(Clip.class, format);

		mainMenuMusicClip = (Clip) AudioSystem.getLine(info);
		mainMenuMusicClip.open(audioStream);

		audioStream = AudioSystem.getAudioInputStream(gameMusic);
		format = audioStream.getFormat();

		info = new DataLine.Info(Clip.class, format);

		gameMusicClip = (Clip) AudioSystem.getLine(info);
		gameMusicClip.open(audioStream);
	}

	/**
	 * Play selected music
	 * @param musicToUse the music to use
	 */
	public void toggleMusic(int musicToUse)
	{
		if (!isMute)
		{
			if (musicToUse == 1)
			{
				gameMusicClip.stop();
				mainMenuMusicClip.loop(10000);
			}
			else if (musicToUse == 2)
			{
				mainMenuMusicClip.stop();
				gameMusicClip.loop(10000);
			}
			else if (musicToUse == 0)
			{
				mainMenuMusicClip.stop();
			}
		}
	}

	/**
	 * Reset every object to start a new game
	 */

	public void newGame()
	{

		// Initialize all the variables
		// groundHeight is the Y coordinate of the ground
		groundHeight = 650;

		mousePressed = false;

		goHome = false;
		homeX = 0;
		homeSet = false;

		canDemolish = false;

		canPlace = true;
		canScroll = true;

		// Random wind direction
		if ((int) (Math.random() * 2) == 0)
		{
			windDirection = -1;
		}
		else
		{
			windDirection = 1;
		}

		buildingSelected = "";

		// Position of the screen in the world and its scroll speed (positive
		// for
		// right, negative for left, 0 for still)
		worldX = 0;
		worldSpeed = 0;

		// Stores whether or not the keyboard is scrolling the screen so it
		// doesn't
		// interfere with the mouse scrolling the screen
		keyboardIsScrolling = false;

		menuScroll = 0;

		// Set it to 1000 to start for testing purposes
		currentGold = 1500;
		currentLumber = 750;
		currentStone = 500;
		currentFood = 500;
		isStarving = false;

		currentPopulation = 0;
		currentHappiness = 100;
		totalHappiness = 100;

		harvestAmount = 0;
		currentDay = 1;
		currentHours = 24;
		currentFrames = 0;

		// View Limits
		doesLimitView = false;

		// Load the objects
		for (int index = 0; index < buildings.length; index++)
		{
			buildings[index] = new Building();
		}

		for (int index = 0; index < texts.length; index++)
		{
			texts[index] = new Text();
		}

		// Create Buttons
		buttons[0].create(853, 260, homeButton, homeButtonPressed);
		buttons[1].create(906, 260, demolishButton, demolishButtonPressed);
		buttons[36].create(959, 260, pauseButton, pauseButtonPressed);
		buttons[28].create(853, 313, collectLumberButton,
				collectLumberButtonPressed);
		buttons[29].create(906, 313, collectStoneButton,
				collectStoneButtonPressed);
		buttons[30].create(959, 313, harvestButton, harvestButtonPressed);
		buttons[2].create(20, 18, menuLeft, menuLeftPressed);
		buttons[3].create(760, 18, menuRight, menuRightPressed);

		// building tiles are 130 pixels apart
		buttons[4].create(82, 23, dirtHouseTile, dirtHouseTilePressed);
		buttons[5].create(212, 23, woodHouseTile, woodHouseTilePressed);
		buttons[6].create(342, 23, watchTowerTile, watchTowerTilePressed);
		buttons[7].create(472, 23, wheatFieldTile, wheatFieldTilePressed);
		buttons[8].create(602, 23, lumberMillTile, lumberMillTilePressed);
		buttons[9].create(732, 23, stoneMineTile, stoneMineTilePressed);
		buttons[10].create(862, 23, tavernTile, tavernTilePressed);

		// Create Background elements
		int backgroundX = -(dirt.getWidth(null) * (NO_OF_GRASS / 2));

		for (int index = 0; index < NO_OF_DIRT; index++)
		{
			backgrounds[index] = new Background("dirt", backgroundX,
					groundHeight, dirt);
			backgroundX += dirt.getWidth(null);
		}

		backgroundX = -(grass[0].getWidth(null) * (NO_OF_GRASS / 2));

		for (int index = NO_OF_DIRT; index < NO_OF_GRASS + NO_OF_DIRT; index++)
		{
			backgrounds[index] = new Background("grass", backgroundX,
					groundHeight, grass[0]);
			backgroundX += grass[0].getWidth(null);
		}

		backgroundX = -(500 * (NO_OF_CLOUDS / 2));

		for (int index = NO_OF_GRASS + NO_OF_DIRT; index < NO_OF_GRASS
				+ NO_OF_DIRT + NO_OF_CLOUDS; index++)
		{
			int random = (int) (Math.random() * 4);

			backgrounds[index] = new Background("cloud", backgroundX,
					(int) (Math.random() * 400) - 300, clouds[random]);
			backgroundX += (int) (Math.random() * 1000);
		}

		// Make the user place the town house before anything begins
		changeCurrentBuildingTo("townHall");

	}

	// Constructor that is used when the engine is called from the main program
	public WorldQuestEngine() throws UnsupportedAudioFileException,
			IOException, LineUnavailableException
	{
		Building.setWorld(this);
		gameStart();
	}

	/**
	 * Load all the images for the game
	 */
	public void loadImages()
	{

		// Catch exceptions if reading the images doesn't work
		try
		{

			images[0] = ImageIO.read(new File("dirtHouse.png"));
			images[1] = ImageIO.read(new File("dirtHouseGhost.png"));
			images[2] = ImageIO.read(new File("dirtHouseGhostError.png"));

			images[3] = ImageIO.read(new File("woodenHouse.png"));
			images[4] = ImageIO.read(new File("woodenHouseGhost.png"));
			images[5] = ImageIO.read(new File("woodenHouseGhostError.png"));

			images[6] = ImageIO.read(new File("townHall.png"));
			images[7] = ImageIO.read(new File("townHallGhost.png"));
			images[8] = ImageIO.read(new File("townHallGhostError.png"));

			images[9] = ImageIO.read(new File("watchTower.png"));
			images[10] = ImageIO.read(new File("watchTowerGhost.png"));
			images[11] = ImageIO.read(new File("watchTowerGhostError.png"));

			images[12] = ImageIO.read(new File("wheatField1.png"));
			images[13] = ImageIO.read(new File("wheatField4Ghost.png"));
			images[14] = ImageIO.read(new File("wheatField4GhostError.png"));
			images[15] = ImageIO.read(new File("wheatField2.png"));
			images[16] = ImageIO.read(new File("wheatField3.png"));
			images[17] = ImageIO.read(new File("wheatField4.png"));

			images[18] = ImageIO.read(new File("stoneMine.png"));
			images[19] = ImageIO.read(new File("stoneMineGhost.png"));
			images[20] = ImageIO.read(new File("stoneMineGhostError.png"));

			images[21] = ImageIO.read(new File("lumberMill.png"));
			images[22] = ImageIO.read(new File("lumberMillGhost.png"));
			images[23] = ImageIO.read(new File("lumberMillGhostError.png"));
			images[24] = ImageIO.read(new File("tavern.png"));
			images[25] = ImageIO.read(new File("tavernGhost.png"));
			images[26] = ImageIO.read(new File("tavernGhostError.png"));

			dirt = ImageIO.read(new File("dirt.jpg"));
			grass[0] = ImageIO.read(new File("grass2.png"));
			clouds[0] = ImageIO.read(new File("cloud1.png"));
			clouds[1] = ImageIO.read(new File("cloud2.png"));
			clouds[2] = ImageIO.read(new File("cloud3.png"));
			clouds[3] = ImageIO.read(new File("cloud4.png"));
			skyBackground = ImageIO.read(new File("skyBackground.png"));

			transparent = ImageIO.read(new File("transparent.png"));
			customCursor = ImageIO.read(new File("customCursor.png"));
			customCursorPressed = ImageIO.read(new File("customCursor2.png"));

			fadeRight = ImageIO.read(new File("fadeRight.png"));
			fadeLeft = ImageIO.read(new File("fadeLeft.png"));
			demolishCursor = ImageIO.read(new File("demolishCursor.png"));
			fade = ImageIO.read(new File("fade.png"));

			// = ImageIO.read(new File("menuBackground.png"));
			newGameButton = ImageIO.read(new File("newGameButton.png"));
			newGameButtonPressed = ImageIO.read(new File(
					"newGameButtonPressed.png"));
			muteButton = ImageIO.read(new File("muted.png"));
			muteButtonPressed = ImageIO.read(new File("unmuted.png"));
			title = ImageIO.read(new File("title.png"));
			leaderButton = ImageIO.read(new File("leaderboardButton.png"));
			leaderButtonPressed = ImageIO.read(new File(
					"leaderboardButtonPressed.png"));
			black = ImageIO.read(new File("black.png"));
			backButton = ImageIO.read(new File("backButton.png"));
			backButtonPressed = ImageIO.read(new File("backButtonPressed.png"));
			endlessButton = ImageIO.read(new File("endlessButton.png"));
			endlessButtonPressed = ImageIO.read(new File(
					"endlessButtonPressed.png"));
			competitiveButton = ImageIO.read(new File("competitiveButton.png"));
			competitiveButtonPressed = ImageIO.read(new File(
					"competitiveButtonPressed.png"));

			menuBase = ImageIO.read(new File("menuBase.PNG"));
			menuSlideBase = ImageIO.read(new File("menuSlideBase.PNG"));
			menuLeft = ImageIO.read(new File("menuLeft.PNG"));
			menuLeftPressed = ImageIO.read(new File("menuLeftPressed.PNG"));
			menuRight = ImageIO.read(new File("menuRight.PNG"));
			menuRightPressed = ImageIO.read(new File("menuRightPressed.PNG"));
			dirtHouseTile = ImageIO.read(new File("dirtHouseTile.PNG"));
			dirtHouseTilePressed = ImageIO.read(new File(
					"dirtHouseTilePressed.PNG"));
			woodHouseTile = ImageIO.read(new File("woodHouseTile.PNG"));
			woodHouseTilePressed = ImageIO.read(new File(
					"woodHouseTilePressed.PNG"));
			watchTowerTile = ImageIO.read(new File("watchTowerTile.PNG"));
			watchTowerTilePressed = ImageIO.read(new File(
					"watchTowerTilePressed.PNG"));
			wheatFieldTile = ImageIO.read(new File("wheatFieldTile.PNG"));
			wheatFieldTilePressed = ImageIO.read(new File(
					"wheatFieldTilePressed.PNG"));
			stoneMineTile = ImageIO.read(new File("stoneMineTile.PNG"));
			stoneMineTilePressed = ImageIO.read(new File(
					"stoneMineTilePressed.PNG"));
			lumberMillTile = ImageIO.read(new File("LumberMillTile.PNG"));
			lumberMillTilePressed = ImageIO.read(new File(
					"LumberMillTilePressed.PNG"));
			tavernTile = ImageIO.read(new File("tavernTile.PNG"));
			tavernTilePressed = ImageIO.read(new File("tavernTilePressed.PNG"));

			homeButton = ImageIO.read(new File("homeButton.PNG"));
			homeButtonPressed = ImageIO.read(new File("homeButtonPressed.PNG"));
			demolishButton = ImageIO.read(new File("demolishButton.PNG"));
			demolishButtonPressed = ImageIO.read(new File(
					"demolishButtonPressed.PNG"));
			collectLumberButton = ImageIO.read(new File(
					"collectLumberButton.PNG"));
			collectLumberButtonPressed = ImageIO.read(new File(
					"collectLumberButtonPressed.PNG"));
			collectStoneButton = ImageIO
					.read(new File("collectStoneButton.PNG"));
			collectStoneButtonPressed = ImageIO.read(new File(
					"collectStoneButtonPressed.PNG"));
			harvestButton = ImageIO.read(new File("harvestButton.PNG"));
			harvestButtonPressed = ImageIO.read(new File(
					"harvestButtonPressed.PNG"));
			pauseButton = ImageIO.read(new File("pauseButton.PNG"));
			pauseButtonPressed = ImageIO
					.read(new File("pauseButtonPressed.PNG"));
			earths[0] = ImageIO.read(new File("earth.jpg"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}


				



	}

	@Override
	/**
	 *  Add the animating thread
	 */
	public void addNotify()
	{
		super.addNotify();

		animator = new Thread(this);
		animator.start();
	}

	@Override
	/**
	 *  Draw everything
	 */
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		// Setting the first font to use
		graphics.setFont(arial24Bold);

		// Importing font metrics for fonts
		currentFontMetrics = graphics.getFontMetrics(arial24Bold);

		// Draw menu if menu screens are selected
		if (currentScreen == 0 || currentScreen == 2 || currentScreen == 3
				|| currentScreen == 4)
		{
			drawMenu(graphics);
		}

		// Draw game if game selected
		else if (currentScreen == 1)
		{
			draw(graphics);

		}
	}

	/**
	 * Updates the current frames and current day based on the game speed Also
	 * updates the happiness when it is being drained by starvation
	 */
	public void passTime()
	{
		// Count the number of frames in the game (based on the delay)
		currentFrames++;

		// Every second is an hour
		if (currentFrames >= DELAY)
		{
			currentFrames = 0;
			currentHours--;

			// Reduce happiness quickly (3% per second) if no food left
			if (currentFood == 0)
			{
				if (currentHappiness > (int) ((1.0 * totalHappiness / 30) + 0.5))
				{
					currentHappiness -= (int) ((1.0 * totalHappiness / 30) + 0.5);
				}
				else
					currentHappiness = 0;

				isStarving = true;
			}
			else
				isStarving = false;

		}

		// Every 24 hours (seconds) one day passes
		if (currentHours <= 0)
		{
			currentHours = 24;
			currentDay++;
		}

		// Cycle through the time for each building and update it
		for (int index = 0; index < buildings.length; index++)
		{

			if (buildings[index].exists)
			{
				buildings[index].cycle();
			}
		}

		// Cycle through the time elapsed for each text object in order for them
		// to check their delay and float
		for (int index = 0; index < texts.length; index++)
		{
			if (texts[index].exists)
			{
				texts[index].passTime();
			}
		}
	}

	/**
	 * Move each object that snaps to the world while the user is scrolling and
	 * scroll the screen if the user moves the mouse to the edges of the screen
	 */
	public void scrollScreen()
	{

		// Scroll at the scroll speed (worldSpeed) when current world view limit
		// is not surpassed
		if ((worldSpeed < 0 && (SCREEN_WIDTH < viewLimitx2))
				|| (worldSpeed > 0 && (0 > viewLimitx1)) || !doesLimitView)
		{
			if (!canScroll)
			{
				canScroll = true;
			}
			int originalSpeed = worldSpeed;

			if (doesLimitView)
			{
				if (worldSpeed > 0 && viewLimitx1 + worldSpeed > 0)
				{
					worldSpeed = -viewLimitx1;
				}
				else if (worldSpeed < 0
						&& viewLimitx2 + worldSpeed < SCREEN_WIDTH)
				{
					worldSpeed = -(viewLimitx2 - SCREEN_WIDTH);
				}
			}

			if (homeSet)
			{
				worldX += worldSpeed;

				viewLimitx2 += worldSpeed;
				viewLimitx1 += worldSpeed;
			}

			for (int index = 0; index < backgrounds.length; index++)
			{
				backgrounds[index].x += worldSpeed;
			}

			for (int index = 0; index < buildings.length; index++)
			{
				if (buildings[index].exists)
				{
					buildings[index].x += worldSpeed;
				}
			}

			for (int index = 0; index < texts.length; index++)
			{
				if (texts[index].doesScroll)
				{
					texts[index].x += worldSpeed;
				}
			}

			worldSpeed = originalSpeed;
		}

		// Don't scroll and show message telling why you can't scroll
		else
		{
			if (canScroll)
			{
				canScroll = false;
				if (!canScroll && worldSpeed != 0)
				{
					displayMessage("Build more watchtowers to increase world vision!");
				}
			}
		}

		// Scroll the screen when the mouse is moved to an edge of the screen
		if (!keyboardIsScrolling && mouseMoved)
		{
			if (mouseX <= 25 && (worldX >= viewLimitx1 || !doesLimitView)
					&& mouseY > scrollAreay2)
			{
				worldSpeed = 25;
			}
			else if (mouseX >= SCREEN_WIDTH - 25
					&& (worldX + SCREEN_WIDTH <= viewLimitx2 || !doesLimitView)
					&& mouseY > scrollAreay2)
			{
				worldSpeed = -25;
			}
			else
			{
				worldSpeed = 0;
			}
		}

		if (goHome)
		{
			for (int index = 0; index < backgrounds.length; index++)
			{
				backgrounds[index].x += homeX - worldX;
			}

			for (int index = 0; index < buildings.length; index++)
			{
				if (buildings[index].exists)
				{
					buildings[index].x += homeX - worldX;
				}
			}
			viewLimitx2 += homeX - worldX;
			viewLimitx1 += homeX - worldX;

			worldX = homeX;

			goHome = false;
		}

		for (int index = 4; index < 27; index++)
		{
			buttons[index].move(menuScroll);

		}

		if (pointIsTouching(mouseX, mouseY, buttons[2].x1, buttons[2].x2,
				buttons[2].y1, buttons[2].y2)
				&& buttons[4].x + 10 < scrollAreax1)

		{
			menuScroll = 10;
			buttons[2].pressed();
		}
		else if (pointIsTouching(mouseX, mouseY, buttons[3].x1,
				buttons[3].x2, buttons[3].y1, buttons[3].y2)
				&& buttons[10].x2 - 5 > scrollAreax2)
		{
			menuScroll = -10;
			buttons[3].pressed();
		}
		else
		{
			buttons[2].released();
			buttons[3].released();
			menuScroll = 0;
		}

	}

	/**
	 * Move background ground objects
	 */
	public void moveObjects()
	{

		// Grass
		for (int index = 0; index < NO_OF_GRASS + NO_OF_DIRT; index++)
		{
			if (backgrounds[index].x + SCREEN_WIDTH > NO_OF_DIRT / 2 * 256)
			{
				backgrounds[index].x -= NO_OF_DIRT / 2 * 256;
			}
			else if (backgrounds[index].x - SCREEN_WIDTH < -(NO_OF_DIRT / 2 * 256))
			{
				backgrounds[index].x += NO_OF_DIRT / 2 * 256;
			}
		}

		// Dirt
		for (int index = NO_OF_GRASS + NO_OF_DIRT; index < NO_OF_GRASS
				+ NO_OF_DIRT + NO_OF_CLOUDS; index++)
		{
			backgrounds[index].x += backgrounds[index].speed * windDirection;

			if (backgrounds[index].x > (500 * (NO_OF_CLOUDS / 2)))
			{
				backgrounds[index].x -= 500 * NO_OF_CLOUDS;
			}
			else if (backgrounds[index].x < -(500 * (NO_OF_CLOUDS / 2)))
			{
				backgrounds[index].x += 500 * NO_OF_CLOUDS;
			}
		}

	}

	/**
	 * Process game results at game end
	 */
	public void roundEnd()
	{

		// Save the player's population data and put it in the correct position
		// on the leaderboard only if the player is in competitive mode and
		// survived the entire 15 days
		if (gameMode == 1)
		{
			int freeIndex = -1;
			for (int index = 0; index < players.length && freeIndex < 0; index++)
			{
				if (!(players[index].exists))
				{
					freeIndex = index;
				}
			}

			if (freeIndex < 0)
			{
				freeIndex = players.length - 1;
			}

			players[freeIndex].setPlayer(name, currentPopulation, currentDay);

			// Order the players from best to worst
			for (int index = freeIndex - 1; index >= 0; index--)
			{
				if (currentDay > players[index].days
						|| (currentDay == players[index].days && currentPopulation > players[index].score))
				{
					players[freeIndex].setPlayer(players[index].name,
							players[index].score, players[index].days);
					players[index].setPlayer(name, currentPopulation,
							currentDay);
					freeIndex = index;
				}
			}
		}

	}

	/**
	 * Draw the menu components
	 * 
	 */
	public void drawMenu(Graphics graphics)
	{
		// DRAW MENUS

		// draw spinning earth background
		graphics.drawImage(earths[0], -180, 0, this);

		// Main Menu
		if (currentScreen == 0)
		{
			graphics.setFont(arial20);
			graphics.setColor(Color.white);
			graphics.drawString("William Xu                     Tony Wu", 370,
					390);
			graphics.drawImage(title, 50, 50, this);

			for (int index = 31; index <= 33; index++)
			{
				graphics.drawImage(buttons[index].image, buttons[index].x,
						buttons[index].y, this);
			}

			if (pointIsTouching(mouseX, mouseY, buttons[31].x1, buttons[31].x2,
					buttons[31].y1, buttons[31].y2))
			{
				buttons[31].pressed();
			}
			else
			{
				buttons[31].released();
			}

			if (pointIsTouching(mouseX, mouseY, buttons[33].x1, buttons[33].x2,
					buttons[33].y1, buttons[33].y2))
			{
				buttons[33].pressed();
			}
			else
			{
				buttons[33].released();
			}
		}

		// Leaderboard
		else if (currentScreen == 2)
		{

			nextLeaderBoardLine += leaderBoardScroll;

			graphics.setFont(arial48Bold);
			graphics.setColor(Color.white);
			graphics.drawString("LEADERBOARD", 180, 200 + nextLeaderBoardLine);

			graphics.setFont(arial24Bold);
			graphics.setColor(Color.white);

			graphics.drawString("Scroll with the", 824, 400);
			graphics.drawString("^ UP and DOWN v", 810, 430);
			graphics.drawString("arrow keys", 842, 460);

			graphics.drawString("     NAME" + "              END POPULATION "

					+ "              NUMBER OF DAYS ", 100,
					275 + nextLeaderBoardLine);

			graphics.setFont(arial24);

			int nextLine = 0;

			for (int index = 0; index < players.length; index++)
			{
				if (players[index].exists)
				{

					graphics.drawString(index + 1 + ". " + players[index].name,
							100, 300 + nextLeaderBoardLine + nextLine);
					graphics.drawString("" + players[index].score, 400, 300
							+ nextLeaderBoardLine + nextLine);
					graphics.drawString("" + players[index].days, 720, 300
							+ nextLeaderBoardLine + nextLine);
					nextLine += 30;
				}
			}

			graphics.drawImage(buttons[34].image, buttons[34].x, buttons[34].y,
					this);

			if (pointIsTouching(mouseX, mouseY, buttons[34].x1, buttons[34].x2,
					buttons[34].y1, buttons[34].y2))
			{
				buttons[34].pressed();
			}
			else
			{
				buttons[34].released();
			}
		}

		else if (currentScreen == 3)
		{
			graphics.setFont(arial36Bold);
			graphics.setColor(Color.white);
			graphics.drawString("Enter your name:", 180, 390);

			graphics.setFont(arial36);
			graphics.drawString(name + "|", 500, 390);
		}

		else if (currentScreen == 4)
		{
			graphics.setFont(arial36Bold);
			graphics.setColor(Color.white);
			currentFontMetrics = graphics.getFontMetrics(arial36Bold);
			graphics.drawString("Select Game Mode:", SCREEN_WIDTH / 2
					- currentFontMetrics.stringWidth("Select Game Mode") / 2,
					300);

			graphics.drawImage(buttons[37].image, buttons[37].x, buttons[37].y,
					this);
			graphics.drawImage(buttons[38].image, buttons[38].x, buttons[38].y,
					this);

			if (pointIsTouching(mouseX, mouseY, buttons[37].x1, buttons[37].x2,
					buttons[37].y1, buttons[37].y2))
			{
				buttons[37].pressed();
				graphics.setFont(arial24);
				graphics.setColor(new Color(0, 255, 255));
				currentFontMetrics = graphics.getFontMetrics(arial24);
				graphics.drawString(
						"Your town lasts forever unless your people revolt",
						SCREEN_WIDTH
								/ 2
								- currentFontMetrics
										.stringWidth("Your town lasts forever unless your people revolt")
								/ 2,
						340);
				graphics.drawString(
						"This does not qualify you for the leaderboard",
						SCREEN_WIDTH
								/ 2
								- currentFontMetrics
										.stringWidth("This does not qualify you for the leaderboard")
								/ 2,
						365);
			}
			else
			{
				buttons[37].released();

			}

			if (pointIsTouching(mouseX, mouseY, buttons[38].x1, buttons[38].x2,
					buttons[38].y1, buttons[38].y2))
			{
				buttons[38].pressed();
				graphics.setFont(arial24);
				graphics.setColor(Color.green);
				currentFontMetrics = graphics.getFontMetrics(arial24);
				graphics.drawString(
						"Populate your town as much as possible in 15 days",
						SCREEN_WIDTH
								/ 2
								- currentFontMetrics
										.stringWidth("Populate your town as much as possible in 15 days")
								/ 2,
						340);
				graphics.drawString(
						"You are ranked on the leaderboard based on your days survived and population",
						SCREEN_WIDTH
								/ 2
								- currentFontMetrics
										.stringWidth("You are ranked on the leaderboard based on your days survived and population")
								/ 2,
						365);
			}

			else
			{
				buttons[38].released();
			}

		}

		// Draw Custom Mouse Cursor
		if (buttons[1].isPressed && currentScreen == 1)
		{
			graphics.drawImage(demolishCursor, mouseX - 16, mouseY - 16, this);
		}
		else if (!mousePressed)
		{
			graphics.drawImage(customCursor, mouseX, mouseY, this);
		}
		else
		{
			graphics.drawImage(customCursorPressed, mouseX, mouseY, this);
		}

		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * Draw each necessary image on the screen at the set framerate
	 * 
	 * @param graphics the graphics class that contains all the draw methods
	 *            needed
	 */
	public void draw(Graphics graphics)
	{

		// draw background elements
		graphics.drawImage(skyBackground, 0, 0, this);
		for (int index = 0; index < backgrounds.length; index++)
		{
			if (onScreen(backgrounds[index].x, backgrounds[index].x
					+ backgrounds[index].image.getWidth(null)))
			{
				graphics.drawImage(backgrounds[index].image,
						backgrounds[index].x, backgrounds[index].y, this);
			}
		}

		// Draw buildings
		for (int index = 0; index < buildings.length; index++)
		{
			if (buildings[index].exists
					&& onScreen(buildings[index].x, buildings[index].x
							+ (buildings[index].image).getWidth(null)))
			{
				graphics.drawImage(buildings[index].image, buildings[index].x,
						buildings[index].y, this);
				if (buildings[index].isFull)
				{
					graphics.setColor(new Color(0, 102, 0));
					graphics.setFont(arial20);
					currentFontMetrics = graphics.getFontMetrics(arial20);
					int width = currentFontMetrics
							.stringWidth("Click to collect");

					graphics.drawString("Click to collect", buildings[index].x
							+ buildings[index].width / 2 - width / 2,
							buildings[index].y - 50);
				}
			}
		}

		// Draw ghost image of building
		if (canPlace)
		{

			// Draw normal ghost image
			if (!checkObstaclesBelow())
			{
				graphics.drawImage(buildingGhost, mouseX - selectedWidth / 2,
						groundHeight - selectedHeight, this);
			}

			// Draw error ghost image
			else
			{
				graphics.drawImage(buildingGhostError, mouseX - selectedWidth
						/ 2, groundHeight - selectedHeight, this);
			}
		}

		// Draw certain things only once the town hall has been placed down
		if (homeSet)
		{

			// Draw the fading at the end of each side of the view limit that
			// the player has
			if (viewLimitx1 + fadeLeft.getWidth(null) >= 0)
			{
				graphics.drawImage(fadeLeft, viewLimitx1 - 1200, 0, this);
			}

			if (SCREEN_WIDTH >= viewLimitx2 - 300)
			{
				graphics.drawImage(fadeRight, viewLimitx2 - 300, 0, this);
			}

			// Show information to the player, such as how to scroll and the
			// distance from the town hall
			graphics.setFont(arial20);
			graphics.setColor(Color.darkGray);
			graphics.drawString("Scroll with the arrow keys or WASD",
					470, 215);

			if ((homeX - worldX) / 15 > 0)
			{
				graphics.drawString("<< " + (homeX - worldX) / 15 + " Home",
						700, 240);
			}
			else if ((homeX - worldX) / 15 < 0)
			{
				graphics.drawString("Home " + -(homeX - worldX) / 15 + " >>",
						700, 240);
			}
			else
			{
				graphics.drawString("   At Home", 700, 240);
			}
		}
		else
		{

			// If the town hall has not been set, tell the player to set it down
			graphics.setColor(Color.darkGray);
			graphics.setFont(arial24Bold);
			graphics.drawString(
					"Build the Town Hall to create your town anywhere you like",
					20, 220);
			graphics.setFont(arial20);
			graphics.drawString(
					"Use the arrow keys or edges of the screen to navigate the world",
					20, 245);
		}

		// Display warning message and happiness indicator if there is no food
		if (isStarving)
		{
			graphics.setColor(Color.red);
			graphics.setFont(arial24);
			graphics.drawString(
					"You have run out of food! Harvest more wheat before", 200,
					350);
			graphics.drawString(
					"your town's happiness runs out and the people rebel!",
					200, 375);

			graphics.setColor(Color.black);
			graphics.fillRect(200, 400, 580, 20);
			graphics.setColor(new Color(0, 102, 0));
			graphics.fillRect(200, 400, (int) (1.0 * currentHappiness
					/ totalHappiness * 580 + 0.5), 20);
			graphics.setColor(Color.black);
			graphics.drawRect(200, 400, 580, 20);
		}

		// Display all the messages
		// Display the town information variables
		graphics.setFont(arial24Bold);
		graphics.setColor(Color.YELLOW);
		graphics.drawString("Gold: " + currentGold, 830, 40);
		graphics.setColor(new Color(139, 69, 19));
		graphics.drawString("Lumber: " + currentLumber, 830, 70);
		graphics.setColor(Color.DARK_GRAY);
		graphics.drawString("Stone: " + currentStone, 830, 100);
		graphics.setColor(Color.ORANGE);
		graphics.drawString("Food: " + currentFood, 830, 130);
		graphics.setColor(Color.BLACK);
		graphics.drawString("Population: " + currentPopulation, 830, 160);
		graphics.setColor(new Color(0, 102, 0));
		graphics.drawString("Happiness: "
				+ (int) (1.0 * currentHappiness / totalHappiness * 100 + 0.5)
				+ "%", 830, 190);
		graphics.setColor(Color.RED);

		if (gameMode == 1)
			graphics.drawString("Day " + currentDay + " /15 (" + currentHours
					+ ")", 830, 220);
		else if (gameMode == 0)
		{
			graphics.drawString(
					"Day " + currentDay + " (" + currentHours + ")", 830, 220);
		}

		graphics.setFont(arial24);

		// Display the static messages (messages with variables)
		for (int index = 0; index < texts.length; index++)
		{
			if (texts[index].getWidth)
			{
				currentFontMetrics = graphics.getFontMetrics(texts[index].font);
				int width = currentFontMetrics.stringWidth(texts[index].string);
				texts[index].width = width;
				texts[index].align();
				texts[index].getWidth = false;
			}
			if (texts[index].exists && texts[index].isOnScreen())
			{
				graphics.setColor(texts[index].color);
				graphics.setFont(texts[index].font);
				graphics.drawString(texts[index].string, texts[index].x,
						texts[index].y);
			}
		}

		// NEW CODE
		// Display Menu Base
		graphics.drawImage(menuBase, 10, 10, this);
		graphics.drawImage(menuSlideBase, 75, 18, this);

		// Draw the information displayed when the mouse hovers over a
		// building
		for (int index = 0; index < buildings.length; index++)
		{
			if (pointIsTouching(mouseX, mouseY, buildings[index].x,
					buildings[index].x + buildings[index].width,
					buildings[index].y + buildings[index].height,
					buildings[index].y)
					&& buildings[index].exists)
			{
				graphics.setFont(arial24Bold);
				graphics.setColor(Color.black);
				graphics.drawString(buildings[index].name, 20, 220);

				graphics.setFont(arial20);
				graphics.setColor(Color.darkGray);

				int nextLine = 25;

				if (buildings[index].noOfResidents >= 0)
				{
					graphics.drawString("Residents: "
							+ buildings[index].noOfResidents + " / "
							+ buildings[index].maxNoOfResidents, 20,
							220 + nextLine);
					nextLine += 25;
					graphics.drawString(
							"Income: "
									+ (int) (buildings[index].noOfResidents
											* incomePerCitizen
											* (1.0 * currentHappiness / totalHappiness) + 0.5)
									+ " Gold per day", 20, 220 + nextLine);
					nextLine += 25;

				}
			}
		}

		// Draw the information displayed when the mouse hovers over buttons
		if (homeSet)
		{
			for (int index = 0; index < buttons.length; index++)
			{
				if (buttons[index].exists)
				{
					if (pointIsTouching(mouseX, mouseY, buttons[index].x,
							buttons[index].x + buttons[index].width,
							buttons[index].y + buttons[index].height,
							buttons[index].y)
							&& buttons[index].exists)
					{
						if (index == 0)
						{
							graphics.setFont(arial20Italic);
							graphics.setColor(Color.darkGray);
							graphics.drawString(
									"Sends you back to the centre of your town",
									20, 220);
						}
						else if (index == 1)
						{
							graphics.setFont(arial20Italic);
							graphics.setColor(Color.darkGray);
							graphics.drawString("Activates demolish mode", 20,
									220);
						}
						else if (index == 28)
						{
							graphics.setFont(arial20Italic);
							graphics.setColor(Color.darkGray);
							graphics.drawString(
									"Pays for and collects all lumber at once",
									20, 220);
						}
						else if (index == 29)
						{
							graphics.setFont(arial20Italic);
							graphics.setColor(Color.darkGray);
							graphics.drawString(
									"Pays for and collects all stone at once",
									20, 220);
						}
						else if (index == 30)
						{
							graphics.setFont(arial20Italic);
							graphics.setColor(Color.darkGray);
							graphics.drawString(
									"Pays for and harvests all wheat at once",
									20, 220);
						}
						else if (index == 36)
						{
							graphics.setFont(arial20Italic);
							graphics.setColor(Color.darkGray);
							graphics.drawString(
									"Pauses the game",
									20, 220);
							buttons[index].pressed();
						}
						else if (index >= 4 && index < 30)
						{

							String tileName = "";
							String tileDescription = "";
							String tileCost = "";

							if (index == 4)
							{
								tileName = "Dirt House";
								tileDescription = "Provides housing for citizens";
								tileCost = "Cost: 75 Gold";
							}
							else if (index == 5)
							{
								tileName = "Wooden House";
								tileDescription = "Provides more housing for citizens";
								tileCost = "Cost: 100 Gold and 25 Lumber";
							}
							else if (index == 6)
							{
								tileName = "Watch Tower";
								tileDescription = "Provides increased vision of the world within a certain radius";
								tileCost = "Cost: 150 Gold, 50 Lumber, and 200 Stone";
							}
							else if (index == 7)
							{
								tileName = "Wheat Field";
								tileDescription = "Produces food for citizens' consumption (Harvest every three days)";
								tileCost = "Cost: 50 Gold and 10 Lumber";
							}
							else if (index == 8)
							{
								tileName = "Lumber Mill";
								tileDescription = "Produces lumber for construction";
								tileCost = "Cost: 250 Gold and 50 Lumber";
							}
							else if (index == 9)
							{
								tileName = "Stone Mine";
								tileDescription = "Produces stone for construction";
								tileCost = "Cost: 250 Gold and 50 Lumber";
							}
							else if (index == 10)
							{
								tileName = "Tavern";
								tileDescription = "Increases happiness within the town";
								tileCost = "Cost: 150 Gold and 50 Lumber";
							}

							graphics.setFont(arial24Bold);
							graphics.setColor(new Color(0, 51, 25));

							graphics.drawString(tileName, 20, 220);

							graphics.setFont(arial24);

							graphics.drawString(tileCost, 20, 275);

							graphics.setFont(arial20Italic);
							graphics.setColor(Color.darkGray);

							graphics.drawString(tileDescription, 20, 245);

						}
					}
				}

			}

		}

		// Highlight the pause button
		if (!(pointIsTouching(mouseX, mouseY, buttons[36].x, buttons[36].x
				+ buttons[36].width, buttons[36].y + buttons[36].height,
				buttons[36].y) && buttons[36].exists))
		{
			buttons[36].released();
		}

		// Draw building tiles
		for (int index = 4; index < 30; index++)
		{
			// Draw whole tile if tile is within scroll area
			if (buttons[index].x > scrollAreax1
					&& buttons[index].x + buttons[index].width < scrollAreax2)
			{
				graphics.drawImage(buttons[index].image, buttons[index].x,
						buttons[index].y, this);
			}

			// part of tile if tile is partially within scroll area
			else if (buttons[index].x < scrollAreax1
					&& buttons[index].x > scrollAreax1 - buttons[index].width)
			{

				graphics.drawImage(buttons[index].image, scrollAreax1,
						scrollAreay1, buttons[index].x + buttons[index].width,
						scrollAreay2, scrollAreax1 - buttons[index].x, 0,
						buttons[index].width, buttons[index].height, this);
			}

			else if (buttons[index].x < scrollAreax2
					&& buttons[index].x + buttons[index].width > scrollAreax2)
			{

				graphics.drawImage(buttons[index].image, buttons[index].x,
						scrollAreay1, scrollAreax2, scrollAreay2, 0, 0,
						scrollAreax2 - buttons[index].x, buttons[index].height,
						this);
			}
		}

		// Draw other buttons
		for (int index = 0; index < 4; index++)
		{
			if (buttons[index].exists)
			{
				graphics.drawImage(buttons[index].image, buttons[index].x,
						buttons[index].y, this);
			}
		}

		for (int index = 20; index <= 30; index++)
		{
			if (buttons[index].exists)
			{
				graphics.drawImage(buttons[index].image, buttons[index].x,
						buttons[index].y, this);
			}
		}

		for (int index = 36; index <= 36; index++)
		{
			if (buttons[index].exists)
			{
				graphics.drawImage(buttons[index].image, buttons[index].x,
						buttons[index].y, this);
			}
		}

		// Draw button shadow
		graphics.drawImage(fade, 80, 23, 100, 174, 50, 0, fade.getWidth(null),
				fade.getHeight(null), this);
		graphics.drawImage(fade, 725, 23, 745, 174, fade.getWidth(null), 0, 50,
				fade.getHeight(null), this);

		// Draw Custom Mouse Cursor
		if (canDemolish)
			graphics.drawImage(demolishCursor, mouseX - 16, mouseY - 16, this);
		else if (!mousePressed)
			graphics.drawImage(customCursor, mouseX, mouseY, this);
		else
			graphics.drawImage(customCursorPressed, mouseX, mouseY, this);

		// Draw pause menu
		if (isPaused)
		{
			graphics.drawImage(black, 0, 0, this);
			graphics.setFont(arial48Bold);
			graphics.setColor(Color.white);
			graphics.drawString("PAUSED", 420, 400);
			graphics.setFont(arial24);
			graphics.drawString("Press P to unpause", 420, 450);
			graphics.drawString("Press ESC to go back to the main menu", 300,
					480);
			graphics.drawString("Progress will be lost!", 410, 510);
		}

		// Draw game over screen from lime limit
		if (gameMode == 1 && currentDay >= 15)
		{
			graphics.drawImage(black, 0, 0, this);
			graphics.setFont(arial48Bold);
			graphics.setColor(Color.white);
			graphics.drawString("TIME'S UP", 370, 400);
			graphics.setFont(arial24);
			graphics.drawString("You've reached the day limit of 15!", 300, 450);
			graphics.drawString(
					"Your score has been recorded on the leaderboard", 230, 480);
			graphics.drawString("Press ESC to go back to the main menu", 280,
					510);
		}

		// Draw game over screen from rebellion
		if (currentHappiness <= 0)
		{
			graphics.drawImage(black, 0, 0, this);
			graphics.setFont(arial48Bold);
			graphics.setColor(Color.white);
			graphics.drawString("GAME OVER", 370, 400);
			graphics.setFont(arial24);
			graphics.drawString(
					"Your town's happiness dropped to 0 and your citizens rebelled!",
					195, 450);

			if (gameMode == 1)
			{
				graphics.drawString(
						"You have been ranked based on your days survived and population",
						180, 480);
			}
			else
			{
				graphics.drawString(
						"You were playing endless mode and therefore were not ranked",
						225, 480);
			}
			graphics.drawString("Press ESC to go back to the main menu", 280,
					510);
		}
	}

	/**
	 * Method that runs each method and action that belongs within the set
	 * framerate (default is roughly 30 frames per second)
	 */
	public void run()
	{

		long beforeTime, timeDiff, sleep;

		beforeTime = System.currentTimeMillis();

		while (true)
		{

			// Run game elements
			if (currentScreen == 1)
			{
				if (!isPaused && (currentDay < 15 || gameMode == 0)
						&& currentHappiness > 0)
				{
					passTime();
					scrollScreen();
					moveObjects();
				}
			}

			// Refresh the graphics every frame
			repaint();

			// Maintain frame rate even if the hardware is unsatisfactory
			timeDiff = System.currentTimeMillis() - beforeTime;
			sleep = DELAY - timeDiff;
			if (sleep < 0)
				sleep = 2;

			try
			{
				Thread.sleep(sleep);
			}
			catch (InterruptedException e)
			{
				System.out.println("Interrupted: " + e.getMessage());
			}

			beforeTime = System.currentTimeMillis();
		}
	}

	// Method that isn't used but Java demands to be included
	public void mouseClicked(MouseEvent event)
	{
	}

	// Method that isn't used but Java demands to be included
	public void mouseEntered(MouseEvent event)
	{
	}

	// Method that isn't used but Java demands to be included
	public void mouseExited(MouseEvent event)
	{
	}

	/**
	 * Handle clicking buttons to change their graphic
	 * 
	 * @param event the mouse details
	 */
	public void mousePressed(MouseEvent event)
	{

		// NEW CODE
		mousePressed = true;

		// All of this below must only happen during the actual game
		if (currentScreen == 1)
		{

			// Stop building tiles from clicking if town hall is not built
			if (homeSet)
			{
				for (int button = 0; button < buttons.length; button++)
				{
					if (pointIsTouching(mouseX, mouseY, buttons[button].x1,
							buttons[button].x2, buttons[button].y1,
							buttons[button].y2)
							&& event.getButton() == MouseEvent.BUTTON1)
					{
						if (!canDemolish)
						{

							// Building tiles
							if (!(button >= 4 && (button < 28 || button >= 36) && (mouseX < scrollAreax1 || mouseX > scrollAreax2)))
							{
								buttons[button].pressed();

								// SELECT BUILDING TILES
								for (int tile = 4; tile < 30; tile++)
								{
									if (pointIsTouching(mouseX, mouseY,
											buttons[tile].x1, buttons[tile].x2,
											buttons[tile].y1, buttons[tile].y2)
											&& !canDemolish
											&& !(mouseX < scrollAreax1 || mouseX > scrollAreax2))
									{

										// Press or release the building tiles
										// in the scroll menu depending on
										// whether or not it is already pressed,
										// or another tile is pressed
										buttonPressedThisTurn = true;

										boolean releaseButtons = false;
										if (buildingSelected.equals("townHall")
												&& canPlace)
										{
											displayMessage("Your first building must be the Town Hall");
										}

										else if (tile == 4)
										{
											if (buildingSelected
													.equals("dirtHouse")
													&& canPlace == true)
											{
												buttons[tile].released();
												canPlace = false;
											}
											else
											{
												canPlace = true;
												changeCurrentBuildingTo("dirtHouse");
												releaseButtons = true;
											}

										}
										else if (tile == 5)
										{
											if (buildingSelected
													.equals("woodenHouse")
													&& canPlace == true)
											{
												buttons[tile].released();
												canPlace = false;
											}
											else
											{
												canPlace = true;
												changeCurrentBuildingTo("woodenHouse");
												releaseButtons = true;
											}
										}

										else if (tile == 6)
										{
											if (buildingSelected
													.equals("watchTower")
													&& canPlace == true)
											{
												buttons[tile].released();
												canPlace = false;
											}
											else
											{
												canPlace = true;
												changeCurrentBuildingTo("watchTower");
												releaseButtons = true;
											}
										}

										else if (tile == 7)
										{
											if (buildingSelected
													.equals("wheatField")
													&& canPlace == true)
											{
												buttons[tile].released();
												canPlace = false;
											}
											else
											{
												canPlace = true;
												changeCurrentBuildingTo("wheatField");
												releaseButtons = true;
											}
										}
										else if (tile == 8)
										{
											if (buildingSelected
													.equals("lumberMill")
													&& canPlace == true)
											{
												buttons[tile].released();
												canPlace = false;
											}
											else
											{
												canPlace = true;
												changeCurrentBuildingTo("lumberMill");
												releaseButtons = true;
											}
										}

										else if (tile == 9)
										{
											if (buildingSelected
													.equals("stoneMine")
													&& canPlace == true)
											{
												buttons[tile].released();
												canPlace = false;
											}
											else
											{
												canPlace = true;
												changeCurrentBuildingTo("stoneMine");
												releaseButtons = true;
											}
										}

										else if (tile == 10)
										{
											if (buildingSelected
													.equals("tavern")
													&& canPlace == true)
											{
												buttons[tile].released();
												canPlace = false;
											}
											else
											{
												canPlace = true;
												changeCurrentBuildingTo("tavern");
												releaseButtons = true;
											}
										}

										if (releaseButtons)
										{
											for (int checkTile = 4; checkTile < 30; checkTile++)
											{
												if (checkTile != tile)
													buttons[checkTile]
															.released();
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// Main Menu
		else if (currentScreen == 0)
		{

			// Toggle the mute button
			if (pointIsTouching(mouseX, mouseY, buttons[32].x1, buttons[32].x2,
					buttons[32].y1, buttons[32].y2))
			{
				if (buttons[32].isPressed)
				{
					isMute = false;
					toggleMusic(1);
					buttons[32].released();
				}
				else
				{
					toggleMusic(0);
					isMute = true;
					buttons[32].pressed();
				}
			}

			// Start a new game for the player
			else if (pointIsTouching(mouseX, mouseY, buttons[31].x1,
					buttons[31].x2, buttons[31].y1, buttons[31].y2))
			{
				currentScreen = 3;
				name = "";
			}

			// Send the player to the leaderboard screen when the leaderboard
			// button is pressed
			else if (pointIsTouching(mouseX, mouseY, buttons[33].x1,
					buttons[33].x2, buttons[33].y1, buttons[33].y2))
			{

				currentScreen = 2;
				nextLeaderBoardLine = 0;
				buttons[34].create(800, 630, backButton, backButtonPressed);
			}
		}

		// Send the player back to the main menu when the back button is pressed
		else if (currentScreen == 2)
		{
			if (pointIsTouching(mouseX, mouseY, buttons[34].x1, buttons[34].x2,
					buttons[34].y1, buttons[34].y2))
			{

				currentScreen = 0;
				buttons[34].remove();
			}
		}

		// Allow the player to select a game mode
		else if (currentScreen == 4)
		{
			if (pointIsTouching(mouseX, mouseY, buttons[37].x1, buttons[37].x2,
					buttons[37].y1, buttons[37].y2))
			{
				gameMode = 0;
				toggleMusic(2);
				newGame();
				currentScreen = 1;
				firstClick = true;
			}

			if (pointIsTouching(mouseX, mouseY, buttons[38].x1, buttons[38].x2,
					buttons[38].y1, buttons[38].y2))
			{
				gameMode = 1;
				toggleMusic(2);
				newGame();
				currentScreen = 1;
				firstClick = true;
			}
		}

	}

	/**
	 * Handle clicking buttons and clicking to place/remove buildings
	 * 
	 * @param event the mouse details
	 */
	public void mouseReleased(MouseEvent event)
	{
		mousePressed = false;

		// Respond to mouse when not paused and not the first click of the game
		if (!isPaused && !firstClick)
		{
			// Stop scrolling the menu
			menuScroll = 0;

			// Only run these if the player is in-game
			if (currentScreen == 1)
			{
				if (mouseMoved)
				{

					if (event.getButton() == MouseEvent.BUTTON1)
					{
						buttonPressedThisTurn = false;

						// Release all buttons that are not tiles
						for (int button = 0; button < 4; button++)
						{
							if (button != 1)
							{
								buttons[button].released();
							}
						}

						for (int button = 28; button < buttons.length; button++)
						{
							buttons[button].released();
						}

						// Only run these when the player has already placed
						// down a town hall
						if (homeSet)
						{

							// Execute operations when a player clicks on an
							// existing building
							for (int index = 0; index < buildings.length; index++)
							{
								if (pointIsTouching(mouseX, mouseY,
										buildings[index].x, buildings[index].x
												+ buildings[index].width,
										buildings[index].y
												+ buildings[index].height,
										buildings[index].y)
										&& buildings[index].exists)
								{

									// If the player is in demolish mode, allow
									// the player to demolish a building that is
									// not a town hall and not a watch tower in
									// the middle of the town
									if (canDemolish)
									{
										if (buildings[index].type
												.equals("townHall"))
										{
											displayMessage("You cannot demolish the Town Hall!");
										}
										else if (buildings[index].type
												.equals("watchTower")
												&& buildings[index].x
														+ buildings[index].width
														/ 2
														+ buildings[index].towerVision != viewLimitx2
												&& buildings[index].x
														+ buildings[index].width
														/ 2
														- buildings[index].towerVision != viewLimitx1)
										{
											displayMessage("You cannot demolish a Watch Tower in the middle of your town!");
										}
										else
										{
											buildings[index].demolish();
										}
									}
									else
									{

										// If the player is not in demolish
										// mode, then they can collect resources
										// from buildings by clicking on them
										buildings[index].collect();

										if (buildings[index].type
												.equals("wheatField"))
										{
											currentFood += harvestAmount;
										}
										else if (buildings[index].type
												.equals("lumberMill"))
										{
											currentLumber += harvestAmount;
										}
										else if (buildings[index].type
												.equals("stoneMine"))
										{
											currentStone += harvestAmount;
										}
										harvestAmount = 0;

									}
								}
							}

							// Home button to centre the town hall in the middle
							// of the screen
							if (pointIsTouching(mouseX, mouseY, buttons[0].x1,
									buttons[0].x2, buttons[0].y1, buttons[0].y2))
							{
								goHome = true;
								buttonPressedThisTurn = true;
							}

							// Demolish Button to activate demolish mode and
							// allow the player to demolish and salvage
							// buildings
							else if (pointIsTouching(mouseX, mouseY,
									buttons[1].x1, buttons[1].x2,
									buttons[1].y1, buttons[1].y2))
							{
								buttonPressedThisTurn = true;
								if (!canDemolish)
								{
									canDemolish = true;
									canPlace = false;
									changeCurrentBuildingTo("");
									for (int tile = 4; tile < 30; tile++)
									{
										buttons[tile].released();

									}
								}
								else
								{
									buttons[1].released();
									canDemolish = false;
								}
							}

							// Collect all the available lumber when the player
							// clicks on the collect lumber button
							else if (pointIsTouching(mouseX, mouseY,
									buttons[28].x1, buttons[28].x2,
									buttons[28].y1, buttons[28].y2))
							{

								buttonPressedThisTurn = true;

								for (int index = 0; index < buildings.length; index++)
								{
									if (buildings[index].exists
											&& buildings[index].type
													.equals("lumberMill"))
									{
										buildings[index].collect();
									}
								}

								if (harvestAmount > 0)
								{
									displayMessage(harvestAmount
											+ " lumber harvested", new Color(
											139, 69, 19));
									currentLumber += harvestAmount;
									harvestAmount = 0;
								}
								else
								{
									displayMessage("No lumber harvested",
											new Color(139, 69, 19));
								}
							}

							// Collect all the available stone when the player
							// clicks on the collect stone button
							else if (pointIsTouching(mouseX, mouseY,
									buttons[29].x1, buttons[29].x2,
									buttons[29].y1, buttons[29].y2))
							{

								buttonPressedThisTurn = true;

								for (int index = 0; index < buildings.length; index++)
								{
									if (buildings[index].exists
											&& buildings[index].type
													.equals("stoneMine"))
									{
										buildings[index].collect();
									}
								}

								if (harvestAmount > 0)
								{
									displayMessage(harvestAmount
											+ " stone harvested",
											Color.darkGray);
									currentStone += harvestAmount;
									harvestAmount = 0;
								}
								else
								{
									displayMessage("No stone harvested",
											Color.darkGray);
								}
							}

							// Harvest Button to harvest all available crops
							else if (pointIsTouching(mouseX, mouseY,
									buttons[30].x1, buttons[30].x2,
									buttons[30].y1, buttons[30].y2))
							{

								buttonPressedThisTurn = true;

								for (int index = 0; index < buildings.length; index++)
								{
									if (buildings[index].exists
											&& buildings[index].type
													.equals("wheatField"))
									{
										buildings[index].collect();
									}
								}

								if (harvestAmount > 0)
								{
									displayMessage(harvestAmount
											+ " food harvested", Color.ORANGE);
									currentFood += harvestAmount;
									harvestAmount = 0;
								}
								else
								{
									displayMessage("No food harvested",
											Color.ORANGE);
								}
							}

							// Pause button to allow the player to pause the
							// game
							else if (pointIsTouching(mouseX, mouseY,
									buttons[36].x1, buttons[36].x2,
									buttons[36].y1, buttons[36].y2))
							{
								if (!isPaused)
								{
									isPaused = true;
								}
								buttonPressedThisTurn = true;

							}
						}

						// Don't allow the player to place buildings beyond the
						// view limit
						if (canPlace && !buttonPressedThisTurn
								&& mouseY > scrollAreay2)
						{

							// Don't allow placement of buildings on top of
							// other buildings
							if (checkObstaclesBelow())
							{
								displayMessage("You cannot place that there!");
							}

							else
							{

								// Create a new building when the player wants
								// to and can afford it
								if (canAfford(buildingSelected))
								{

									int freeBuilding = -1;
									for (int index = 0; index < buildings.length
											&& freeBuilding < 0; index++)
									{
										if (!buildings[index].exists)
										{
											freeBuilding = index;
										}
									}

									if (freeBuilding < 0)
									{
										freeBuilding++;
									}

									buildings[freeBuilding].create(
											buildingSelected, mouseX,
											groundHeight, building);

								}
								else
								{
									// Tell the player that the building cannot
									// be afforded (If only we had time to
									// specify which resources specifically)
									displayMessage("You cannot afford that!");
								}

								// Set the home area once the town hall is
								// placed so
								// that
								// the
								// home button will return you to this area
								if (canPlace && !homeSet)
								{
									homeX = worldX;
									if (mouseX > SCREEN_WIDTH / 2)
									{
										homeX -= mouseX - SCREEN_WIDTH / 2;
									}
									else if (mouseX < SCREEN_WIDTH / 2)
									{
										homeX += SCREEN_WIDTH / 2 - mouseX;

									}

									canPlace = false;
									homeSet = true;
									doesLimitView = true;

								}

							}
						}
					}
				}
			}

		}

		firstClick = false;

	}

	/**
	 * Keep track of the location of the mouse when it is dragged with a button
	 * pressed down
	 * 
	 * @param event the mouse details
	 */
	public void mouseDragged(MouseEvent event)
	{
		if (!isPaused)
		{
			if (!mouseMoved)
			{
				mouseMoved = true;
			}

			mouseX = event.getX();
			mouseY = event.getY();
		}

	}

	/**
	 * Keep track of the location of the mouse when it moves without pressing it
	 * down
	 * 
	 * @param event the mouse details
	 */
	public void mouseMoved(MouseEvent event)
	{
		if (!isPaused)
		{
			if (!mouseMoved)
			{
				mouseMoved = true;
			}

			mouseX = event.getX();
			mouseY = event.getY();
		}

	}

	/**
	 * Executes actions performed when a key on the keyboard is pressed down
	 * 
	 * @param event the details of the key that was pressed
	 */
	public void keyPressed(KeyEvent event)
	{
		if (!isPaused)
		{
			// Allow the player to scroll left and right along the screen with
			// the arrow keys or WASD
			if (!keyboardIsScrolling)
			{
				if ((event.getKeyCode() == KeyEvent.VK_LEFT || event
						.getKeyCode() == KeyEvent.VK_A))
				{

					worldSpeed = 50;
					keyboardIsScrolling = true;

				}

				else if ((event.getKeyCode() == KeyEvent.VK_RIGHT || event
						.getKeyCode() == KeyEvent.VK_D))
				{

					worldSpeed = -50;
					keyboardIsScrolling = true;

				}
			}
		}

		// Allow the player to pause and unpause with the 'p' key
		if (event.getKeyCode() == KeyEvent.VK_P && currentScreen == 1)
		{
			if (!isPaused)
			{
				isPaused = true;
			}
			else
			{
				isPaused = false;
			}
		}

		// Allow the player to exit a certain screen by pressing escape
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			nextLeaderBoardLine = 0;
			if (isPaused)
			{
				currentScreen = 0;
				isPaused = false;
				toggleMusic(1);
			}
			else if (currentScreen == 2)
			{
				currentScreen = 0;
			}

			else if (currentHappiness <= 0 || currentDay >= 15)
			{
				roundEnd();
				try
				{
					saveLeaderBoard();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				currentScreen = 2;
				buttons[34].create(800, 630, backButton, backButtonPressed);
				toggleMusic(1);
			}
		}

		// Allow the player to scroll the leaderboard with the arrow keys
		if (currentScreen == 2)
		{
			if (event.getKeyCode() == KeyEvent.VK_UP)
			{
				leaderBoardScroll = 20;
			}
			else if (event.getKeyCode() == KeyEvent.VK_DOWN)
			{
				leaderBoardScroll = -20;
			}
		}

	}

	/**
	 * Executes actions performed when a key on the keyboard is released
	 * 
	 * @param event the details of the key that was released
	 */
	public void keyReleased(KeyEvent event)
	{
		// Stop scrolling the leaderboard
		leaderBoardScroll = 0;

		// Stop scrolling the screen in the world
		if (!isPaused)
		{
			if (keyboardIsScrolling)
			{
				if (event.getKeyCode() == KeyEvent.VK_LEFT
						|| event.getKeyCode() == KeyEvent.VK_A
						|| event.getKeyCode() == KeyEvent.VK_D
						|| event.getKeyCode() == KeyEvent.VK_RIGHT)
				{
					worldSpeed = 0;
					keyboardIsScrolling = false;
				}
			}
		}

		// Allow the player to move on from the name entering screen
		if (currentScreen == 3 && event.getKeyCode() == KeyEvent.VK_ENTER)
		{
			if (name.equals(""))
			{
				name = "Unnamed Player";
			}
			currentScreen = 4;
		}

	}

	/**
	 * This method is only used for entering a player's name into the program
	 * for the purposes of updating the leaderboard
	 * 
	 * @param event the details of the key that was typed
	 */
	public void keyTyped(KeyEvent event)
	{
		if (currentScreen == 3)
		{
			if ((Character.isLetter(event.getKeyChar())
					|| Character.isDigit(event.getKeyChar()) || event
					.getKeyChar() == KeyEvent.VK_SPACE) && name.length() < 20)
			{
				name += event.getKeyChar();
			}
			else if (event.getKeyChar() == KeyEvent.VK_BACK_SPACE
					&& name.length() > 0)
			{
				name = name.substring(0, name.length() - 1);
			}
		}

	}

	/**
	 * Checks and returns the result of whether or not the mouse is on the same
	 * x location as another building and whether a building can be placed
	 * 
	 * @return whether a building can be placed
	 */
	private boolean checkObstaclesBelow()
	{

		for (int index = 0; index < buildings.length; index++)
		{
			if (buildings[index].exists
					&& mouseX > buildings[index].x - selectedWidth / 2
					&& mouseX < buildings[index].x + buildings[index].width
							+ selectedWidth / 2)
			{
				return true;
			}
		}

		if (doesLimitView
				&& (mouseX + building.getWidth(null) / 2 >= viewLimitx2 || mouseX
						- building.getWidth(null) / 2 <= viewLimitx1))
		{
			return true;
		}

		return false;
	}

	/**
	 * Checks and returns the result of whether or not a certain point is within
	 * a rectangular area
	 * 
	 * @param x the x coordinate of the given point
	 * @param y the y coordinate of the given point
	 * @param x1 the left x coordinate of the given area
	 * @param x2 the right x coordinate of the given area
	 * @param y1 the bottom y coordinate of the given area
	 * @param y2 the top y coordinate of the given area
	 * @return whether the point and area touches
	 */
	private boolean pointIsTouching(int x, int y, int x1, int x2, int y1, int y2)
	{
		if (x >= x1 && x <= x2 && y <= y1 && y >= y2)
		{
			return true;
		}
		return false;
	}

	/**
	 * Checks and returns the result of whether or not a given line is shown on
	 * the screen
	 * 
	 * @param x1 the left x coordinate of the line
	 * @param x2 the right x coordinate of the line
	 * @return
	 */
	private boolean onScreen(int x1, int x2)
	{
		if (x1 <= SCREEN_WIDTH && x2 >= 0)
		{
			return true;
		}

		return false;
	}

	/**
	 * Changes the building type that the user is currently placing
	 * 
	 * @param name the type of building
	 * @param normal the image of the building when it's normal
	 * @param ghost the image of the ghost before placement
	 * @param ghostError the image of the ghost before placement when it cannot
	 *            be placed
	 */
	public void changeCurrentBuildingTo(String name)
	{
		buildingSelected = name;

		if (name.equals("dirtHouse"))
		{
			building = images[0];
			buildingGhost = images[1];
			buildingGhostError = images[2];
		}
		else if (name.equals("woodenHouse"))
		{
			building = images[3];
			buildingGhost = images[4];
			buildingGhostError = images[5];
		}
		else if (name.equals("townHall"))
		{
			building = images[6];
			buildingGhost = images[7];
			buildingGhostError = images[8];
		}
		else if (name.equals("watchTower"))
		{
			building = images[9];
			buildingGhost = images[10];
			buildingGhostError = images[11];
		}
		else if (name.equals("wheatField"))
		{
			building = images[12];
			buildingGhost = images[13];
			buildingGhostError = images[14];
		}
		else if (name.equals("lumberMill"))
		{
			building = images[21];
			buildingGhost = images[22];
			buildingGhostError = images[23];
		}
		else if (name.equals("stoneMine"))
		{
			building = images[18];
			buildingGhost = images[19];
			buildingGhostError = images[20];
		}
		else if (name.equals("tavern"))
		{
			building = images[24];
			buildingGhost = images[25];
			buildingGhostError = images[26];
		}

		selectedWidth = building.getWidth(null);

		if (name.equals("wheatField"))
		{
			selectedHeight = building.getHeight(null) - 22;
		}
		else
		{
			selectedHeight = building.getHeight(null);
		}
	}

	/**
	 * Checks if the user has enough resources on hand to build a certain
	 * building
	 * 
	 * @param type the type of building to check
	 * @return whether or not the user can afford it
	 */
	private boolean canAfford(String type)
	{
		if (type.equals("dirtHouse"))
		{
			if (currentGold >= 75)
			{
				return true;
			}
		}
		else if (type.equals("woodenHouse"))
		{
			if (currentGold >= 100 & currentLumber >= 25)
			{
				return true;
			}
		}
		else if (type.equals("townHall"))
		{
			if (currentGold >= 500 & currentLumber >= 250 & currentStone >= 250)
			{
				return true;
			}
		}
		else if (type.equals("watchTower"))
		{
			if (currentGold >= 150 & currentLumber >= 50 & currentStone >= 200)
			{
				return true;
			}
		}
		else if (type.equals("wheatField"))
		{
			if (currentGold >= 50 & currentLumber >= 10)
			{
				return true;
			}
		}
		else if (type.equals("lumberMill"))
		{
			if (currentGold >= 250 & currentLumber >= 50)
			{
				return true;
			}
		}
		else if (type.equals("stoneMine"))
		{
			if (currentGold >= 250 & currentLumber >= 50)
			{
				return true;
			}
		}
		else if (type.equals("tavern"))
		{
			if (currentGold >= 150 & currentLumber >= 50)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Write a message that can rise if needed to (Static, non-changing messages
	 * only!)
	 * 
	 * @param message the text of the given message
	 * @param x the x coordinate of the message
	 * @param y the y coordinate of the message
	 * @param font the font of the given message
	 * @param color the color of the given message
	 * @param doesRise whether the message will rise a little and then disappear
	 * @param time the time (in framerate time) that the message remains, put 0
	 *            or lower if permanent
	 * @param alignment which alignment will the text be aligned to: -1 is left,
	 *            0 is centre, 1 is right
	 */
	public void createText(String text, int x, int y, Font font, Color color,
			boolean doesRise, int time, int alignment, boolean doesScroll)
	{
		int freeIndex = 0;
		for (int index = 0; index < texts.length; index++)
		{
			if (!texts[index].exists)
			{
				freeIndex = index;
			}
		}

		texts[freeIndex].create(text, x, y, font, color, doesRise, time,
				alignment, doesScroll);
	}

	/**
	 * Uses the custom createText method to display a message in the centre on
	 * the screen
	 * 
	 * @param message the message that is to be displayed
	 */
	public void displayMessage(String message)
	{
		createText(message, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, arial24,
				Color.RED, true, 90, 0, false);
	}

	/**
	 * Uses the custom createText method to display a message in the centre on
	 * the screen
	 * 
	 * @param message the message that is to be displayed
	 */
	public void displayMessage(String message, Color colour)
	{
		createText(message, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, arial24,
				colour, true, 90, 0, false);
	}

}