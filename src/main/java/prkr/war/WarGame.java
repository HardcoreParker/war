package prkr.war;

import java.util.ArrayList;

import exceptions.DuplicatePlayerException;
import exceptions.TooManyPlayersException;

public class WarGame {
	private ArrayList<Player> players = new ArrayList<Player>();
	
	/**
	 * Adds a player to the game. Does not accept more than 52 players 
	 * and will not accept two players with the same name as defined by String equality
	 * 
	 * @param name
	 * @throws DuplicatePlayerException
	 * @throws TooManyPlayersException
	 */
    public void addPlayer(String name) throws DuplicatePlayerException, TooManyPlayersException {
    	if(players.contains(new Player(name))) {
    		throw new DuplicatePlayerException();
    	}
    	if(players.size() >= 52) {
    		throw new TooManyPlayersException();
    	}
    	
    	players.add(new Player(name));
    }
    
    /**
     * For every player involved in the game, distributes cards equally to their respective decks.
     * Cards are removed from the supplied deck as they are distribued. Any cards remaining
     * after equal distribution are not removed from the deck and need to be handled separately.
     * 
     * @param deck
     */
    public void distributeCards(Deck deck) {
    	int cardsPerPlayer = deck.getCards().size() / getPlayers().size();
    	
    	for(int i = 1; i <= cardsPerPlayer; i++) {
    		for (Player player : getPlayers()) {
        		player.getDeck().addFirst(deck.getCards().pop());
        	}
    	}
    }
    
    public void removePlayer(Player player) {
    	players.remove(player);
    }
    
    public ArrayList<Player> getPlayers() {
    	return this.players;
    }

}
