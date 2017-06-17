package prkr.war.framework;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Deck {
	
	private LinkedList<Card> cardsList = new LinkedList<Card>();
	
	public Deck() {
		for (Card.Suit suit : Card.Suit.values()) {
			for (Card.Rank rank : Card.Rank.values()) {
				cardsList.add(new Card(rank, suit));
			}
		}
	}
	
	public LinkedList<Card> getCards() {
		return this.cardsList;
	}
	
	public void shuffle() {
		Collections.shuffle(cardsList);
	}
	
}
