package prkr.war;

import exceptions.DuplicatePlayerException;
import exceptions.TooManyPlayersException;

public class App {
    public static void main( String[] args ) {
    	WarGame war = new WarGame();
    	
    	try {
			war.addPlayer("Parker");
			war.addPlayer("John");
			war.addPlayer("Smith");
		} catch (TooManyPlayersException e) {
			System.out.println("You can't define more than 52 players");
    	} catch (DuplicatePlayerException e) {
    		System.out.println("You've submitted a duplicate player, please give a different name");
    	}
    	
    	Deck deck = new Deck();
    	
    	war.distributeCards(deck);
    	
    	
    	
        System.out.println( "Hello World!" );
    }
}
