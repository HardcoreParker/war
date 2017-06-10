package prkr.war;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import exceptions.DuplicatePlayerException;
import exceptions.TooManyPlayersException;

public class WarGame_IT {

	WarGame warGame;
	Deck deck;
	
	@Before
	public void setUpBefore() {
		warGame = new WarGame();
		
		deck = new Deck();
		deck.shuffle();
	}
	
	@Test
	public void war_52_players_test() {
		// If there are miltiple wars, they do not resolve independently. There will be 1 winner per round.
		setUpWithNPlayersExceptionSafe(52);
		Deck unshuffledDeck = new Deck(); // To assist with assertions
		warGame.distributeCards(unshuffledDeck);
		
		ArrayList<BattleEntry> roundProposals = new ArrayList<BattleEntry>();
		
		while(warGame.getPlayers().size() > 1) {
			for(Player player : warGame.getPlayers()) {
				BattleEntry proposal = new BattleEntry(player.getDeck().removeFirst(), player);
				roundProposals.add(proposal);
			}
			BattleResolution resolution = warGame.compareAllCardsInRound(roundProposals);
			
			for(Card card : resolution.getPot()) {
				resolution.getWinner().getDeck().addLast(card);
			}
			
			List<Player> playersToRemove = new ArrayList<Player>();
			for(Player player : warGame.getPlayers()) {
				if (player.getDeck().isEmpty()) {
					playersToRemove.add(player);
				}
			}
			warGame.getPlayers().removeAll(playersToRemove);
		}
		
		assertEquals(warGame.getPlayers().size(), 1);
		assertEquals(warGame.getPlayers().get(0), new Player("Player 51")); // Only possible because deck is unshuffled
	}
	
	private void setUpWithNPlayersExceptionSafe(int players) {
		for (int i = 0; i < players; i++) {
			try {
				warGame.addPlayer("Player "+String.valueOf(i));
			} catch (DuplicatePlayerException e) {
				fail();
			} catch (TooManyPlayersException e) {
				fail();
			}
		}
	}

}
