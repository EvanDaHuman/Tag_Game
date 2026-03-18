package culminating;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Arrays;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//Evan Wang 2024/06/14
//Final Culminating Game: Tag
//This program is a two-player tag game made with Java graphics

@SuppressWarnings("serial")
public class Tag_ISU extends JFrame implements Runnable, KeyListener {
	//Global Variables
	
	Thread thread; //Thread
	CardLayout cardLayout; //Storing different JPanels
    JPanel mainPanel; //Main Panel
    
    //Timer Variables
    Timer timer; 
    final int GAME_LENGTH = 160;
    int timeLeft = GAME_LENGTH;
    JLabel timerLabel;
    
    //Player variables
    final int WIN_VALUE = 80; //Score needed to win
    int player1 = 1;
    int player2 = 2;
    int it = player1; //Keep track of who's it
    //Score of each player
    int p1Score = 0;
    int p2Score = 0;
    
    //Image icons
    //Array to store all icons that can be used(in the inventory)
    ImageIcon avatars [][] = {{new ImageIcon("player1.png"), new ImageIcon("player2.png"), new ImageIcon("player1Flipped.png"), new ImageIcon("player2Flipped.png")}, {new ImageIcon("geodashP1.png"), new ImageIcon("geodashP2.png"), new ImageIcon("geodashP1.png"), new ImageIcon("geodashP2.png")}, {new ImageIcon("charmander.png"), new ImageIcon("squirtle.png"), new ImageIcon("charmander.png"), new ImageIcon("squirtle.png")}, {new ImageIcon("minecraftAlex.png"), new ImageIcon("minecraftSteve.png"), new ImageIcon("minecraftAlex.png"), new ImageIcon("minecraftSteve.png")}};
    int avatar = 0; //Keeps track of which icon is equipped
    //Facing forward
    ImageIcon player1Icon = avatars[avatar][0]; 
    ImageIcon player2Icon = avatars[avatar][1];
    //Backwards
    ImageIcon player1Flipped = avatars[avatar][2];
    ImageIcon player2Flipped = avatars[avatar][3];
    //Current
    ImageIcon player1Cur = player1Icon;
    ImageIcon player2Cur = player2Icon;
    
    ImageIcon itIcon = new ImageIcon("it.png");  //For it symbol
    ImageIcon powerUp = new ImageIcon("powerCube.png"); //For power up cube
    
    //Variables for the players' positions initialized with default values 
    int x1 = 650;
    int y1 = 664;
    int x2 = 830;
    int y2 = 664;
    
    //Variables for movement
    boolean left1, right1, jump1, left2, right2, jump2; //Keep track of which direction player is moving
    final double IT_SPEED = 10; //Player who's it runs faster
    final double NORMAL_SPEED = 8; 
    final double SUPER_SPEED = 12; //For speed boost power up
    //Current speed of players
    double speed1 = NORMAL_SPEED;
    double speed2 = NORMAL_SPEED;
	final double JUMP_SPEED = 15;		
	final double MAX_FALL_SPEED = -15;
	final double BOUNCE_PAD_SPEED = 25;
	//Velocities of player
	double xVel1 = 0;
	double yVel1 = 0;
	double xVel2 = 0;
	double yVel2 = 0;
	final double NORMAL_GRAVITY = 0.6;
	final double LOW_GRAVITY = 0.4; //For low gravity power up
	//Current gravity of players
	double gravity1 = NORMAL_GRAVITY;
	double gravity2 = NORMAL_GRAVITY;
	final double NORMAL_ACCEL = 0.25;
	final double SUPER_ACCEL = 0.75; //For speed boost power up
	//Current acceleration of players
	double accel1 = NORMAL_ACCEL;
	double accel2 = NORMAL_ACCEL;
	//Keeps track of if player is airborne
	boolean airborne1 = false;
	boolean airborne2 = false;
	//bottomCollision will be false if the player gets the ghost mode power up and can pass through the bottom of platforms
	boolean bottomCollision1 = true;
	boolean bottomCollision2 = true;
	
	//For adding a delay between tags(0.5 sec no tag-back rule)
    long lastSwitchTime = 0;
    final long DELAY = 500; 
    
    //For platforms
    final int PLATFORM_COUNT = 9;
    ImageIcon platformImages [] = new ImageIcon[PLATFORM_COUNT];
    //2D array for location and size of platforms
    int platformSizes [][] = {{0, 730, 1542, 82}, {700, 580, 654, 38}, {100, 580, 286, 38}, {330, 440, 322, 38}, {0, 300, 530, 38}, {900, 440, 222, 38}, {1122, 300, 222, 38}, {360, 140, 678, 38}, {1449, 673, 91, 57}};
    
    //For decorations
    final int DECORATION_COUNT = 9;
    ImageIcon decorationImages [] = new ImageIcon[DECORATION_COUNT];
    //2D array for location and size of decorations
    int decorationSizes [][] = {{203, 679, 40, 51}, {1160, 679, 40, 51}, {220, 530, 100, 50}, {840, 72, 45, 85}, {70, 160, 152, 140}, {1195, 169, 77, 131}, {1000, 389, 40, 51}, {440, 89, 40, 51}, {1240, 499, 44, 81}};
    
    //For power ups
    boolean powerCube = false;
    int powerX, powerY; 
    final int POWER_DURATION = 8; //Power up lasts for 8 seconds
    final int POWER_HEIGHT= 70;
    final int POWER_WIDTH = 67;
    final int POWER_COUNT = 3;
    ImageIcon powerImages [] = new ImageIcon[POWER_COUNT];
    //2D array for size of power up indicators
    int powerSizes[][] = {{120, 82}, {140, 100}, {120, 80}};
    //Current power up for each player (-1 means no power up)
    int p1Power = -1;
    int p2Power = -1;
    int powerPickedUp = 0; //Time when power up was picked up
    
