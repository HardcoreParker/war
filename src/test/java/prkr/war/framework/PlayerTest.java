package prkr.war.framework;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import prkr.war.framework.Card;
import prkr.war.framework.Player;
import prkr.war.framework.Card.Rank;
import prkr.war.framework.Card.Suit;

public class PlayerTest {
	Player player1 = new Player("Player1");

	
	@Before
	public void setUpBefore() {
		player1 = new Player("Player1");
	}
	
	@Test
	public void getDeck_starts_empty_test() {
		LinkedList<Card> deck = player1.getDeck();
		assert(deck.isEmpty());
	}
	
	@Test
	public void giveCard_test() {
		player1.dealCard(new Card(Rank.ACE, Suit.SPADES));
		
		LinkedList<Card> deck = player1.getDeck();
		assert(deck.size() == 1);
		assert(deck.getFirst().equals(new Card(Rank.ACE, Suit.SPADES)));
	}
	
	@Test
	public void giveCard_multiple_cards_test() {
		player1.dealCard(new Card(Rank.ACE, Suit.SPADES));
		player1.dealCard(new Card(Rank.QUEEN, Suit.HEARTS));
		
		LinkedList<Card> deck = player1.getDeck();
		assert(deck.size() == 2);
		assert(deck.getFirst().equals(new Card(Rank.QUEEN, Suit.HEARTS)));
		assert(deck.get(1).equals(new Card(Rank.ACE, Suit.SPADES)));
	}
	
	@Test
	public void equals_test() {
		Player samePlayer = new Player("Player1");
		
		assertEquals(player1, samePlayer);
	}
	
	@Test
	public void equals_same_reference_test() {
		assertEquals(player1, player1);
	}
	
	@Test
	public void equals_not_equals_test() {
		Player player2 = new Player("Player2");
		
		assertNotEquals(player1, player2);
	}
	
	@Test
	public void equals_different_object_test() {
		String player2 = "Player1";
		
		assertNotEquals(player1, player2);
	}
	
	@Test
	public void toString_test() {
		assert(player1.toString().equals("Player1"));
	}

}
