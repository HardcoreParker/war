package prkr.war;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import exceptions.DuplicatePlayerException;
import exceptions.TooManyPlayersException;
import prkr.war.Card.Rank;
import prkr.war.Card.Suit;

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
			assertEquals(1, playerDeckSize);
		}
		
		assertEquals(1, deck.getCards().size());
	}

	@Test
	public void distributeCards_52_players_test() {
		setUpWithNPlayersExceptionSafe(52);
		
		warGame.distributeCards(deck);
		
		ArrayList<Player> players = warGame.getPlayers();
		
		assert(players.get(0).getDeck().size() == 1);
	}
	
	/**
	 * removePlayer()
	 */
	@Test
	public void removePlayer_test() {
		setUpWithNPlayersExceptionSafe(3);
		
		assertTrue(warGame.getPlayers().contains(new Player("Player 0")));
		
		warGame.removePlayer(new Player("Player 0"));
		
		assert(warGame.getPlayers().size() == 2);
		assertFalse(warGame.getPlayers().contains(new Player("Player 0")));
	}
	
	/**
	 * gatherMatchedEntriesAndRanks()
	 */
	@Test
	public void gatherMatchedEntriesAndRanks_test() {
		ArrayList<BattleEntry> battleEntries = new ArrayList<BattleEntry>();
		
		BattleEntry entry1 = new BattleEntry(new Card(Rank.ACE, Suit.SPADES), new Player("Player 1"));
		BattleEntry entry2 = new BattleEntry(new Card(Rank.ACE, Suit.HEARTS), new Player("Player 2"));
		
		battleEntries.add(entry1);
		battleEntries.add(entry2);
		
		HashMap<Rank, Set<BattleEntry>> matches = warGame.gatherMatchedRanksAndEntries(battleEntries);
		
		assertEquals(1, matches.size());
		assertTrue(matches.containsKey(Rank.ACE));
		assertTrue(matches.get(Rank.ACE).size() == 2);
		assertTrue(matches.get(Rank.ACE).contains(entry1));
		assertTrue(matches.get(Rank.ACE).contains(entry2));
	}
	
	@Test
	public void gatherMatchedEntriesAndRanks_many_matches_test() {
		ArrayList<BattleEntry> battlesProposals = new ArrayList<BattleEntry>();
		
		BattleEntry entry1 = new BattleEntry(new Card(Rank.TWO, Suit.SPADES), new Player("Player 1"));
		BattleEntry entry2 = new BattleEntry(new Card(Rank.TWO, Suit.HEARTS), new Player("Player 2"));
		
		BattleEntry entry3 = new BattleEntry(new Card(Rank.THREE, Suit.SPADES), new Player("Player 3"));
		BattleEntry entry4 = new BattleEntry(new Card(Rank.THREE, Suit.HEARTS), new Player("Player 4"));

		
		battlesProposals.add(entry1);
		battlesProposals.add(entry2);
		
		battlesProposals.add(entry3);
		battlesProposals.add(entry4);
		
		HashMap<Rank, Set<BattleEntry>> matches = warGame.gatherMatchedRanksAndEntries(battlesProposals);
		
		assertEquals(2, matches.size());
		assertTrue(matches.get(Rank.TWO).contains(entry1));
		assertTrue(matches.get(Rank.TWO).contains(entry2));
		
		assertTrue(matches.get(Rank.THREE).contains(entry3));
		assertTrue(matches.get(Rank.THREE).contains(entry4));

	}
	
	@Test
	public void gatherMatchedEntriesAndRanks_no_matches_test() {
		ArrayList<BattleEntry> battlesProposals = new ArrayList<BattleEntry>();
		
		BattleEntry entry1 = new BattleEntry(new Card(Rank.TWO, Suit.SPADES), new Player("Player 1"));
		BattleEntry entry2 = new BattleEntry(new Card(Rank.THREE, Suit.HEARTS), new Player("Player 2"));
		
		BattleEntry entry3 = new BattleEntry(new Card(Rank.FOUR, Suit.SPADES), new Player("Player 3"));
		BattleEntry entry4 = new BattleEntry(new Card(Rank.FIVE, Suit.HEARTS), new Player("Player 4"));
		
		battlesProposals.add(entry1);
		battlesProposals.add(entry2);
		
		battlesProposals.add(entry3);
		battlesProposals.add(entry4);
		
		HashMap<Rank, Set<BattleEntry>> matches = warGame.gatherMatchedRanksAndEntries(battlesProposals);
		
		assertEquals(0, matches.size());
	}
	
	/**
	 * initiateSuitResolution() test
	 */
	@Test
	public void initiateSuitResolution_test() {
		Set<BattleEntry> entriesInAParticularWar = new HashSet<BattleEntry>();
		
		Player player1 = new Player("Player 1");
		Card twoOfSpades = new Card(Rank.TWO, Suit.SPADES);
		Player player2 = new Player("Player 2");
		Card twoOfHearts = new Card(Rank.TWO, Suit.HEARTS);
		
		BattleEntry entry1 = new BattleEntry(twoOfSpades, player1);
		BattleEntry entry2 = new BattleEntry(twoOfHearts, player2);
		
		
		entriesInAParticularWar.add(entry1);
		entriesInAParticularWar.add(entry2);
		
		BattleResolution resolution = warGame.initiateSuitResolution(entriesInAParticularWar);
		
		assertEquals(player1, resolution.getWinner());
		assertEquals(twoOfSpades, resolution.getWinningCard());
		assertEquals(2, resolution.getPot().size());
		assertTrue(resolution.getPot().contains(twoOfSpades));
		assertTrue(resolution.getPot().contains(twoOfHearts));
	}
	
	/**
	 * compareAllCardsInRound() test
	 */
	@Test
	public void compareAllCardsInRound_3_players_test() {
		setUpWithNPlayersExceptionSafe(3);
		
		Card winningCard = new Card(Rank.SEVEN, Suit.CLUBS);
		
		warGame.getPlayers().get(0).dealCard(new Card(Rank.TWO, Suit.SPADES));
		warGame.getPlayers().get(0).dealCard(new Card(Rank.SIX, Suit.SPADES));
		warGame.getPlayers().get(0).dealCard(new Card(Rank.ACE, Suit.SPADES));

		
		warGame.getPlayers().get(1).dealCard(new Card(Rank.THREE, Suit.HEARTS));
		warGame.getPlayers().get(1).dealCard(new Card(Rank.SIX, Suit.HEARTS));
		warGame.getPlayers().get(1).dealCard(new Card(Rank.ACE, Suit.HEARTS));

		warGame.getPlayers().get(2).dealCard(winningCard);
		warGame.getPlayers().get(2).dealCard(new Card(Rank.SIX, Suit.CLUBS));
		warGame.getPlayers().get(2).dealCard(new Card(Rank.ACE, Suit.CLUBS));
		
		
		ArrayList<BattleEntry> entries = new ArrayList<BattleEntry>();
		for(Player player : warGame.getPlayers()) {
			entries.add(new BattleEntry(player.getDeck().removeFirst(), player));
		}
		
		BattleResolution resolution = warGame.compareAllCardsInRound(entries);
		
		assertEquals(warGame.getPlayers().get(2), resolution.getWinner());
		assertEquals(9, resolution.getPot().size());
		assertEquals(winningCard, resolution.getWinningCard());
	}
	
	/**
	 * This test will consist of 4 players with 3 cards each.
	 * The first battle will be a 4-way war
	 * The second battle will be two 2-way wars
	 * The third battle will resolve with player 0 emerging as the ultimate winner, despite player 2 winning their war
	 */
	@Test
	public void war_4_players_two_wars_test() {
		setUpWithNPlayersExceptionSafe(4);
		
		Card winningCard = new Card(Rank.KING, Suit.SPADES);
		
		warGame.getPlayers().get(0).dealCard(new Card(Rank.KING, Suit.SPADES)); // Winning card
		warGame.getPlayers().get(0).dealCard(new Card(Rank.THREE, Suit.SPADES));
		warGame.getPlayers().get(0).dealCard(new Card(Rank.ACE, Suit.SPADES));

		warGame.getPlayers().get(1).dealCard(new Card(Rank.FOUR, Suit.HEARTS));
		warGame.getPlayers().get(1).dealCard(new Card(Rank.THREE, Suit.HEARTS));
		warGame.getPlayers().get(1).dealCard(new Card(Rank.ACE, Suit.HEARTS));

		
		warGame.getPlayers().get(2).dealCard(new Card(Rank.FIVE, Suit.CLUBS));
		warGame.getPlayers().get(2).dealCard(new Card(Rank.TWO, Suit.CLUBS));
		warGame.getPlayers().get(2).dealCard(new Card(Rank.ACE, Suit.CLUBS));
		
		warGame.getPlayers().get(3).dealCard(new Card(Rank.FOUR, Suit.DIAMONDS));
		warGame.getPlayers().get(3).dealCard(new Card(Rank.TWO, Suit.DIAMONDS));
		warGame.getPlayers().get(3).dealCard(new Card(Rank.ACE, Suit.DIAMONDS));
		
		ArrayList<BattleEntry> entries = new ArrayList<BattleEntry>();
		for(Player player : warGame.getPlayers()) {
			entries.add(new BattleEntry(player.getDeck().removeFirst(), player));
		}
		
		BattleResolution resolution = warGame.compareAllCardsInRound(entries);
		
		assertEquals(warGame.getPlayers().get(0), resolution.getWinner());
		assertEquals(12, resolution.getPot().size());
		assertEquals(winningCard, resolution.getWinningCard());
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
