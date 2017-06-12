package prkr.war.framework;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import prkr.war.framework.Card;
import prkr.war.framework.Deck;
import prkr.war.framework.Card.Rank;
import prkr.war.framework.Card.Suit;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;

public class DeckTest {

	Deck testDeck; 
	
	@Before
	public void setUpBefore() {
		testDeck = new Deck();
	}
	
	@Test
	public void deck_constructor_test() {
		assert(testDeck.getCards().size() == 52);
		
		// Spot check that the deck is normalized
		assert(testDeck.getCards().getFirst().equals(new Card(Rank.TWO, Suit.CLUBS)));
		assert(testDeck.getCards().getLast().equals(new Card(Rank.ACE, Suit.SPADES)));
		
		assert(testDeck.getCards().get(10).equals(new Card(Rank.QUEEN, Suit.CLUBS)));
		assert(testDeck.getCards().get(20).equals(new Card(Rank.NINE, Suit.DIAMONDS)));
		assert(testDeck.getCards().get(40).equals(new Card(Rank.THREE, Suit.SPADES)));
		assert(testDeck.getCards().get(50).equals(new Card(Rank.KING, Suit.SPADES)));
	}
	
	@Test
	public void shuffle_cards_havent_mutated_test() {
		LinkedList<Card> cardsInitial = (LinkedList<Card>) testDeck.getCards().clone();
		
		testDeck.shuffle();
		LinkedList<Card> cardsShuffled = testDeck.getCards();
		
		assert(cardsShuffled.size() == 52);
		assertThat(cardsInitial, IsIterableContainingInAnyOrder.containsInAnyOrder(cardsShuffled.toArray()));
	}
	
	@Test
	public void shuffle_all_52_cards_are_not_in_same_places_test() {
		/* 
		 * It is technically possible for this test to false negative, but it shouldn't.
		 * While it isn't a good idea to try to assert that randomness has happened OR to test
		 * static util methods, a test case like this is infinitely more likely to save us from
		 * a poor refactor of Deck.shuffle() than it is to cause frustration over a failed build.
		 * (though productivity would likely be lost as it gets passed around the office)
		 */
		LinkedList<Card> cardsInitial = (LinkedList<Card>) testDeck.getCards().clone();
		testDeck.shuffle();
		LinkedList<Card> cardsShuffled = testDeck.getCards();
		
		int timesMatched = 0;
		for(int i = 0; i < cardsInitial.size(); i++) {
			if(cardsInitial.get(i).equals(cardsShuffled.get(i))) {
				timesMatched++;
			}
		}
		
		assert(timesMatched < 52);
	}
}
