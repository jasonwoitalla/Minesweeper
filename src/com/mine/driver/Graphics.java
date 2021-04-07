package com.mine.driver;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.mine.graphics.Canvas;

public class Graphics {
	
	Logic logic;
	Canvas canvas;
	private int windowWidth = 0, windowHeight = 0;
	
	boolean[][] revealed;
	boolean[][] flagged;
	boolean showMines = false;
	
	//Images
	BufferedImage tile, flag, mine, xIcon;
	BufferedImage logo, play, options;
	BufferedImage[] faces = new BufferedImage[1];
	BufferedImage[] clickFaces = new BufferedImage[1];
	
	//User Input
	int level = 3;
	boolean custom = false, processInput = false;
	int gridWidth = 30, gridHeight = 16;
	int numberOfMines = 0;
	
	int xOffset = 0;
	int yOffset = 64;

	JPanel inputPanel;
	
	//post-condition: creates a Logic driver class
	public Graphics() {
		try {
			tile = ImageIO.read(new File("res/tile.png"));
			flag = ImageIO.read(new File("res/Flag.png"));
			mine = ImageIO.read(new File("res/Mine.png"));
			xIcon = ImageIO.read(new File("res/x.png"));
			logo = ImageIO.read(new File("res/logo.png"));
			play = ImageIO.read(new File("res/play.png"));
			options = ImageIO.read(new File("res/options.png"));
			faces[0] = ImageIO.read(new File("res/cole_1.png"));
			clickFaces[0] = mine;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		createWindow(640, 480);//Input window
		windowWidth = 640;
		windowHeight = 480;
		drawInput();
	}
	
	//pre-condition: width > 0, height > 0
	//post-condition: creates a JFrame with specified width and height 
	void createWindow(int width, int height) {
		canvas = new Canvas("Minesweeper", width, height, new MouseInput(this));
		canvas.loadFont("res/fonts/digital-7.ttf");
	}
	
	//pre-condition: a JFrame has been created
	//post-condition: draws GUI asking for user input
	void drawInput() {
		canvas.setInkColor(new Color(191,191,191));
		canvas.fillRectangle(0, 0, 640, 480);
		
		inputPanel = canvas.addRadioGroup(3, new String[]{"Beginner", "Intermediate", "Expert"});
		
		canvas.drawImage(logo, 5, 5);
		canvas.drawImage(play, 640-260, 325);
		canvas.drawImage(options, 5, 325);
	}
	
	//pre-condition: drawInput() has been called, user has clicked input data
	//post-condition: creates a logic class with input data
	public void processInput() {
		canvas.erase();
		level = canvas.getSelected();

		if(!custom) {
			switch(level) {
			case 1:
				gridWidth = 9; 
				gridHeight = 9;
				numberOfMines = 10;
				break;
			case 2:
				gridWidth = 16;
				gridHeight = 16;
				numberOfMines = 40;
				break;
			case 3:
				gridWidth = 30;
				gridHeight = 16;
				numberOfMines = 99;
				break;
			}
		}
		
		showMines = false;
		windowWidth = gridWidth*32;
		windowHeight = gridHeight*32;
		canvas.setSize(windowWidth, windowHeight);
		revealed = new boolean[gridWidth][gridHeight];
		flagged = new boolean[gridWidth][gridHeight];
		
		logic = new Logic(gridWidth, gridHeight, numberOfMines, this);
		drawBoard(logic.getBoard(), xOffset, yOffset);
	}
	
	//pre-condition: board is filled with integers ranging from -1 to 8, JFrame is created
	//post-condition: populates JFrame with GUI based on board
	public void drawBoard(int[][] board, int xOffset, int yOffset) {
		canvas.erase();
		drawLines(xOffset, yOffset);
		
		for(int r = 0; r < board.length; r++) {
			for(int c = 0; c < board[0].length; c++) {//Loop through board
				if(revealed[r][c]) //Check if this should tile should be shown or hidden
					drawNumber(board[r][c], r, c, xOffset, yOffset);
				else if(!showMines || board[r][c] != -1){
					drawTile(r, c, xOffset, yOffset);//Draw tile
					if(flagged[r][c])//Check if this tile has a flag on it
						drawFlag(r, c, xOffset, yOffset);
				}
				
				if(board[r][c] < 0 && showMines)//Show the mine
					drawMine(r, c, xOffset, yOffset);
			}
		}
		
		drawFace();
		drawMineCount(5,7);
		drawClock();
	}
	
	//pre-condition: faces array is populated
	private void drawFace() {
		int x = windowWidth/2;
		if(!clicked) {
			canvas.drawImage(faces[0], x, 0, 56, 64);
		}else
			canvas.drawImage(clickFaces[0], x, 0, 56, 64);
	}
	
	//post-condition: draws grid lines with offset
	private void drawLines(int xOffset, int yOffset) {
		canvas.setInkColor(Color.GRAY);
		
		for(int x = 1; x < gridWidth; x++) 
			canvas.drawLine((x*32)+xOffset, yOffset, (x*32)+xOffset, (gridHeight*32)+yOffset);
		for(int y = 0; y < gridHeight; y++) 
			canvas.drawLine(xOffset, (y*32)+yOffset, (gridWidth*32)+xOffset, (y*32)+yOffset);
		
		canvas.setInkColor(Color.BLACK);
	}
	
	//post-condition: draws the number at row and col with offset
	private void drawNumber(int num, int row, int col, int xOffset, int yOffset) {
		if(num <= 0)
			return;
		
		switch(num) {
		case 1://Blue
			canvas.setInkColor(new Color(0, 0, 255));
			break;
		case 2://Green
			canvas.setInkColor(new Color(0, 150, 0));
			break;
		case 3://Red
			canvas.setInkColor(new Color(200, 0, 0));
			break;
		case 4://Dark Blue
			canvas.setInkColor(new Color(0,0,150));
			break;
		case 5://Dark Red
			canvas.setInkColor(new Color(100,0,0));
			break;
		case 6:
			canvas.setInkColor(new Color(0,75,0));
			break;
		}
		
		canvas.setFont(new Font("Serif", Font.BOLD, 24));
		canvas.drawString(Integer.toString(num), (row*32)+xOffset+11, (col*32)+yOffset+24);
		canvas.setInkColor(Color.BLACK);
	}
	
	private void drawTile(int row, int col, int xOffset, int yOffset) {
		canvas.drawImage(tile, (row*32)+xOffset, (col*32)+yOffset);
	}
	
	private void drawFlag(int row, int col, int xOffset, int yOffset) {
		if(!showMines)
			canvas.drawImage(flag, (row*32)+xOffset+2, (col*32)+yOffset+2);
		else
			canvas.drawImage(xIcon, (row*32)+xOffset, (col*32)+yOffset);
	}
	
	private void drawMine(int row, int col, int xOffset, int yOffset) {
		canvas.drawImage(mine, (row*32)+xOffset+3, (col*32)+yOffset+3);
	}
	
	private void drawMineCount(int x, int y) {
		canvas.setFont(new Font("digital-7", Font.BOLD, 56));
		canvas.setInkColor(Color.BLACK);
		canvas.fillRectangle(x, y, 100, 50);
		
		canvas.setInkColor(Color.RED);
		canvas.drawString(toDigital(logic.minesLeft()), x+4, y+43);
	}
	
	//post-condition: draws a clock to the right of the screen
	public void drawClock() {
		int x = windowWidth-105;
		int y = 7;
		canvas.setFont(new Font("digital-7", Font.BOLD, 56));
		canvas.setInkColor(Color.BLACK);
		canvas.fillRectangle(x, y, 100, 50);
		
		canvas.setInkColor(Color.RED);
		canvas.drawString(toDigital(logic.getTime()), x+4, y+43);
	}
	
	//pre-condition: num is greater than 0 and less than 1000
		//post-condition: returns a three character string format: 0-0-num
		private String toDigital(int num) {
			if(num < 10) 
				return "00" + num;
			else if(num < 100) 
				return "0" + num;
			else 
				return "" + num;
		}
	
	public void leftMouseClick(int x, int y) {
		if(!processInput) {
			System.out.println("X: " + x + " | Y: " + y);
			
			if(x >= 385 && x <= 625 && y >= 330 && y <= 420) {//Play Button
				processInput = true;
				processInput();
				canvas.removePanel(inputPanel);
			}else if(x >= 15 && x <= 250 && y >= 330 && y <= 420) {//Options Button
				System.out.println("Options");
			}
			
			return;
		}
		
		clicked = true;
		if(y < yOffset) {
			processInput();
			return;
		}
		
		if(showMines)
			return;
		
		int row = ((x-xOffset)/32);
		int col = ((y-yOffset)/32);
		
		if(!flagged[row][col])
			showMines = logic.reveal(row, col, revealed, flagged);
		else
			logic.removeFlag(row, col, flagged);
		
		if(showMines)
			logic.stopTimer();
		drawBoard(logic.getBoard(), xOffset, yOffset);
	}
	
	public void rightMouseClick(int x, int y) {
		if(showMines || y < yOffset)
			return;
		
		int row = ((x-xOffset)/32);
		int col = ((y-yOffset)/32);
		
		logic.flag(row, col, flagged);
		drawBoard(logic.getBoard(), xOffset, yOffset);
	}
	
	boolean clicked = false;
	public void unClick() {
		if(!processInput)
			return;
		
		clicked = false;
		drawFace();
	}
}
