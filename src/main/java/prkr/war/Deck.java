package prkr.war;

import java.util.Collections;
import java.util.LinkedList;

public class Deck {
	
	private LinkedList<Card> cardsList = new LinkedList<Card>();
	
	Deck() {
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
		Collections.shuffle(this.cardsList);
	}
	
}
