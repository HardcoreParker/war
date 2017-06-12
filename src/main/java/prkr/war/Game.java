package prkr.war;

import java.util.HashSet;
import java.util.Scanner;

import prkr.war.exceptions.DuplicatePlayerException;
import prkr.war.exceptions.TooManyPlayersException;

public class Game {

	private WarGame warGame;
	private Scanner scanner;
	
	public Game(WarGame warGame, Scanner scanner) {
		this.warGame = warGame;
		this.scanner = scanner;
	}
	
	public void startGame() {
    	System.out.println("Welcome to War!");
    	System.out.println("Please enter a name for each player involved in this game (up to 52) followed by <Enter>.");
    	System.out.println("Indicate that you are ready by submitting 'next'");
    	
    	boolean doneMakingPlayers = false;
    	
    	while(!doneMakingPlayers) {
			String input = scanner.nextLine();
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
    	deck.shuffle();
    	warGame.distributeCards(deck);
    	
    	boolean gameShouldContinue = true;
    	System.out.println("Here we GO!");
    	while(gameShouldContinue) {
    		scanner.nextLine();
			
			BattleResolution resolution = beginBattle(warGame);
			
			System.out.println("The battle was resolved with "+resolution.getWinner()+" as the winner and a pot of "+resolution.getPot().size()+". The high card was a "+resolution.getWinningCard());
			for(Player player : warGame.getPlayers()) {
				System.out.println(player+"'s deck has "+player.getDeck().size()+" remaining.");
			}
			if(warGame.getPlayers().size() <= 1) {
				System.out.println("The game has finished! Winner is "+warGame.getPlayers());
				gameShouldContinue = false;
			}
    	}
	}
	
	 private static BattleResolution beginBattle(WarGame warGame) {
	    	HashSet<BattleEntry> entries = warGame.gatherEntries();
	    	
	    	BattleResolution resolution = warGame.initiateBattle(entries);
	    	warGame.awardWinner(resolution);
	    	warGame.refreshPot();
	    	warGame.removeIneligiblePlayers();
	    	
	    	return resolution;
	    }
	
}