    //For teleporters
    boolean teleporters = false;
    int teleporterPlaced = 0;
    final int TELEPORTER_SIZE = 60;
    final int TELEPORTER_DURATION = 10;
    ImageIcon teleporterImage = new ImageIcon("teleporter.png");
    //2D array to store the location of the two teleporters
    int [][] teleporterLocations = {{0, 0}, {0, 0}};
    
    //For music
    Clip menuMusic, gameMusic, gameOver;
    
    //For sound effects
    Clip powerSound1, powerSound2, bounce1, bounce2, teleport, ding;
    
    //Constant variables
    final int FPS = 60;
    final int SCREEN_WIDTH = 1540;
    final int SCREEN_HEIGHT = 790;
    final int PLAYER_WIDTH = 60;
    final int PLAYER_HEIGHT = 63;

    //Basically my main method and menu screen
    public Tag_ISU() {
        // Set the title of the window
        setTitle("Tag");
        
        // Initialize CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Load background image
        ImageIcon backgroundIcon = new ImageIcon("SkyBackground.jpg");
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BoxLayout(backgroundLabel, BoxLayout.Y_AXIS));

        // Load title image
        ImageIcon titleIcon = new ImageIcon("TagLogo.png");
        JLabel titleLabel = new JLabel(titleIcon);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create custom looking buttons
        JButton playButton = createCustomButton("PLAY", 300, 80, 30);
        JButton inventoryButton = createCustomButton("INVENTORY", 300, 80, 30);

        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 100)));
        backgroundLabel.add(titleLabel);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 50)));
        backgroundLabel.add(playButton);
        backgroundLabel.add(Box.createRigidArea(new Dimension(0, 20)));
        backgroundLabel.add(inventoryButton);
        
        // Create the play screen panel
        JPanel playScreen = createPlayScreen();
        
        // Create the inventory screen panel
        JPanel inventoryScreen = createInventoryScreen();
        
        // Add action listeners for the buttons
        playButton.addActionListener(e -> {
            newGame();
            startTimer();
        	cardLayout.show(mainPanel, "playScreen");
        	gameMusic.setFramePosition(0);
        	gameMusic.start();
        });	
        inventoryButton.addActionListener(e -> 
        {
        	cardLayout.show(mainPanel, "inventoryScreen");
        });	
        

        // Create a panel for the menu with the background image
        JPanel menuPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.add(backgroundLabel);

        // Add panels to the card layout
        mainPanel.add(menuPanel, "menu");
        mainPanel.add(playScreen, "playScreen");
        mainPanel.add(inventoryScreen, "inventoryScreen");

        // Show the main menu initially
        cardLayout.show(mainPanel, "menu");

        mainPanel.addKeyListener (this);
        mainPanel.setFocusable (true);
        // Set up the frame
        add(mainPanel);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        
        //Initialize images for platforms
        for (int i = 0; i < PLATFORM_COUNT; i++) {
        	platformImages[i] = new ImageIcon("platform" + (i+1) + ".png");
        }
        
        //Initialize images for decorations
        for (int i = 0; i < DECORATION_COUNT; i++) {
        	decorationImages[i] = new ImageIcon("decoration" + (i+1) + ".png");
        }
        
        //Initialize images for powerups
        for (int i = 0; i < POWER_COUNT; i++) {
        	powerImages[i] = new ImageIcon("power" + (i+1) + ".png");
        }
       	
        //Initialize audios and sound effects
        try {
    		//Music
        	AudioInputStream sound = AudioSystem.getAudioInputStream(new File ("menuMusic.wav"));
        	menuMusic = AudioSystem.getClip();
        	menuMusic.open(sound);
        	sound = AudioSystem.getAudioInputStream(new File ("gameMusic.wav"));
        	gameMusic = AudioSystem.getClip();
        	gameMusic.open(sound);
        	sound = AudioSystem.getAudioInputStream(new File ("gameOver.wav"));
        	gameOver = AudioSystem.getClip();
        	gameOver.open(sound);
        	
        	//Sound effects
        	sound = AudioSystem.getAudioInputStream(new File ("hypercharge.wav"));
    		powerSound1 = AudioSystem.getClip();
    		powerSound1.open(sound);
    		sound = AudioSystem.getAudioInputStream(new File ("hypercharge.wav"));
    		powerSound2 = AudioSystem.getClip();
    		powerSound2.open(sound);
    		sound = AudioSystem.getAudioInputStream(new File ("bounce.wav"));
    		bounce1 = AudioSystem.getClip();
    		bounce1.open(sound);
    		sound = AudioSystem.getAudioInputStream(new File ("bounce.wav"));
    		bounce2 = AudioSystem.getClip();
    		bounce2.open(sound);
    		sound = AudioSystem.getAudioInputStream(new File ("teleport.wav"));
    		teleport = AudioSystem.getClip();
    		teleport.open(sound);
    		sound = AudioSystem.getAudioInputStream(new File ("ding.wav"));
    		ding = AudioSystem.getClip();
    		ding.open(sound);
    		
    	} catch (Exception e) {
    	}
        
        //Start playing menu music
        menuMusic.setFramePosition(0);
        menuMusic.start();
        menuMusic.loop(menuMusic.LOOP_CONTINUOUSLY);
        
        //Start thread
        thread = new Thread(this);
		thread.start();
    }

    //Method that creates a white button with Comic sans font, rounded edges, and no border
    //Parameters include: text to be displayed(string), width and height of the button(int), and the font size(int)
    //Return: A JButton
    public JButton createCustomButton(String text, int width, int height, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        button.setPreferredSize(new Dimension(width, height));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Empty border for rounded edges
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    //Method that creates the game screen
    //Parameters: None
    //Return: A JPanel of the game screen
    public JPanel createPlayScreen() {
    	//Create JPanel
    	JPanel screenPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) { //Paint Compoenent
                super.paintComponent(g);
                //Background image
                ImageIcon backgroundIcon = new ImageIcon("SkyBackground.jpg");
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
                
                // Display scores
                g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
                g.setColor(Color.RED);
                String p1ScoreText = "P1: " + p1Score;
                g.drawString(p1ScoreText, SCREEN_WIDTH - g.getFontMetrics().stringWidth(p1ScoreText) - 20, 50);

                g.setColor(Color.BLUE);
                String p2ScoreText = "P2: " + p2Score;
                g.drawString(p2ScoreText, SCREEN_WIDTH - g.getFontMetrics().stringWidth(p2ScoreText) - 20, 100);
                
                //Draw decorations
                for (int i = 0; i < DECORATION_COUNT; i++) {
                	g.drawImage(decorationImages[i].getImage(), decorationSizes[i][0], decorationSizes[i][1], decorationSizes[i][2], decorationSizes[i][3], this);
                }
                
                //Display power up indicators if player has power up
            	if (p1Power != -1) {
            		int x = x1 - (powerSizes[p1Power][0] - PLAYER_WIDTH)/2;
                	int y = y1 - (powerSizes[p1Power][1] - PLAYER_HEIGHT);
            		g.drawImage(powerImages[p1Power].getImage(), x, y, powerSizes[p1Power][0], powerSizes[p1Power][1], this);
            	}
            	
                if (p2Power != -1) {
                	int x = x2 - (powerSizes[p2Power][0] - PLAYER_WIDTH)/2;
                	int y = y2 - (powerSizes[p2Power][1] - PLAYER_HEIGHT);
                	g.drawImage(powerImages[p2Power].getImage(), x, y, powerSizes[p2Power][0], powerSizes[p2Power][1], this);
                }
                
                //Draw the player icons
                g.drawImage(player1Cur.getImage(), x1, y1, PLAYER_WIDTH, PLAYER_HEIGHT, this);
                g.drawImage(player2Cur.getImage(), x2, y2, PLAYER_WIDTH, PLAYER_HEIGHT, this);
                
                //Draw the "it" symbol over the player who's it
                Image itImage = itIcon.getImage();
                if (it == player1) {
                	g.drawImage(itImage, x1+14, y1-38, 32, 28, this);
                } else {
                	g.drawImage(itImage, x2+14, y2-38, 32, 28, this);
                }
                
                //Draw platforms
                for (int i = 0; i < PLATFORM_COUNT; i++) {
                	g.drawImage(platformImages[i].getImage(), platformSizes[i][0], platformSizes[i][1], platformSizes[i][2], platformSizes[i][3], this);
                }
                
                //If there should be a power up cube, draw it
                if (powerCube) {
                	g.drawImage(powerUp.getImage(), powerX, powerY, POWER_WIDTH, POWER_HEIGHT, this);
                }
                
                //If there should be teleporters, draw them
                if (teleporters) {
                	g.drawImage(teleporterImage.getImage(), teleporterLocations[0][0], teleporterLocations[0][1], TELEPORTER_SIZE, TELEPORTER_SIZE, this);
                	g.drawImage(teleporterImage.getImage(), teleporterLocations[1][0], teleporterLocations[1][1], TELEPORTER_SIZE, TELEPORTER_SIZE, this);
                }
            }
        };

        // Create and add the back button
        ImageIcon backIcon = new ImageIcon("backButton.png");
        Image scaledImage = backIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JButton backButton = new JButton(scaledIcon);
        backButton.setPreferredSize(new Dimension(50, 50));
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorder(BorderFactory.createEmptyBorder());
        backButton.addActionListener(e -> { //Action listener
            cardLayout.show(mainPanel, "menu");
            newGame();
            gameMusic.stop();
            menuMusic.setFramePosition(0);
            menuMusic.start();
            menuMusic.loop(menuMusic.LOOP_CONTINUOUSLY);
            stopTimer();
        });
        
        //Create panel for the timer
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(backButton, BorderLayout.WEST);

        // Timer label setup
        timerLabel = new JLabel("160", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 40));
        timerLabel.setForeground(Color.WHITE);

        topPanel.add(timerLabel, BorderLayout.CENTER);

        screenPanel.add(topPanel, BorderLayout.NORTH);

        return screenPanel;
    }
    
    //Method that creates the inventory screen
    //Parameters: None
    //Return: JPanel for the inventory screen
    public JPanel createInventoryScreen(){
    	JPanel inventoryPanel = new JPanel(new BorderLayout()) {//Create JPanel
            @Override
            protected void paintComponent(Graphics g) { //Paint Component
                super.paintComponent(g);
                setLayout(null);//For absolute positioning
                
                //Background
                ImageIcon backgroundIcon = new ImageIcon("SkyBackground.jpg");
                g.drawImage(backgroundIcon.getImage(), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
                
                //Title
                g.setColor(Color.BLACK);
                g.setFont(new Font("Comic Sans MS", Font.BOLD, 60));
                g.drawString("INVENTORY", 550, 100);
                
                g.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
                
                //Variables for y-values of the two rows
                int row1 = 180;
                int row2 = 460;
                
                //Draw the equipped avatars on the right side
                g.setColor(Color.RED);
                g.drawString("PLAYER 1", 1200, row1);
                
                g.setColor(Color.BLUE);
                g.drawString("PLAYER 2", 1200, row2);
                
                g.drawImage(player1Icon.getImage(), 1180, row1+40, 189, 180, this);
                g.drawImage(player2Icon.getImage(), 1180, row2+40, 189, 180, this);
                
                //Draw the titles for each set of avatars
                g.setColor(Color.BLACK);
                g.drawString("DEFAULT", 215, row1);
                g.drawString("GEOMETRY DASH", 160, row2);
                g.drawString("POKEMON", 755, row1);
                g.drawString("MINECRAFT", 745, row2);
                
                //Draw the avatars
                g.drawImage(avatars[0][0].getImage(), 140, row1+40, 126, 120, this);
                g.drawImage(avatars[0][1].getImage(), 320, row1+40, 126, 120, this);
                g.drawImage(avatars[1][0].getImage(), 140, row2+40, 126, 120, this);
                g.drawImage(avatars[1][1].getImage(), 320, row2+40, 126, 120, this);
                g.drawImage(avatars[2][0].getImage(), 680, row1+40, 126, 120, this);
                g.drawImage(avatars[2][1].getImage(), 860, row1+40, 126, 120, this);
                g.drawImage(avatars[3][0].getImage(), 680, row2+40, 126, 120, this);
                g.drawImage(avatars[3][1].getImage(), 860, row2+40, 126, 120, this);
                
                //Create buttons to equip each avatar
                JButton equip1 = createCustomButton("EQUIP", 120, 40, 20);
                JButton equip2 = createCustomButton("EQUIP", 120, 40, 20);
                JButton equip3 = createCustomButton("EQUIP", 120, 40, 20);
                JButton equip4 = createCustomButton("EQUIP", 120, 40, 20);
                
                //Add action listeners to change the avatar
        	    equip1.addActionListener(e -> avatar = 0);
        	    equip2.addActionListener(e -> avatar = 1);
        	    equip3.addActionListener(e -> avatar = 2);
        	    equip4.addActionListener(e -> avatar = 3);
        	    
        	    //Set location and size of buttons
        	    equip1.setBounds(220, row1+180, 120, 40);  
                equip2.setBounds(220, row2+180, 120, 40);
                equip3.setBounds(780, row1+180, 120, 40);
                equip4.setBounds(780, row2+180, 120, 40);
                
                //Add buttons to JPanel
                add(equip1);
                add(equip2);
                add(equip3);
                add(equip4);
            }
            
	    };
        
        // Create and add the back button
        ImageIcon backIcon = new ImageIcon("backButton.png");
        Image scaledImage = backIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JButton backButton = new JButton(scaledIcon);
        backButton.setPreferredSize(new Dimension(50, 50));
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorder(BorderFactory.createEmptyBorder());
        backButton.addActionListener(e -> {
        	cardLayout.show(mainPanel, "menu");
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(backButton, BorderLayout.WEST);

        inventoryPanel.add(topPanel, BorderLayout.NORTH);

        return inventoryPanel;
    }
    
    
    //Method that resets the variables back to default for a new game
    //Parameters: None
    //Return: Void because all the variables changed are global variables
    void newGame() {
    	menuMusic.stop(); //Stop menu music
    	
    	//Change icons to face forward
    	player1Cur = player1Icon;
        player2Cur = player2Icon;
    	
    	//Reset timer and scores
        timeLeft = GAME_LENGTH;
    	p1Score = 0;
    	p2Score = 0;
    	
    	//Make a random player it
    	it = (int) (Math.random() * 2)+1;
    	
    	//Place players in spawn positions
    	x1 = 650;
        y1 = 664;
        x2 = 830;
        y2 = 664;
        
        //Reset the movement variables
        if (it == 1) {
        	speed1 = IT_SPEED;
            speed2 = NORMAL_SPEED;
        } else {
        	speed1 = NORMAL_SPEED;
            speed2 = IT_SPEED;
        }

        gravity1 = NORMAL_GRAVITY;
        gravity2 = NORMAL_GRAVITY;
        
        accel1 = NORMAL_ACCEL;
        accel2 = NORMAL_ACCEL;
        
        bottomCollision1 = true;
        bottomCollision2 = true;
        
        //Remove all powerups and powercubes
        p1Power = -1;
        p2Power = -1;
        
        powerCube = false;
    }
    
    //Method that constantly loops and changes the screen based on inputs
    //Parameters: None
    //Return type: Void because all variables that are changed are global
    @Override
	public void run() {
		while(true) {
			move();
			keepInBound();
			checkTag();
			checkWinner();
			
			//Check collision
			boolean touching1 = false;
			boolean touching2 = false;
			for (int i = 0; i < PLATFORM_COUNT; i++) {
				if (checkCollision1(platformSizes[i])) {
					touching1 = true;
				}
				if (checkCollision2(platformSizes[i])) {
					touching2 = true;
				}
				if (i == 8) {
					checkBounce(platformSizes[i]);
				}
			}
			//If no collisions happened, then make the player airborne since they are not on a platform
			if (!touching1) {
				airborne1 = true;
			}
			if (!touching2) {
				airborne2 = true;
			}
			
			//If there is a power up cube, check if a player picks it up
			if (powerCube) {
				checkPowerUp();
			}
			
			//If there are teleporters, check if a player collides with them
			if (teleporters) {
				checkTeleporterP1();
				checkTeleporterP2();
			}
			
			//Change the icons
		    player1Icon = avatars[avatar][0];
		    player2Icon = avatars[avatar][1];
		    player1Flipped = avatars[avatar][2];
		    player2Flipped = avatars[avatar][3];
		  
			this.repaint(); //Repaint the screen
			try {
				Thread.sleep(1000/FPS);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

    
    @Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
    
    //Method that checks if a key is pressed
    //Parameters: KeyEvent e(button that is pressed)
    //Return Type: Void because all variables that are changed are global
    @Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_A) {
			left1 = true;
			right1 = false;
		}else if(key == KeyEvent.VK_D) {
			right1 = true;
			left1 = false;
		} else if(key == KeyEvent.VK_W) {
			jump1 = true;
		}  else if (key == KeyEvent.VK_LEFT) {
			left2 = true;
			right2 = false;
		} else if (key == KeyEvent.VK_RIGHT) {
			right2 = true;
			left2 = false;
		} else if (key == KeyEvent.VK_UP) {
			jump2 = true;
		} 
	}

    //Method that checks if a key is released
    //Parameters: KeyEvent e (button that is pressed)
    //Return Type: Void because all variables that are changed are global
	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_A) {
			left1 = false;
			xVel1 = 0;
		}else if(key == KeyEvent.VK_D) {
			right1 = false;
			xVel1 = 0;
		}else if(key == KeyEvent.VK_W) {
			jump1 = false;
		} else if (key == KeyEvent.VK_LEFT) {
			left2 = false;
			xVel2 = 0;
		} else if (key == KeyEvent.VK_RIGHT) {
			right2 = false;
			xVel2 = 0;
		} else if (key == KeyEvent.VK_UP) {
			jump2 = false;
		} 
	}
    
	//Method that changes the players locations based on their movement
	//Parameters: None
	//Return: Void because all variables that are changed are global
    void move() {
    	//Check for super speed
    	if (p1Power == 0) {
    		speed1 = SUPER_SPEED;
    		accel1 = SUPER_ACCEL;
    	} else {
    		if (it == player1) {
    			speed1 = IT_SPEED;
    		} else {
    			speed1 = NORMAL_SPEED;
    		}
    		accel1 = NORMAL_ACCEL;
    	}
    	
    	if (p2Power == 0) {
    		speed2 = SUPER_SPEED;
    		accel2 = SUPER_ACCEL;
    	} else {
    		if (it == player2) {
    			speed2 = IT_SPEED;
    		} else {
    			speed2 = NORMAL_SPEED;
    		}
    		accel2 = NORMAL_ACCEL;
    	}
    	
    	//Check for low-gravity
    	if (p1Power == 1) {
    		gravity1 = LOW_GRAVITY;
    	} else {
    		gravity1 = NORMAL_GRAVITY;
    	}
    	
    	if (p2Power == 1) {
    		gravity2 = LOW_GRAVITY;
    	} else {
    		gravity2 = NORMAL_GRAVITY;
    	}
    	
    	//Check for ghost mode(phase through bottom of platforms)
    	if (p1Power == 2) {
    		bottomCollision1 = false;
    	} else {
    		bottomCollision1 = true;
    	}
    	
    	if (p2Power == 2) { 
    		bottomCollision2 = false;
    	} else {
    		bottomCollision2 = true;
    	}
    	
    	//If player moves left or right
    	//We increase the velocity by accelerating until it reaches the top speed
		if(left1) {
			player1Cur = player1Icon;
			if (xVel1 == 0) {
				xVel1 = -2;
			}
			if (xVel1 > -speed1) {
				xVel1 -= accel1;
			} else {
				xVel1 = -speed1;
			}
		} else if(right1) {
			player1Cur = player1Flipped;
			if (xVel1 == 0) {
				xVel1 = 2;
			}
			if (xVel1 < speed1) {
				xVel1 += accel1;
			} else {
				xVel1 = speed1;
			}
		} else {
			xVel1 = 0;
		}
		
		if(left2) {
			player2Cur = player2Icon;
			if (xVel2 == 0) {
				xVel2 = -2;
			}
			if (xVel2 > -speed2) {
				xVel2 -= accel2;
			} else {
				xVel2 = -speed2;
			}
		} else if(right2) {
			player2Cur = player2Flipped;
			if (xVel2 == 0) {
				xVel2 = 2;
			}
			if (xVel2 < speed2) {
				xVel2 += accel2;
			} else {
				xVel2 = speed2;
			}
		} else {
			xVel2 = 0;
		}
		
		//For jumping and falling
		if(airborne1) {
			if (yVel1 > MAX_FALL_SPEED) {
				yVel1 -= gravity1;
			}
		}else {
			if(jump1) {
				airborne1 = true;
				yVel1 = JUMP_SPEED;
			} 
		}
		
		if(airborne2) {
			if (yVel2 > MAX_FALL_SPEED) {
				yVel2 -= gravity2;
			}
		}else {
			if(jump2) {
				airborne2 = true;
				yVel2 = JUMP_SPEED;
			}
		}
		
		
		//Add velocities to the x and y values of the players
		x1 += xVel1;
		y1 -= yVel1;
		
		x2 += xVel2;
		y2 -= yVel2;
	}
	
    //Method that keeps players on the screen
    //Parameters: None:
    //Return Type: Void because all variables that are changed are global
	void keepInBound() {
		if(x1 < 0) {
			x1 = 0;
		} else if(x1 > SCREEN_WIDTH - PLAYER_WIDTH) {
			x1 = SCREEN_WIDTH - PLAYER_WIDTH;
		}
		if(y1 < 0) {
			y1 = 0;
			yVel1 = 0;
		} else if(y1 > SCREEN_HEIGHT - PLAYER_HEIGHT) {
			y1 = SCREEN_HEIGHT - PLAYER_HEIGHT;
			airborne1 = false;
			yVel1 = 0;
		}
		
		if(x2 < 0) {
			x2 = 0;
		} else if(x2 > SCREEN_WIDTH - PLAYER_WIDTH) {
			x2 = SCREEN_WIDTH - PLAYER_WIDTH;
		}
		if(y2 < 0) {
			y2 = 0;
			yVel2 = 0;
		} else if(y2 > SCREEN_HEIGHT - PLAYER_HEIGHT) {
			y2 = SCREEN_HEIGHT - PLAYER_HEIGHT;
			airborne2 = false;
			yVel2 = 0;
		}
	}
	
	//Method that checks if the players tag each other
	//Parameters: None
	//Return Type: Void because all variables that are changed are global
	void checkTag() {
		long currentTime = System.currentTimeMillis(); //Current time
		if (x1 > x2-PLAYER_WIDTH && x1 < x2+PLAYER_WIDTH && y1 > y2-PLAYER_HEIGHT && y1 < y2+PLAYER_HEIGHT) { //if players collide, switch who's it
			if (currentTime - lastSwitchTime >= DELAY) { //Can only be tagged 0.5 seconds after the previoust tag
				if (it == player1) {
					it = player2;
					speed2 = IT_SPEED;
					speed1 = NORMAL_SPEED;
					ding.setFramePosition(0);
					ding.start();
				} else {
					it = player1;
					speed1 = IT_SPEED;
					speed2 = NORMAL_SPEED;
					ding.setFramePosition(0);
					ding.start();
				}
			}
			lastSwitchTime = currentTime;
		}
	}
	
	//Method that checks of player 1 and a platform
	//Parameters: The location and size of the platform (array)
	//Return Type: Boolean(whether a collision occurred or not)
	public boolean checkCollision1(int [] platform) {
		//The sides of the player and platform
		double left1 = x1;
		double right1 = x1 + PLAYER_WIDTH;
		double top1 = y1;
		double bottom1 = y1 + PLAYER_HEIGHT;
		double left2 = platform[0];
		double right2 = platform[0] + platform[2];
		double top2 = platform[1];
		double bottom2 = platform[1] + platform[3];

		if(right1 > left2 && left1 < left2 && right1 - left2 < bottom1 - top2 && right1 - left2 < bottom2 - top1)
        {
            //player collides from left side of the wall
			x1 = platform[0] - PLAYER_WIDTH;
        }
        else if(left1 < right2 && right1 > right2 && right2 - left1 < bottom1 - top2 && right2 - left1 < bottom2 - top1)
        {
            //player collides from right side of the wall
        	x1 = platform[0] + platform[2];
        }
        else if(bottom1 > top2 && top1 < top2 && left1 < right2 && right1 > left2)
        {
            //player collides from top side of the wall
        	y1 = platform[1] - PLAYER_HEIGHT;
        	if (yVel1 <= 0) {
        		airborne1 = false;
        		yVel1 = 0;
        	}
        	
        }
        else if(top1 < bottom2 && bottom1 > bottom2 && left1 < right2 && right1 > left2 && bottomCollision1)
        {
            //player collides from bottom side of the wall
        	y1 = platform[1] + platform[3];
        	airborne1 = true;
        	jump1 = false;
        	yVel1 = 0;
        } else {
        	return false;
        }
		return true;
	}
	
	//Method that checks of player 2 and a platform
	//Parameters: The location and size of the platform (array)
	//Return Type: Boolean(whether a collision occurred or not)
	public boolean checkCollision2(int [] platform) {
		//The sides of the player and platform
		double left1 = x2;
		double right1 = x2 + PLAYER_WIDTH;
		double top1 = y2;
		double bottom1 = y2 + PLAYER_HEIGHT;
		double left2 = platform[0];
		double right2 = platform[0] + platform[2];
		double top2 = platform[1];
		double bottom2 = platform[1] + platform[3];
		
		if(right1 > left2 && left1 < left2 && right1 - left2 < bottom1 - top2 && right1 - left2 < bottom2 - top1)
        {
            //player collides from left side of the wall
			x2 = platform[0] - PLAYER_WIDTH;
        }
        else if(left1 < right2 && right1 > right2 && right2 - left1 < bottom1 - top2 && right2 - left1 < bottom2 - top1)
        {
            //player collides from right side of the wall
        	x2 = platform[0] + platform[2];
        }
        else if(bottom1 > top2 && top1 < top2 && left1 < right2 && right1 > left2)
        {
            //player collides from top side of the wall
        	y2 = platform[1] - PLAYER_HEIGHT;
        	if (yVel2 <= 0) {
        		airborne2 = false;
        		yVel2 = 0;
        	}
        	
        }
        else if(top1 < bottom2 && bottom1 > bottom2 && left1 < right2 && right1 > left2 && bottomCollision2)
        {
            //player collides from bottom side of the wall
        	y2 = platform[1] + platform[3];
        	airborne2 = true;
        	jump2 = false;
        	yVel2 = 0;
        } else {
        	return false;
        }
		return true;
	}
	
	//Method that checks if the players jump on the bounce pad
	//Parameters: An array of the location and size of the bounce pad
	//Return Type: Void because all variables that are changed are global
	void checkBounce(int [] bouncePad) {
		int left1 = x1;
		int right1 = x1 + PLAYER_WIDTH;
		int bottom1 = y1 + PLAYER_HEIGHT;
		//If the player collides with the top of the bounce pad, make them jump super high
		if (left1 < bouncePad[0] + bouncePad[2] && right1 > bouncePad[0] && bottom1 >= bouncePad[1]) {
			yVel1 = BOUNCE_PAD_SPEED;
			airborne1 = true;
			bounce1.setFramePosition(0);
			bounce1.start();
		}
		
		int left2 = x2;
		int right2 = x2 + PLAYER_WIDTH;
		int bottom2 = y2 + PLAYER_HEIGHT;
		//If the player collides with the top of the bounce pad, make them jump super high
		if (left2 < bouncePad[0] + bouncePad[2] && right2 > bouncePad[0] && bottom2 >= bouncePad[1]) {
			yVel2 = BOUNCE_PAD_SPEED;
			airborne2 = true;
			bounce2.setFramePosition(0);
			bounce2.start();
		}
	}
    
	//Method that places the power up cube in a random location
	//Parameters: None
	//Return Type: Void because all variables that are changed are global
	void powerUp() { 
		//place power cube at random position on any platform
		int randomPlatform = (int) (Math.random() * 9); 
		powerY = platformSizes[randomPlatform][1] - POWER_HEIGHT;
		int left = platformSizes[randomPlatform][0];
		int right = platformSizes[randomPlatform][0] + platformSizes[randomPlatform][2] - POWER_WIDTH;
		int randomX = (int) (Math.random() * (right-left+1)) + left;
		powerX = randomX;
	}
	
	//Method that that checks if player collides with power cube and gives player a power up
	//Parameters: None
	//Return Type: Void because all variables that are changed are global
	void checkPowerUp() { //check if player touches power cube
		//Variables for sides of player and power cube
		double left1 = x1;
		double right1 = x1 + PLAYER_WIDTH;
		double top1 = y1;
		double bottom1 = y1 + PLAYER_HEIGHT;
		double left2 = powerX;
		double right2 = powerX + POWER_WIDTH;
		double top2 = powerY;
		double bottom2 = powerY + POWER_HEIGHT;
		
		//If they collide give them a random power up
		if (left1 < right2 && right1 > left2 && top1 < bottom2 && bottom1 > top2) {
			int random = (int) (Math.random() * 3);
			p1Power = random;
			powerCube = false;
			powerPickedUp = timeLeft;
			powerSound1.setFramePosition(0);
			powerSound1.start();
			
		}
		
		left1 = x2;
		right1 = x2 + PLAYER_WIDTH;
		top1 = y2;
		bottom1 = y2 + PLAYER_HEIGHT;
		
		//If they collide give them a random power up
		if (left1 < right2 && right1 > left2 && top1 < bottom2 && bottom1 > top2) {
			int random = (int) (Math.random() * 3);
			p2Power = random;
			powerCube = false;
			powerPickedUp = timeLeft;
			powerSound2.setFramePosition(0);
			powerSound2.start();
		}
		
	}
	
	//Method that places two teleporters at random position on any platform
	//Parameters: None
	//Return Type: Void because all variables that are changed are global
	void placeTeleporter() { 
		int randomPlatform = (int) (Math.random() * 9);
		teleporterLocations[0][1] = platformSizes[randomPlatform][1] - TELEPORTER_SIZE;
		int left = platformSizes[randomPlatform][0];
		int right = platformSizes[randomPlatform][0] + platformSizes[randomPlatform][2] - POWER_WIDTH;
		int randomX = (int) (Math.random() * (right-left+1)) + left;
		teleporterLocations[0][0] = randomX;
		int prev = randomPlatform;
		while (randomPlatform == prev) { //Makes sure the two teleportes are NOT on the same platform
			randomPlatform = (int) (Math.random() * 9);
		}
		teleporterLocations[1][1] = platformSizes[randomPlatform][1] - TELEPORTER_SIZE;
		left = platformSizes[randomPlatform][0];
		right = platformSizes[randomPlatform][0] + platformSizes[randomPlatform][2] - POWER_WIDTH;
		randomX = (int) (Math.random() * (right-left+1)) + left;
		teleporterLocations[1][0] = randomX;
	}

	//Method that checks if player 1 collides with the teleporters
	//Parameters: None
	//Return Type: Void because all variables that are changed are global
	void checkTeleporterP1() {
		//If collide with teleporter 1, send to teleporter 2
		double left1 = x1;
		double right1 = x1 + PLAYER_WIDTH;
		double top1 = y1;
		double bottom1 = y1 + PLAYER_HEIGHT;
		double left2 = teleporterLocations[0][0];
		double right2 = teleporterLocations[0][0] + TELEPORTER_SIZE;
		double top2 = teleporterLocations[0][1];
		double bottom2 = teleporterLocations[0][1] + TELEPORTER_SIZE;
		
		if (left1 < right2 && right1 > left2 && top1 < bottom2 && bottom1 > top2) {
			teleporters = false;
			x1 = teleporterLocations[1][0];
			y1 = teleporterLocations[1][1];
			teleport.setFramePosition(0);
			teleport.start();
			
		}
		
		//If collide with teleporter 2, send to teleporter 1
		left2 = teleporterLocations[1][0];
		right2 = teleporterLocations[1][0] + TELEPORTER_SIZE;
		top2 = teleporterLocations[1][1];
		bottom2 = teleporterLocations[1][1] + TELEPORTER_SIZE;
		
		if (left1 < right2 && right1 > left2 && top1 < bottom2 && bottom1 > top2) {
			teleporters = false;
			x1 = teleporterLocations[0][0];
			y1 = teleporterLocations[0][1];
			teleport.setFramePosition(0);
			teleport.start();
		}
	}
	
	//Method that checks if player 2 collides with the teleporters
	//Parameters: None
	//Return Type: Void because all variables that are changed are global
	void checkTeleporterP2() {
		//If collide with teleporter 1, send to teleporter 2
		double left1 = x2;
		double right1 = x2 + PLAYER_WIDTH;
		double top1 = y2;
		double bottom1 = y2 + PLAYER_HEIGHT;
		double left2 = teleporterLocations[0][0];
		double right2 = teleporterLocations[0][0] + TELEPORTER_SIZE;
		double top2 = teleporterLocations[0][1];
		double bottom2 = teleporterLocations[0][1] + TELEPORTER_SIZE;
		
		if (left1 < right2 && right1 > left2 && top1 < bottom2 && bottom1 > top2) {
			teleporters = false;
			x2 = teleporterLocations[1][0];
			y2 = teleporterLocations[1][1];
			teleport.setFramePosition(0);
			teleport.start();
			
		}
		
		//If collide with teleporter 1, send to teleporter 2
		left2 = teleporterLocations[1][0];
		right2 = teleporterLocations[1][0] + TELEPORTER_SIZE;
		top2 = teleporterLocations[1][1];
		bottom2 = teleporterLocations[1][1] + TELEPORTER_SIZE;
		
		if (left1 < right2 && right1 > left2 && top1 < bottom2 && bottom1 > top2) {
			teleporters = false;
			x2 = teleporterLocations[0][0];
			y2 = teleporterLocations[0][1];
			teleport.setFramePosition(0);
			teleport.start();
		}
	}

	//Method that ends the game if any player's score reaches 80
	//Parameters: None
	//Return Type: Void because all variables that are changed are global
	private void checkWinner() {
		if (p1Score >= WIN_VALUE || p2Score >= WIN_VALUE) {
			//Show the game over screen
			JPanel endScreen = gameOver();
	        mainPanel.add(endScreen, "endScreen");
	        cardLayout.show(mainPanel, "endScreen");
		}
		
	}
	
	//Method that creates the game over screen
	//Parameters: None
	//Return Type: JPanel of the screen
	private JPanel gameOver() {
		timer.stop(); //Stop the timer
		
		//Stop game music and play game over music
		gameMusic.stop();
		gameOver.setFramePosition(0);
		gameOver.start();
		gameOver.loop(gameOver.LOOP_CONTINUOUSLY);
		
		//Display text and image of loser
		ImageIcon loserText, loserImage;
		int width = 332;
		int height1 = 188;
		int height2 = 349;
	    String message;
	    if (p2Score > p1Score) {
	    	loserText = new ImageIcon("redLose.png");
	    	loserImage = player1Icon;
	    	message = "Player 1 has lost!!!";
	    } else if (p1Score > p2Score) {
	    	loserText = new ImageIcon("blueLose.png");
	    	loserImage = player2Icon;
	    	message = "Player 2 has lost!!!";
	    } else {
	    	if (it == player1) {
	    		loserText = new ImageIcon("redLose.png");
	    		loserImage = player1Icon;
		    	message = "Player 1 has lost!!!";
	    	} else {
	    		loserText = new ImageIcon("blueLose.png");
	    		loserImage = player2Icon;
		    	message = "Player 2 has lost!!!";
	    	}
	    }
	    newGame(); //Reset variables to default
	    JPanel endPanel = new JPanel(new BorderLayout()) { //Create JPanel
            @Override
            protected void paintComponent(Graphics g) { //Graphics component
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("SkyBackground.jpg");
                g.drawImage(backgroundIcon.getImage(), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
                g.drawImage(loserText.getImage(), SCREEN_WIDTH - width - 285, (SCREEN_HEIGHT - height1 - height2)/2, this);
                g.setColor(Color.BLACK);
                g.setFont(new Font("Comic Sans MS", Font.BOLD, 38));
                g.drawString(message, SCREEN_WIDTH - width - 300, (SCREEN_HEIGHT - height1 - height2)/2 - 30);
                g.drawImage(loserImage.getImage(), SCREEN_WIDTH - width - 300, (SCREEN_HEIGHT - height1 - height2)/2 + height1, width, height2, this);
            }
            
	    };
	    //Create buttons to restart or go back to menu
	    JButton restartButton = createCustomButton("RESTART", 400, 120, 40);
	    JButton menuButton = createCustomButton("BACK TO MENU", 600, 120, 40);
	    
	    //Create panel for buttons
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
	    buttonPanel.setOpaque(false); 

	    restartButton.setPreferredSize(new Dimension(800, 100));
	    menuButton.setPreferredSize(new Dimension(800, 100));

	    //Create action listeners
	    restartButton.addActionListener(e -> { 
	        newGame();
	        startTimer();
	        cardLayout.show(mainPanel, "playScreen");
	        gameOver.stop();
	        gameMusic.setFramePosition(0);
        	gameMusic.start();
	    });
	    menuButton.addActionListener(e -> {
	    	cardLayout.show(mainPanel, "menu");
	    	gameOver.stop();
	    	menuMusic.setFramePosition(0);
	        menuMusic.start();
	        menuMusic.loop(menuMusic.LOOP_CONTINUOUSLY);
	    });

	    buttonPanel.add(Box.createVerticalGlue()); 
	    buttonPanel.add(restartButton);
	    buttonPanel.add(Box.createVerticalStrut(50)); 
	    buttonPanel.add(menuButton);
	    buttonPanel.add(Box.createVerticalGlue()); 

	    JPanel wrapperPanel = new JPanel(new GridBagLayout());
	    wrapperPanel.setOpaque(false); 
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.weightx = 1.0;
	    gbc.weighty = 1.0;
	    gbc.anchor = GridBagConstraints.EAST;

	    wrapperPanel.add(buttonPanel, gbc);

	    endPanel.add(wrapperPanel, BorderLayout.WEST);
	    
        return endPanel;
	 }
	
	
	//Method that starts the timer and keeps track of time
	//Parameters: None
	//Return: Void because all variables changed are global variables
    private void startTimer() {
    	//Reset timer
        timeLeft = GAME_LENGTH;
        timerLabel.setText(String.valueOf(timeLeft));
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText(String.valueOf(timeLeft));
                //Increment score of each second a player is not it
                if (it == player1) {
                	p2Score++;
                } else {
                	p1Score++;
                }
                
                if (timeLeft % 20 == 10) { //Power up: Every 20 seconds starting from 10
                	powerUp();
                	powerCube = true;
                }
                
                if (timeLeft + POWER_DURATION < powerPickedUp) {//Power up ran out after 8 seconds
                	p1Power = -1;
                	p2Power = -1;
                }
                
                if (timeLeft % 20 == 15) { //Place teleporters every 20 seconds starting from 5
                	placeTeleporter();
                	teleporters = true;
                	teleporterPlaced = timeLeft;
                }
                
                if (timeLeft + TELEPORTER_DURATION < teleporterPlaced) { //Teleporters dissappear if not used in 8 seconds
                	teleporters = false;
                }
            }	
        });
        timer.start(); //Start timer
    }
    
    
    //Method that stops the timer
    //Parameters: None
    //Return: Void
    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
    
  //Main Method that displays the menu screen
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Tag_ISU().setVisible(true);
        });
    }
}
