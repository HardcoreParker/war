package prkr.war.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import prkr.war.framework.BattleEntry;
import prkr.war.framework.BattleResolution;
import prkr.war.framework.Card;
import prkr.war.framework.Player;
import prkr.war.framework.Card.Rank;

public class PrintingUtil {

	public void announceWar(HashMap<Rank, Set<BattleEntry>> mapOfPairs) {
		System.out.println();
		System.out.println("WAR! The following cards initiated a war: ");
		for(Rank rank : mapOfPairs.keySet()) {
			for(BattleEntry entry : mapOfPairs.get(rank)) {
				System.out.println(entry.getCard()+" from: "+entry.getPlayer());
			}
		}
		System.out.println();
	}
	
	public void announceResolvedBattle(BattleResolution resolution) {
		System.out.println("The winner was resolved as: "+resolution.getWinner()+" who's pot contained "+resolution.getPot().size()+" cards. The high card was a "+resolution.getWinningCard());
		System.out.println("Pot:");
		for(Card card : resolution.getPot()) {
			System.out.println(card);
		}
		System.out.println();
	}

	public void announcePlayerEliminated(Player player) {
		System.out.println();
		System.out.println(player + " has been eliminated.");
	}
	
	public void announceAllPlayersAreEliminated() {
		System.console().flush();
		System.out.println("Well, this is unlikely to happen unless you're exceeding 5 players at once, but every player was elminated at once. Go outside or something");
	}

	public void announceBeginningOfGame() {
		System.out.println("Welcome to War!");
    	System.out.println("Please enter a name for each player involved in this game followed by <Enter>.");
    	System.out.println("Indicate that you are ready by submitting 'done'");
	}

	public void announcePlayerAdded(Player player) {
		System.out.println();
		System.out.println("Added player "+player);
		System.out.println("You can add another player, or start the game with 'done'");
	}
	
	public void announceDuplicatePlayer() {
		System.out.println();
		System.out.println("We already have a player with that name, can you think of another one?");
	}

	public void announceTooManyPlayers() {
		System.out.println();
		System.out.println("Any more players won't be very fun, let's start.");
	}
	
	public void announceStart() {
		System.out.println();
    	System.out.println("Here we GO!");
	}

	public void announcePlayerDeckSizes(List<Player> players) {
		System.out.println();
		for(Player player : players) {
			System.out.println(player+"'s deck has "+player.getDeck().size()+" remaining.");
		}		
	}

	public void announceGameOver(List<Player> players) {
		System.out.println();
		System.out.println("The game has finished! Winner is "+players);
	}
	
}
