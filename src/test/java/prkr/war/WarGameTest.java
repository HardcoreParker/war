package prkr.war;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import prkr.war.Card.Rank;
import prkr.war.Card.Suit;
import prkr.war.exceptions.DuplicatePlayerException;
import prkr.war.exceptions.GameOverException;
import prkr.war.exceptions.TooManyPlayersException;
import util.PrintingUtil;

public class WarGameTest {

	WarGame warGame;
	Deck deck;
	Card aceOfSpades = new Card(Rank.ACE, Suit.SPADES);
	Card twoOfHearts = new Card(Rank.TWO, Suit.HEARTS);

	
	@Before
	public void setUpBefore() {
		warGame = new WarGame(new PrintingUtil());
		
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
	
	/**
	 * distrubuteCards()
	 */
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
	 * getPlayers()
	 */
	@Test
	public void getPlayers_Test() {
		setUpWithNPlayersExceptionSafe(1);
		
		ArrayList<Player> players = warGame.getPlayers();
		
		assertNotNull(players);
		assertFalse(players.isEmpty());
	}
	
	@Test
	public void getPlayers_no_players_test() {
		ArrayList<Player> players = warGame.getPlayers();
		
		assertNotNull(players);
		assertTrue(players.isEmpty());
	}
	
	/**
	 * getPot()
	 * addToPot()
	 */
	@Test
	public void getPot_addToPot_test(){
		Card card = new Card(Rank.ACE, Suit.SPADES);
		warGame.addToPot(card);
		
		HashSet<Card> pot = warGame.getPot();
		assertNotNull(pot);
		assertFalse(pot.isEmpty());
		assertTrue(pot.contains(card));
		assertEquals(1, pot.size());
	}
	
	@Test
	public void getPot_starts_empty_test() {
		HashSet<Card> pot = warGame.getPot();
		assertNotNull(pot);
		assertTrue(pot.isEmpty());
	}
	
	/**
	 * refreshPot()
	 */
	@Test
	public void refreshPot_test() {
		assertTrue(warGame.getPot().isEmpty());
		warGame.addToPot(aceOfSpades);
		warGame.addToPot(twoOfHearts);
		assertFalse(warGame.getPot().isEmpty());
		warGame.refreshPot();
		assertTrue(warGame.getPot().isEmpty());
	}
	
	/**
	 * addCardsToPot()
	 */
	@Test
	public void addCardsToPot_test() {
		HashSet<BattleEntry> battleEntries = new HashSet<BattleEntry>();
		battleEntries.add(new BattleEntry(aceOfSpades, new Player("P1")));
		battleEntries.add(new BattleEntry(twoOfHearts, new Player("P2")));
		
		warGame.addCardsToPot(battleEntries);
		
		HashSet<Card> pot = warGame.getPot();
		
		assertEquals(2, pot.size());
		assert(pot.contains(aceOfSpades));
		assert(pot.contains(twoOfHearts));
	}
	
	/**
	 * awardWinner()
	 */
	@Test
	public void awardWinner_test() {
		setUpWithNPlayersExceptionSafe(2);
		Player P1 = warGame.getPlayers().get(0);
		Player P2 = warGame.getPlayers().get(1);
		
		warGame.addToPot(aceOfSpades);
		warGame.addToPot(twoOfHearts);
		
		BattleResolution resolution = new BattleResolution(P1, warGame.getPot(), aceOfSpades);
		
		warGame.awardWinner(resolution);
		
		assertEquals(2, P1.getDeck().size());
		assertTrue(P1.getDeck().contains(aceOfSpades));
		assertTrue(P1.getDeck().contains(twoOfHearts));
		assertEquals(0, P2.getDeck().size());
	}
	
	/**
	 * removeIneligiblePlayers()
	 */
	@Test
	public void removeIneligiblePlayers_test() {
		setUpWithNPlayersExceptionSafe(2);
		
		Player P1 = warGame.getPlayers().get(0);
		Player P2 = warGame.getPlayers().get(1);
		
		P1.dealCard(aceOfSpades);
		P1.dealCard(twoOfHearts);
		
		assertEquals(2, P1.getDeck().size());
		assertEquals(0, P2.getDeck().size());
		assertEquals(2, warGame.getPlayers().size());
		assertTrue(warGame.getPlayers().contains(P1));
		assertTrue(warGame.getPlayers().contains(P2));
		
		try {
			warGame.removeIneligiblePlayers();
		} catch (GameOverException e) {
			fail();
		}
		
		assertTrue(warGame.getPlayers().contains(P1));
		assertFalse(warGame.getPlayers().contains(P2));
	}
	
	
	/**
	 * gatherMatchedEntriesAndRanks()
	 */
	@Test
	public void gatherMatchedEntriesAndRanks_test() {
		HashSet<BattleEntry> battleEntries = new HashSet<BattleEntry>();
		
		BattleEntry entry1 = new BattleEntry(new Card(Rank.ACE, Suit.SPADES), new Player("Player 1"));
		BattleEntry entry2 = new BattleEntry(new Card(Rank.ACE, Suit.HEARTS), new Player("Player 2"));
		
		battleEntries.add(entry1);
		battleEntries.add(entry2);
		
		HashMap<Rank, HashSet<BattleEntry>> matches = warGame.identifyPairs(battleEntries);
		
		assertEquals(1, matches.size());
		assertTrue(matches.containsKey(Rank.ACE));
		assertTrue(matches.get(Rank.ACE).size() == 2);
		assertTrue(matches.get(Rank.ACE).contains(entry1));
		assertTrue(matches.get(Rank.ACE).contains(entry2));
	}
	
	@Test
	public void gatherMatchedEntriesAndRanks_many_matches_test() {
		HashSet<BattleEntry> battlesProposals = new HashSet<BattleEntry>();
		
		BattleEntry entry1 = new BattleEntry(new Card(Rank.TWO, Suit.SPADES), new Player("Player 1"));
		BattleEntry entry2 = new BattleEntry(new Card(Rank.TWO, Suit.HEARTS), new Player("Player 2"));
		
		BattleEntry entry3 = new BattleEntry(new Card(Rank.THREE, Suit.SPADES), new Player("Player 3"));
		BattleEntry entry4 = new BattleEntry(new Card(Rank.THREE, Suit.HEARTS), new Player("Player 4"));

		
		battlesProposals.add(entry1);
		battlesProposals.add(entry2);
		
		battlesProposals.add(entry3);
		battlesProposals.add(entry4);
		
		HashMap<Rank, HashSet<BattleEntry>> matches = warGame.identifyPairs(battlesProposals);
		
		assertEquals(2, matches.size());
		assertTrue(matches.get(Rank.TWO).contains(entry1));
		assertTrue(matches.get(Rank.TWO).contains(entry2));
		
		assertTrue(matches.get(Rank.THREE).contains(entry3));
		assertTrue(matches.get(Rank.THREE).contains(entry4));

	}
	
	// TODO - rename to identifyPairs
	@Test
	public void gatherMatchedEntriesAndRanks_no_matches_test() {
		HashSet<BattleEntry> battlesProposals = new HashSet<BattleEntry>();
		
		BattleEntry entry1 = new BattleEntry(new Card(Rank.TWO, Suit.SPADES), new Player("Player 1"));
		BattleEntry entry2 = new BattleEntry(new Card(Rank.THREE, Suit.HEARTS), new Player("Player 2"));
		
		BattleEntry entry3 = new BattleEntry(new Card(Rank.FOUR, Suit.SPADES), new Player("Player 3"));
		BattleEntry entry4 = new BattleEntry(new Card(Rank.FIVE, Suit.HEARTS), new Player("Player 4"));
		
		battlesProposals.add(entry1);
		battlesProposals.add(entry2);
		
		battlesProposals.add(entry3);
		battlesProposals.add(entry4);
		
		HashMap<Rank, HashSet<BattleEntry>> matches = warGame.identifyPairs(battlesProposals);
		
		assertEquals(0, matches.size());
	}
	
	/**
	 * initiateBattle() test
	 */
	@Test
	public void initiateBattle_3_players_test() {
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
		
		
		HashSet<BattleEntry> entries = new HashSet<BattleEntry>();
		for(Player player : warGame.getPlayers()) {
			entries.add(new BattleEntry(player.getDeck().removeFirst(), player));
		}
		
		BattleResolution resolution = warGame.initiateBattle(entries);
		
		assertEquals(warGame.getPlayers().get(2), resolution.getWinner());
		assertEquals(9, warGame.getPot().size());
		assertEquals(winningCard, resolution.getWinningCard());
	}
	
	/**
	 * compareAllCardsInRound() test
	 */
	@Test
	public void initiateBattle_3_players_no_wars_test() {
		setUpWithNPlayersExceptionSafe(3);
		
		Player P1 = warGame.getPlayers().get(0);
		P1.dealCard(new Card(Rank.TWO, Suit.SPADES));
		P1.dealCard(new Card(Rank.SIX, Suit.SPADES));
		P1.dealCard(new Card(Rank.KING, Suit.SPADES));
		
		Player P2 = warGame.getPlayers().get(1);
		P2.dealCard(new Card(Rank.THREE, Suit.HEARTS));
		P2.dealCard(new Card(Rank.FIVE, Suit.HEARTS));
		P2.dealCard(new Card(Rank.QUEEN, Suit.HEARTS));

		Player P3 = warGame.getPlayers().get(2);
		P3.dealCard(new Card(Rank.SEVEN, Suit.CLUBS));
		P3.dealCard(new Card(Rank.SEVEN, Suit.CLUBS));
		P3.dealCard(new Card(Rank.JACK, Suit.CLUBS));
		

		HashSet<BattleEntry> entries = warGame.gatherEntries();
		BattleResolution resolution = warGame.initiateBattle(entries);
		assertTrue(resolution.getWinner().equals(P1));
		assertEquals(3, resolution.getPot().size());
		assertEquals(new Card(Rank.KING, Suit.SPADES), resolution.getWinningCard());
	}
	
	/**
	 * gatherEntries()
	 */
	@Test
	public void gatherEntries_test() {
		setUpWithNPlayersExceptionSafe(2);
		
		Player P1 = warGame.getPlayers().get(0);
		Player P2 = warGame.getPlayers().get(1);
		
		P1.dealCard(aceOfSpades);
		P2.dealCard(twoOfHearts);
		
		HashSet<BattleEntry> entries = warGame.gatherEntries();
		
		assertEquals(2, entries.size());
		assertEquals(0, P1.getDeck().size());
		assertEquals(0, P2.getDeck().size());
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
				warGame.addPlayer("Player "+String.valueOf(i+1));
			} catch (DuplicatePlayerException e) {
				fail();
			} catch (TooManyPlayersException e) {
				fail();
			}
		}
	}

}
