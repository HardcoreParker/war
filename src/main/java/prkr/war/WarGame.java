package prkr.war;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
        
	private class RoundProposal {
		private final Card.Rank rank;
		private final Player player;
		RoundProposal(Card.Rank rank, Player player) {
			this.rank = rank;
			this.player = player;
		}
	}
    
    public HashMap<Player, LinkedList<Card>> initRound(HashMap<Card, Player> offerings) {
    	HashMap<Card.Rank, AtomicInteger> ranksAndOccurrences = new HashMap<Card.Rank, AtomicInteger>();

		for(Card card : offerings.keySet()) {
			Set<Card> cards = offerings.keySet();
			if(cards.contains(card.getRank())) { // refactor to just check contains rank
				ranksAndOccurrences.get(card.getRank()).
				integer++;
			}
		}
    	
    	HashMap<Card.Rank, Player> ranksInvolvedInRound = new HashMap<Card.Rank, Player>();
    	
		Card highCard = null;
		Player winner = null;
		for(Card currentCard : cardsInRound.keySet()) {
			if(highCard == null) {
				highCard = currentCard;
				winner = cardsInRound.get(currentCard);
			} else if (currentCard.getRank().ordinal() > highCard.getRank().ordinal()) {
				highCard = currentCard;
				winner = cardsInRound.get(currentCard);
			} else if (currentCard.getRank().ordinal() == highCard.getRank().ordinal()) {
				HashMap<Card, Player> newWar = new HashMap<Card, Player>();
			}
		}
    	
    	return null;
    }
    
    public void removePlayer(Player player) {
    	players.remove(player);
    }
    
    public ArrayList<Player> getPlayers() {
    	return this.players;
    }

}
