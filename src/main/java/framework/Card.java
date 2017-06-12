package framework;

public class Card {
	
	// There is no common ranking of suits - rank in ascending alphabetical order
	public enum Suit {	CLUBS, DIAMONDS, HEARTS, SPADES; }
	public enum Rank { 	TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE; }
	
	private final Rank rank;
	private final Suit suit;
	
	public Card(Rank rank, Suit suit) {
		this.rank = rank;
		this.suit = suit;
	}
	
	public Rank getRank() {
		return this.rank;
	}
	public Suit getSuit() {
		return this.suit;
	}
	
	@Override
	public String toString() { 
		return this.rank + " of " + this.suit;
	}
	
	@Override
	public boolean equals(Object object) {
	    if (this == object) return true; // true if same object reference
	    if (object == null || getClass() != object.getClass()) return false; // false if classes don't match

	    Card card = (Card) object;

	    if (rank != card.rank) return false;
	    if (suit != card.suit) return false;
	    
	    return true;
	}
}
