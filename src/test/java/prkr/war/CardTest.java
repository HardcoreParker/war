package prkr.war;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import prkr.war.Card.Rank;
import prkr.war.Card.Suit;

public class CardTest {
	
	Card aceOfSpades;
	
	@Before
	public void setUpBefore() {
		aceOfSpades = new Card(Rank.ACE, Suit.SPADES);
	}
	
	@Test
	public void toString_test() {
		assert(aceOfSpades.toString().equals("ACE of SPADES"));
	}
	
	@Test
	public void equals_different_reference_same_object_test() {
		Card differentAceOfSpades = new Card(Rank.ACE, Suit.SPADES);
		assertEquals(aceOfSpades, differentAceOfSpades);
	}
	
	@Test
	public void equals_true_same_object_test() {
		assertEquals(aceOfSpades, aceOfSpades);
	}
	
	@Test 
	public void equals_false_not_a_card_test() {
		String cardString = "Rank.ACE, Suit.SPADES";
		assertNotEquals(aceOfSpades, cardString);
	}
	
	@Test 
	public void equals_false_null_test() {
		assertNotEquals(aceOfSpades, null);
	}
	
	@Test
	public void equals_false_different_rank_test() {
		Card twoOfSpades = new Card(Rank.TWO, Suit.SPADES);
		assertNotEquals(aceOfSpades, twoOfSpades);
	}
	
	@Test
	public void equals_false_different_suit_test() {
		Card aceOfHearts = new Card(Rank.ACE, Suit.HEARTS);
		assertNotEquals(aceOfSpades, aceOfHearts);
	}
	
	@Test
	public void getters_test() {
		assertEquals(aceOfSpades.getRank(), Rank.ACE);
		assertEquals(aceOfSpades.getSuit(), Suit.SPADES);
	}
	
	@Test
	public void ordinal_rank_comparison_test() {
		Card queenOfHearts = new Card(Rank.QUEEN, Suit.HEARTS);
		assert(aceOfSpades.getRank().ordinal() > queenOfHearts.getRank().ordinal());
	}
	
	@Test
	public void ordinal_suit_comparison_test() {
		// In order of strong to weak
		Card queenOfSpades = new Card(Rank.QUEEN, Suit.SPADES);
		Card queenOfHearts = new Card(Rank.QUEEN, Suit.HEARTS);
		Card queenOfDiamonds = new Card(Rank.QUEEN, Suit.DIAMONDS);
		Card queenOfClubs = new Card(Rank.QUEEN, Suit.CLUBS);
		
		assert(queenOfSpades.getSuit().ordinal() > queenOfHearts.getSuit().ordinal());
		assert(queenOfHearts.getSuit().ordinal() > queenOfDiamonds.getSuit().ordinal());
		assert(queenOfDiamonds.getSuit().ordinal() > queenOfClubs.getSuit().ordinal());
	}
	
	@Test
	public void ordinal_is_ace_high_test() {
		Card twoOfSpades = new Card(Rank.TWO, Suit.SPADES);
		assert(aceOfSpades.getRank().ordinal() > twoOfSpades.getRank().ordinal());
	}

}
