package prkr.war;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import exceptions.DuplicatePlayerException;
import exceptions.TooManyPlayersException;

public class WarGameTest {

	WarGame warGame;
	Deck deck;
	
	@Before
	public void setUpBefore() {
		warGame = new WarGame();
		
		deck = new Deck();
		deck.shuffle();
	}
	
	@Test
	public void addPlayer_happypath_test() {
		try {
			warGame.addPlayer("One");
			warGame.addPlayer("Two");
			warGame.addPlayer("Three");
		} catch (DuplicatePlayerException e) {
			fail();
		} catch (TooManyPlayersException e) {
			fail();
		}
		
		ArrayList<Player> players = warGame.getPlayers();
		
		assert(players.size() == 3);
		assert(players.contains(new Player("One")));
		assert(players.contains(new Player("Two")));
		assert(players.contains(new Player("Three")));
	}
	
	@Test(expected = DuplicatePlayerException.class)
	public void addPlayer_force_DuplicatePlayerException_test() throws DuplicatePlayerException, TooManyPlayersException {
		warGame.addPlayer("One");
		warGame.addPlayer("One");
	}
	
	@Test(expected = TooManyPlayersException.class)
	public void addPlayer_force_TooManyPlayersException_test() throws DuplicatePlayerException, TooManyPlayersException {
		setUpWithNPlayers(53);
	}
	
	@Test
	public void disributeCards_test() {
		setUpWithNPlayersExceptionSafe(3);
		
		warGame.distributeCards(deck);
		
		ArrayList<Player> players = warGame.getPlayers();
		
		assert(players.get(0).getDeck().size() == 17);
		assert(players.get(1).getDeck().size() == 17);
		assert(players.get(2).getDeck().size() == 17);
	}
	
	@Test
	public void distributeCards_no_player_has_unequal_cards_test() {
		// Ensure no player has the extra card and that it remains in the intiial deck
		setUpWithNPlayersExceptionSafe(51);
		
		warGame.distributeCards(deck);

		int globalDeckSize = warGame.getPlayers().get(0).getDeck().size();
		for(Player player : warGame.getPlayers()) {
			int playerDeckSize = player.getDeck().size();
			assertEquals(playerDeckSize, globalDeckSize);
			assertEquals(playerDeckSize, 1);
		}
		
		assertEquals(deck.getCards().size(), 1);
	}

	@Test
	public void distributeCards_52_players_test() {
		setUpWithNPlayersExceptionSafe(52);
		
		warGame.distributeCards(deck);
		
		ArrayList<Player> players = warGame.getPlayers();
		
		assert(players.get(0).getDeck().size() == 1);
	}
	
	@Test
	public void removePlayer_test() {
		setUpWithNPlayersExceptionSafe(3);
		
		assertTrue(warGame.getPlayers().contains(new Player("Player 0")));
		
		warGame.removePlayer(new Player("Player 0"));
		
		assert(warGame.getPlayers().size() == 2);
		assertFalse(warGame.getPlayers().contains(new Player("Player 0")));
	}
	
	private void setUpWithNPlayers(int players) throws TooManyPlayersException {
		for (int i = 0; i < players; i++) {
			try {
				warGame.addPlayer("Player "+String.valueOf(i));
			} catch (DuplicatePlayerException e) {
				fail();
			}
		}
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
