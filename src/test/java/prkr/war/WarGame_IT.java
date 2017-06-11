package prkr.war;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import prkr.war.Card.Rank;
import prkr.war.Card.Suit;
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
	
	/**
	 * P1 and P2 get into a war, then P3 and P4, then P3 and P4 again.
	 * P4 ultimately wins with high card of eightOfClubs
	 */
	@Test
	public void war_alternatingWars_test() {
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
		
		assertEquals(1, warGame.getPlayers().size());
		
		assertTrue(warGame.getPlayers().contains(player4));
		assertFalse(warGame.getPlayers().contains(player1));
		assertFalse(warGame.getPlayers().contains(player2));
		assertFalse(warGame.getPlayers().contains(player3));
		
		LinkedList<Card> player4Deck = player4.getDeck();
		
		assertEquals(16, player4Deck.size());
		
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
