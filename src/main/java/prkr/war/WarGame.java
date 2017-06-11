package prkr.war;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import prkr.war.Card.Rank;
import prkr.war.exceptions.DuplicatePlayerException;
import prkr.war.exceptions.TooManyPlayersException;

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
    
    public ArrayList<Player> getPlayers() {
    	return this.players;
    }
    
    public void removePlayer(Player player) {
    	players.remove(player);
    }
    
    /**
     * For every player involved in the game, distributes cards equally to their respective decks.
     * Cards are removed from the supplied deck as they are distribued. Any cards remaining
     * after equal distribution are not removed from the supplied deck and need to be handled separately.
     * 
     * TODO - Handle remaining cards separately
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
    
    public void beginBattle() {
    	HashSet<BattleEntry> entries = new HashSet<BattleEntry>();
    	for(Player player : getPlayers()) {
    		Card card = player.getDeck().removeFirst();
    		entries.add(new BattleEntry(card, player));
    	}
    	
    	BattleResolution resolution = initiateBattle(entries);
    }
    
    /** 
     * This method contains the core logic for intaking and resolving a battle of War.
     * It accepts an ArrayList of RoundEntries and proceeds to determine if there are any matches among the cards entered into the battle
     * If there are matches, it first determines if the players involved in the matches are able to produce another card
     * 	If their deck contains another card, a war is begun
     * 	If their deck is empty, we resolve the matches based on suit 
     * 
     * Downstream calls to initWar() return here, and in the case of multiple wars among >2 players, this variant of War
     * follows a winner-takes-all method
     * 
     * @param battleEntries
     * @return RoundResolution
     */
    public BattleResolution initiateBattle(HashSet<BattleEntry> battleEntries) {
    	HashMap<Rank, HashSet<BattleEntry>> mapOfPairs = identifyPairs(battleEntries);
		HashSet<Card> pot = new HashSet<Card>();
		Player winner = null;
		Card highCard = null;

		// TODO - reverse if and else
    	if(mapOfPairs.size() > 0) { // Decides if there will be a war
    		// Add cards to pot
    		for(Rank rank : mapOfPairs.keySet()) { // Set up a particular war for this rank
    			HashSet<BattleEntry> entriesInAParticularWar = mapOfPairs.get(rank);
    			
    			BattleResolution battleResolution = null;
    			
    			// TODO - Next - Since we had a bug from initiateSuitResolution and initWar being different
    			// by adding cards to pot at different/non existant times, it's time to make them 1 method 
    			if(anyPlayersHaveEmptyDeck(entriesInAParticularWar)) {
    				// Evaluate winner based on suit strength
    				battleResolution = initiateSuitResolution(entriesInAParticularWar);
    			} else {
    				// Standard war
    				battleResolution = initWar(entriesInAParticularWar);
    			}
    			
    			// TODO - Pull all of these ordinal comparisons out to a method that takes compareSuit as a bool
    			// always add the pot
    			pot.addAll(battleResolution.getPot());
    			
    			for(BattleEntry entry : battleEntries) {
    				pot.add(entry.getCard());
    			}
    			
    			if(highCard == null || battleResolution.getWinningCard().getRank().ordinal() > highCard.getRank().ordinal()) { 
    				/* Note - Shouldn't have to worry about changing the highCard due to a higher suit strength - this would get resolved in initiateSuitResolution */
    				highCard = battleResolution.getWinningCard();
    				winner = battleResolution.getWinner();
    			}
    		}
    	} else {
    		for(BattleEntry entry : battleEntries) {
    			Card card = entry.getCard();
    			Player player = entry.getPlayer();
    			pot.add(card); // Add this card to the 'pot'
    			
    			// Assign new highCard if current card is eligible
    			if(highCard == null) {
    				highCard = card;
    				winner = player;
    			} else if (card.getRank().ordinal() > highCard.getRank().ordinal()) {
    				highCard = card;
    				winner = player;
    			}
    		}
    	}
    	
    	BattleResolution resolution = new BattleResolution(winner, pot, highCard);
    	    	
    	return resolution;
    }

	private void awardWinner(BattleResolution resolution) {
		Player battleWinner = resolution.getWinner();
    	battleWinner.getDeck().addAll(resolution.getPot());
	}

	protected BattleResolution initiateSuitResolution(Set<BattleEntry> entriesInAParticularWar) {
    	Card winningCard = null;
    	Player winner = null;
    	HashSet<Card> pot = new HashSet<Card>();
    	for(BattleEntry entry : entriesInAParticularWar) {
    		// Always add to pot
    		pot.add(entry.getCard());
    		
    		if(winningCard == null || entry.getCard().getSuit().ordinal() > winningCard.getSuit().ordinal()){
    			winningCard = entry.getCard();
    			winner = entry.getPlayer();
    		}
    		
    	}
    	
    	return new BattleResolution(winner, pot, winningCard);
	}
	
	protected boolean anyPlayersHaveEmptyDeck(Set<BattleEntry> entriesInAParticularWar) {
    	for(BattleEntry entry: entriesInAParticularWar) {
    		if(entry.getPlayer().getDeck().isEmpty()) {
    			return true;
    		}
    	}
    	return false;
	}

    protected BattleResolution initWar(HashSet<BattleEntry> entriesInAParticularWar) {
    	HashSet<Card> pot = new HashSet<Card>();

    	// New entries for next battle
    	HashSet<BattleEntry> battleEntries = new HashSet<BattleEntry>();

    	for(BattleEntry entry : entriesInAParticularWar) {
    		Player player = entry.getPlayer();
    		Card card = player.getDeck().removeFirst();
    		battleEntries.add(new BattleEntry(card, player));
    	}
    	
    	BattleResolution resolution = initiateBattle(battleEntries);
    	
    	pot.addAll(resolution.getPot());
    	
    	return new BattleResolution(resolution.getWinner(), pot, resolution.getWinningCard());
	}
    
    /**
	 * Takes in a list of RoundEntry objects and returns a list of ranks that had more than 1 entry
	 * Matched ranks will be used to calculate wars that need to be started.
	 * 
	 * @param battleEntries
	 * @return
	 */
    protected HashMap<Rank, HashSet<BattleEntry>> identifyPairs(HashSet<BattleEntry> battleEntries) {
    	HashMap<Rank, HashSet<BattleEntry>> matchesAndEntries = new HashMap<Rank, HashSet<BattleEntry>>();
    	
		for(BattleEntry battleEntry : battleEntries) {
			Rank thisRank = battleEntry.getCard().getRank();
			
			if(matchesAndEntries.keySet().contains(thisRank)) {
				matchesAndEntries.get(thisRank).add(battleEntry);
			} else {
				matchesAndEntries.put(thisRank, new HashSet<BattleEntry>());
				matchesAndEntries.get(thisRank).add(battleEntry);
			}
		}
		
		/* Remove all of the entries in matchesAndEntries that doesn't have a list bigger than 1 */
		HashSet<Rank> eligibleForRemoval = new HashSet<Rank>();
		for(Rank rank : matchesAndEntries.keySet()) {
    		if (matchesAndEntries.get(rank).size() <= 1) {
    			eligibleForRemoval.add(rank);
    		}
    	}
		for(Rank rank : eligibleForRemoval) {
			matchesAndEntries.remove(rank);
		}
		
    	return matchesAndEntries;
    }
}
