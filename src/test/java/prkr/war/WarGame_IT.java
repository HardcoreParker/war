package prkr.war;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import prkr.war.Card.Suit;
import prkr.war.Card.Rank;
import prkr.war.exceptions.DuplicatePlayerException;
import prkr.war.exceptions.TooManyPlayersException;

public class WarGame_IT {

	WarGame warGame;
	Deck deck;
	
	@Before
	public void setUpBefore() {
		warGame = new WarGame();
		
		deck = new Deck();
	}
	
//	@Test
//	public void war_52_players_test() {
//		// If there are miltiple wars, they do not resolve independently. There will be 1 winner per round.
//		setUpWithNPlayersExceptionSafe(52);
//		Deck unshuffledDeck = new Deck(); // To assist with assertions
//		warGame.distributeCards(unshuffledDeck);
//		
//		ArrayList<BattleEntry> roundProposals = new ArrayList<BattleEntry>();
//		
//		while(warGame.getPlayers().size() > 1) {
//			for(Player player : warGame.getPlayers()) {
//				BattleEntry proposal = new BattleEntry(player.getDeck().removeFirst(), player);
//				roundProposals.add(proposal);
//			}
//			BattleResolution resolution = warGame.initiateBattle(roundProposals);
//			
//			for(Card card : resolution.getPot()) {
//				resolution.getWinner().getDeck().addLast(card);
//			}
//			
//			List<Player> playersToRemove = new ArrayList<Player>();
//			for(Player player : warGame.getPlayers()) {
//				if (player.getDeck().isEmpty()) {
//					playersToRemove.add(player);
//				}
//			}
//			warGame.getPlayers().removeAll(playersToRemove);
//		}
//		
//		assertEquals(warGame.getPlayers().size(), 1);
//		assertEquals(warGame.getPlayers().get(0), new Player("Player 51")); // Only possible because deck is unshuffled
//	}
	
	/**
	 * Round 1:
	 * Player 1 and 2 get into a war and resolve it in 1 war (P1 is winner)
	 * Player 3 and 4 do not get into a war (P3 is winner)
	 * Result: 4 cards are awarded to P1, 2 cards are awarded to P3
	 * 
	 * Round 2:
	 * Player 1 and 2 do not get into a war
	 * Player 3 and 4 get into a war and resolve it in 2 wars (P4 is winner)
	 */
	@Test
	public void war_4_players_test() {
		setUpWithNPlayersExceptionSafe(4);
		
		Player player1 = warGame.getPlayers().get(0);
		Player player2 = warGame.getPlayers().get(1);
		Player player3 = warGame.getPlayers().get(2);
		Player player4 = warGame.getPlayers().get(3);
		
		Card aceOfSpades = new Card(Rank.ACE, Suit.SPADES);
		Card kingOfHearts = new Card(Rank.KING, Suit.HEARTS);
		Card twoOfDiamonds = new Card(Rank.TWO, Suit.DIAMONDS);
		Card fourOfClubs = new Card(Rank.FOUR, Suit.CLUBS);
		
		player1.dealCard(fourOfClubs);
		player1.dealCard(twoOfDiamonds);
		player1.dealCard(kingOfHearts);
		player1.dealCard(aceOfSpades);
		
		Card aceOfHearts = new Card(Rank.ACE, Suit.HEARTS);
		Card twoOfHearts = new Card(Rank.TWO, Suit.HEARTS);
		Card threeOfDiamonds = new Card(Rank.THREE, Suit.DIAMONDS);
		Card fiveOfClubs = new Card(Rank.FIVE, Suit.CLUBS);
		
		player2.dealCard(fiveOfClubs);
		player2.dealCard(threeOfDiamonds);
		player2.dealCard(twoOfHearts);
		player2.dealCard(aceOfHearts);
		
		
		Card fiveOfSpades = new Card(Rank.FIVE, Suit.SPADES);
		Card sixOfHearts = new Card(Rank.SIX, Suit.HEARTS);
		Card sevenOfDiamonds = new Card(Rank.TWO, Suit.DIAMONDS);
		Card twoOfSpades = new Card(Rank.TWO, Suit.SPADES);
		
		player3.dealCard(twoOfSpades);
		player3.dealCard(sevenOfDiamonds);
		player3.dealCard(sixOfHearts);
		player3.dealCard(fiveOfSpades);
		
		Card fourOfHearts = new Card(Rank.FOUR, Suit.HEARTS);
		Card sixOfSpades = new Card(Rank.SIX, Suit.SPADES);
		Card sevenOfHearts = new Card(Rank.SEVEN, Suit.HEARTS);
		Card eightOfClubs = new Card(Rank.EIGHT, Suit.CLUBS);
		
		player4.dealCard(eightOfClubs);
		player4.dealCard(sevenOfHearts);
		player4.dealCard(sixOfSpades);
		player4.dealCard(fourOfHearts);
		
		warGame.beginBattle();
		
		assertEquals(4, warGame.getPlayers().size());
		assertTrue(warGame.getPlayers().contains(player1));
		assertTrue(warGame.getPlayers().contains(player2));
		assertTrue(warGame.getPlayers().contains(player3));
		assertTrue(warGame.getPlayers().contains(player4));
		
		LinkedList<Card> player1Deck = warGame.getPlayers().get(0).getDeck();
		LinkedList<Card> player2Deck = warGame.getPlayers().get(1).getDeck();
		LinkedList<Card> player3Deck = warGame.getPlayers().get(2).getDeck();
		LinkedList<Card> player4Deck = warGame.getPlayers().get(3).getDeck();
		
		assertTrue(warGame.getPlayers().get(0).equals(player1));
		
		assertTrue(player1Deck.contains(aceOfSpades));
		assertTrue(player1Deck.contains(kingOfHearts));
		assertTrue(player1Deck.contains(twoOfDiamonds));
		assertTrue(player1Deck.contains(fourOfClubs));
		assertEquals(6, warGame.getPlayers().get(0).getDeck().size());
		
		assertEquals(2, player2Deck.size());
		
		assertEquals(5, player3Deck.size());
		
		assertEquals(3, player4Deck.size());
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
