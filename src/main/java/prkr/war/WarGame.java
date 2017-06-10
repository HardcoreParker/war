package prkr.war;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import exceptions.DuplicatePlayerException;
import exceptions.TooManyPlayersException;
import prkr.war.Card.Rank;

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
    
    public void beginBattle() {
    	ArrayList<BattleEntry> entries = new ArrayList<BattleEntry>();
    	for(Player player : getPlayers()) {
    		Card card = player.getDeck().removeFirst();
    		entries.add(new BattleEntry(card, player));
    	}
    	
    	BattleResolution resolution = compareAllCardsInRound(entries);
    	
    	Player battleWinner = resolution.getWinner();
    	battleWinner.getDeck().addAll(resolution.getPot());
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
    public BattleResolution compareAllCardsInRound(ArrayList<BattleEntry> battleEntries) {
    	HashMap<Rank, Set<BattleEntry>> rankMatches = gatherMatchedRanksAndEntries(battleEntries);
		ArrayList<Card> pot = new ArrayList<Card>();
		Player winner = null;
		Card highCard = null;

    	if(rankMatches.size() > 0) { // Decides if there will be a war
    		for(Rank rank : rankMatches.keySet()) { // Set up a particular war for this rank
    			Set<BattleEntry> entriesInAParticularWar = rankMatches.get(rank);
    			
    			BattleResolution battleResolution = null;
    			
    			if(anyPlayersHaveEmptyDeck(entriesInAParticularWar)) {
    				// Evaluate winner based on suit strength
    				battleResolution = initiateSuitResolution(entriesInAParticularWar);
    			} else {
    				// Standard war
    				battleResolution = initWar(entriesInAParticularWar);
    			}
    			
    			// always add the pot
    			pot.addAll(battleResolution.getPot());
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

	protected BattleResolution initiateSuitResolution(Set<BattleEntry> entriesInAParticularWar) {
    	Card winningCard = null;
    	Player winner = null;
    	ArrayList<Card> pot = new ArrayList<Card>();
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

    protected BattleResolution initWar(Set<BattleEntry> entriesInAParticularWar) {
    	ArrayList<Card> pot = new ArrayList<Card>(); // List because order is important

    	ArrayList<BattleEntry> battleEntries = new ArrayList<BattleEntry>();

    	for(BattleEntry entry : entriesInAParticularWar) {
    		Player player = entry.getPlayer();
    		Card card = player.getDeck().removeFirst();
    		pot.add(card);
    		battleEntries.add(new BattleEntry(card, player));
    	}
    	
    	BattleResolution resolution = compareAllCardsInRound(battleEntries);
    	
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
    protected HashMap<Rank, Set<BattleEntry>> gatherMatchedRanksAndEntries(ArrayList<BattleEntry> battleEntries) {
    	HashMap<Rank, Set<BattleEntry>> matchesAndEntries = new HashMap<Rank, Set<BattleEntry>>();
    	
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
		Set<Rank> eligibleForRemoval = new HashSet<Rank>();
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
    
    public void removePlayer(Player player) {
    	players.remove(player);
    }
    
    public ArrayList<Player> getPlayers() {
    	return this.players;
    }

}
