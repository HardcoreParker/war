package prkr.war;

import java.util.HashSet;
import java.util.Scanner;

import prkr.war.exceptions.DuplicatePlayerException;
import prkr.war.exceptions.GameOverException;
import prkr.war.exceptions.TooManyPlayersException;
import util.PrintingUtil;

public class Game {

	private WarGame warGame;
	private Scanner scanner;
	private static PrintingUtil printingUtil;
	
	public Game(WarGame warGame, Scanner scanner, PrintingUtil printingUtil) {
		this.warGame = warGame;
		this.scanner = scanner;
		this.printingUtil = printingUtil;
	}
	
	public void startGame() {
    	printingUtil.announceBeginningOfGame();
    	
    	boolean doneMakingPlayers = false;
    	while(!doneMakingPlayers) {
			String input = scanner.nextLine();
			if(input.equals("done")) {
				doneMakingPlayers = true;
			} else if(input instanceof String){
				try {
					warGame.addPlayer(input);
				} catch (DuplicatePlayerException e) {
					printingUtil.announceDuplicatePlayer();
				} catch (TooManyPlayersException e) {
					printingUtil.announceTooManyPlayers();
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
    	
    	printingUtil.announceStart();
    	
    	boolean gameShouldContinue = true;
    	while(gameShouldContinue) {
    		scanner.nextLine();
			
			beginBattle(warGame);
			
			printingUtil.announcePlayerDeckSizes(warGame.getPlayers());
			
			if(warGame.getPlayers().size() <= 1) {
				printingUtil.announceGameOver(warGame.getPlayers());
				gameShouldContinue = false;
			}
    	}
	}
	
	 private static BattleResolution beginBattle(WarGame warGame) {
	    	HashSet<BattleEntry> entries = warGame.gatherEntries();
	    	
	    	BattleResolution resolution = warGame.initiateBattle(entries);
	    	warGame.awardWinner(resolution);
	    	warGame.refreshPot();
	    	
	    	try {
				warGame.removeIneligiblePlayers();
			} catch (GameOverException e) {
				printingUtil.announceAllPlayersAreEliminated();
				
			}
	    	
	    	return resolution;
	    }
	
}
