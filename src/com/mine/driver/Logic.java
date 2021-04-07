package com.mine.driver;

import java.util.Timer;
import java.util.TimerTask;

public class Logic {
	
	int[][] board; //-1 is a mine | 0 is blank | # is number of mines nearby
	int numberOfMines = 0;
	int numberOfFlags = 0;
	int time = 0;
	Graphics graphics;
	boolean gameOver = false;
	
	Timer timer = new Timer();
	
	//pre-condition: 0 < level <= 3
	//post-condition: calls generateBoard with preset data
	public Logic(int level, Graphics graphics) 
	{
		this.graphics = graphics;
		if (level == 1)
			generateBoard(9, 9, 10);
		else if (level == 2)
			generateBoard(16, 16, 40);
		else 
			generateBoard(16, 30, 99);
	}
	
	//pre-condition: width > 0, height > 0, numberOfMines > 0, with user input data
	//post-condition: calls generateBoard with input data
	public Logic(int width, int height, int numberOfMines, Graphics graphics) 
	{
		this.graphics = graphics;
		generateBoard(width, height, numberOfMines);
	}

	//pre-condition: width > 0, height > 0, numberOfMines > 0
	//post-condition: creates a board of specified dimensions 
	private void generateBoard(int width, int height, int numberOfMines) 
	{
		board= new int[width][height];
		for(int r = 0; r < board.length; r++)
			for(int c = 0; c < board[0].length; c++)
				board[r][c] = 0;
		this.numberOfMines = numberOfMines;
		placeMines();
		countMines();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				time++;
				graphics.drawClock();
			}
			
		}, 1000, 1000);
	}
	
	//pre-condition: numberOfMines > 0, board is initialized
	//post-condition: board is populated with randomly placed "mines"
	private void placeMines() {
		for(int i = 0; i < numberOfMines; i++) {

			//Picks intial spot to place mine
			int row = (int) (Math.random()*(board.length));
			int col = (int) (Math.random()*(board[0].length));

			while(board[row][col] != 0) { //Keeps picking spots until the spot is empty
				row = (int) (Math.random()*(board.length));
				col = (int) (Math.random()*(board[0].length));
			}

			board[row][col] = -1;//-1 equals mine
		}
	}
	
	//pre-condition: board needs to be randomly filled with mines
	//post-condition: returns # of mines in the 8 surrounding area
	private void countMines() 
	{
		int sum = 0; 
		for(int row = 0; row < board.length; row++)
		{
			for (int col = 0; col < board[0].length; col++)
			{
				if(board[row][col] >= 0) { //This spot is not a mine
					if (row != 0 && col != 0 && row != board.length-1 && col != board[0].length-1)//Not an end condition
					{
						if (board[row-1][col-1] == -1)
							sum ++;
						if (board[row-1][col] == -1)
							sum ++;
						if (board[row-1][col+1] == -1)
							sum ++;
						if (board[row][col-1] == -1)
							sum ++;
						if (board[row][col+1] == -1)
							sum ++;
						if (board[row+1][col-1] == -1)
							sum ++;
						if (board[row+1][col] == -1)
							sum ++;
						if (board[row+1][col+1] == -1)
							sum ++;
					}
					else //End condition
					{
						if (row != 0)
						{
							if (board[row-1][col] == -1)
								sum ++;
							if (row != board.length-1)
								if (board[row+1][col] == -1)
									sum ++;
							if(col ==0)
							{
								if (board[row-1][col+1] == -1)
									sum ++;
								if (row != board.length-1)
								{
									if (board[row+1][col+1] == -1)
										sum ++;
								}
								if (board[row][col+1] == -1)
									sum ++;
							}
							if (col == board[0].length-1)
							{
								if (board[row][col-1] == -1)
									sum ++;
								if (board[row-1][col-1] == -1)
									sum ++;
								if (row != board.length-1)
								{
									if (board[row+1][col-1] == -1)
										sum ++;
								}
							}	
						}
						if (row == 0) 
						{
							if (board[row+1][col] == -1)
								sum ++;
							if(col != board[0].length-1)
							{
								if (board[row+1][col+1] == -1)
									sum ++;
								if (board[row][col+1] == -1)
									sum ++;
							}
							if (col != 0)
							{
								if (board[row][col-1] == -1)
									sum ++;
								if (board[row+1][col-1] == -1)
									sum ++;
							}
						}
						
					}
					
					board[row][col] = sum;
					sum = 0; 
				}
			}
		}
	}
	
	//pre-condition: board is initialized
	//post-condition: sets flagged position to true
	public void flag(int row, int col, boolean[][] flagged) 
	{
		flagged[row][col] = true;
		numberOfFlags++;
	}
	
	//pre-condition: row and col is true in flagged
	//post-condition: sets the row and col in flagged to false and decrease flag count
	public void removeFlag(int row, int col, boolean[][] flagged)
	{
		flagged[row][col] = false;
		numberOfFlags--;
	}
	
	//pre-condition: revealed needs to be initialized
	//post-condition: returns true if a bomb was clicked on first try
	//					false if no bomb was clicked
	public boolean reveal(int row, int col, boolean[][] revealed, boolean[][] flagged) 
	{
		if(board[row][col] < 0)
			return true;
		else if(board[row][col] == 0 && !revealed[row][col] && !flagged[row][col]) {
			revealed[row][col] = true;
			revealSurrounding(row, col, revealed, flagged);
		}
		
		if(!flagged[row][col])//Only reveal if it's not flagged
			revealed[row][col] = true;
		
		return false;
	}
	
	//pre-condition: revealed needs to be initialized
	//post-condition: calls reveal on 8 surrounding positions
	public void revealSurrounding(int row, int col, boolean[][] revealed, boolean[][] flagged) 
	{
		//TODO: Optimization???
		if(row < revealed.length-1)
			reveal(row+1, col, revealed, flagged);
		if(col < revealed[0].length-1)
			reveal(row, col+1, revealed, flagged);
		if(row < revealed.length-1 && col < revealed[0].length-1)
			reveal(row+1, col+1, revealed, flagged);
		
		if(row > 0) {
			reveal(row-1, col, revealed, flagged);
			
			if(col < revealed[0].length-1)
				reveal(row-1, col+1, revealed, flagged);
		}
		
		if(col > 0) {
			reveal(row, col-1, revealed, flagged);
			
			if(row < revealed.length-1)
				reveal(row+1, col-1, revealed, flagged);
		}
		
		if(row > 0 && col > 0)
			reveal(row-1, col-1, revealed, flagged);
	}
	
	//pre-condition: timer has been started
	//post-condition: timer is no longer scheduled
	public void stopTimer() {
		timer.cancel();
	}
	
	public int[][] getBoard(){
		return board;
	}
	
	public int minesLeft() {
		return numberOfMines-numberOfFlags;
	}
	
	public int getTime() {
		return time;
	}
}
