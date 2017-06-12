package prkr.war;

import java.util.Scanner;

public class App {
	
	private static Game game;
	
    public static void main( String[] args ) {
    	game = new Game(new WarGame(), new Scanner(System.in));
    	game.startGame();
    }
}
