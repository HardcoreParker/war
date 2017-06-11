package prkr.war;

import prkr.war.exceptions.DuplicatePlayerException;
import prkr.war.exceptions.TooManyPlayersException;

public class App {
    public static void main( String[] args ) {
    	WarGame warGame = new WarGame();
    	System.out.println("Welcome to War!");
    	System.out.println("Please enter a name for each player involved in this game (up to 52) followed by <Enter>.");
    	System.out.println("Indicate that you are ready by submitting 'next'");
    	
    	boolean doneMakingPlayers = false;
    	
    	while(!doneMakingPlayers) {
			String input = System.console().readLine();
			if(input.equals("next")) {
				doneMakingPlayers = true;
			} else if(input instanceof String){
				try {
					warGame.addPlayer(input);
					System.out.println("Added player "+input);
					System.out.println("You can add another player, or start the game with 'next'");
				} catch (DuplicatePlayerException e) {
					System.out.println("We already have a player with that name, can you think of another one?");
				} catch (TooManyPlayersException e) {
					System.out.println("We already have 52 players. Starting the game.");
					doneMakingPlayers = true;
					break;
				}
			} else {
				System.out.println("I don't think that's a valid input. Try again (strings only please)");
			}
    	}
    	
    	Deck deck = new Deck();
    	
    	warGame.distributeCards(deck);
    	
    	boolean gameShouldContinue = true;
    	System.out.println("Here we GO!");
    	while(gameShouldContinue) {
    		System.out.println("Type n to continue to the next round.");
			String input = System.console().readLine();
			if(input.equals("next") || input.equals("n")) {
				BattleResolution resolution = warGame.beginBattle();
				System.out.println("The battle was resolved with "+resolution.getWinner()+" as the winner and a pot of "+resolution.getPot().size()+". The high card was a "+resolution.getWinningCard());
				for(Player player : warGame.getPlayers()) {
					System.out.println("PLAYER: "+player+"'s deck has "+player.getDeck().size()+" remaining.");
				}
				if(warGame.getPlayers().size() <= 1) {
					System.out.println("The game has finished! Winner is "+warGame.getPlayers());
					gameShouldContinue = false;
				}
			} else {
				System.out.println("Please just say 'n' for now");
				break;
			}
    	}
    }
}
