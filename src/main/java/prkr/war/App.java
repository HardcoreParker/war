package prkr.war;

import java.util.Scanner;

import prkr.war.util.PrintingUtil;

public class App {
	
	private static Game game;
	
    public static void main( String[] args ) {
    	game = new Game(new WarGame(new PrintingUtil()), new Scanner(System.in), new PrintingUtil());
    	game.startGame();
    }
}
