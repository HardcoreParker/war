package prkr.war;

import java.util.LinkedList;

public class Player {
	
	private LinkedList<Card> playerDeck = new LinkedList<Card>();
	private final String name;
	
	Player(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public LinkedList<Card> getDeck() {
		return this.playerDeck;
	}
	
	/**
	 * Adds a card to the top of a given player's deck. This is intended to be used during dealing, 
	 * but not as a result of winning a War, as cards won during a War are added to the bottom of
	 * a given player's deck.
	 * @param card
	 */
	public void dealCard(Card card) {
		playerDeck.addFirst(card);
	}

	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Asserts equality based on Player name
	 */
	@Override
	public boolean equals(Object object) {
		if (this == object) return true; // true if same object reference
	    if (object == null || getClass() != object.getClass()) return false; // false if classes don't match

	    Player player = (Player) object;

	    if (!name.equals(player.getName())) return false;
	    
	    return true;
	}
}
