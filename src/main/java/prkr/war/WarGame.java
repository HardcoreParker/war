package prkr.war;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import prkr.war.Card.Rank;
import prkr.war.exceptions.DuplicatePlayerException;
import prkr.war.exceptions.TooManyPlayersException;

public class WarGame {
	
	private ArrayList<Player> players = new ArrayList<Player>();
	private HashSet<Card> pot = new HashSet<Card>();
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
	 * after equal distribution are added to the pot
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
		
		// handle remaining cards
		for(Card card : deck.getCards()) {
			addToPot(card);
		}
	}

	/**
     * Returns a list of players still in the game
     * @return
     */
    public ArrayList<Player> getPlayers() {
    	return this.players;
    }
    
    /**
     * Getter for WarGame.Pot
     * @return
     */
    public HashSet<Card> getPot() {
    	return this.pot;
    }

	protected void refreshPot() {
		pot = new HashSet<Card>();
	}

	/**
	 * Convenience method for adding the cards from a set of BattleEntries to the pot
	 * @param battleEntries
	 */
	protected void addCardsToPot(HashSet<BattleEntry> battleEntries) {
		for(BattleEntry entry : battleEntries) {
			addToPot(entry.getCard());
		}
	}
	
    /**
	 * Convenience method for adding cards to the pot
	 * @param card
	 */
	protected void addToPot(Card card) {
		getPot().add(card);
	}

	protected void removeIneligiblePlayers() {
    	ArrayList<Player> playersToRemove = new ArrayList<Player>();
    	for(Player player : getPlayers()) {
    		if(player.getDeck().size() < 1) {
    			playersToRemove.add(player);
    		}
    	}
    	
    	for(Player player : playersToRemove) {
        	players.remove(player);
        	// TODO = extract to gamelogger
        	System.out.println(player + " has been eliminated.");
    	}
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
    	addCardsToPot(battleEntries);
    	
    	HashMap<Rank, HashSet<BattleEntry>> mapOfPairs = identifyPairs(battleEntries);
    	HashSet<BattleEntry> entriesForLatestWar = null;
    	
    	while(!mapOfPairs.isEmpty() && getPlayers().size() > 1) { // Decides if there will be a war
    		entriesForLatestWar = initWar();
        
    		addCardsToPot(entriesForLatestWar);
    		
    		mapOfPairs = identifyPairs(entriesForLatestWar);
    	}
    	

    	Card highCard = null;
    	Player winner = null;
    	
    	HashSet<BattleEntry> entriesEligibleForWinning = null;
    	if(entriesForLatestWar != null) {
    		entriesEligibleForWinning = entriesForLatestWar;
    	} else {
    		entriesEligibleForWinning = battleEntries;
    	}
    	
    	System.out.println("BATTLE:");
		for(BattleEntry entry : entriesEligibleForWinning) {
			System.out.println(entry.getPlayer().toString()+" played "+entry.getCard().toString());
			Card card = entry.getCard();
			Player player = entry.getPlayer();
			
			if(highCard == null) {
				highCard = card;
				winner = player;
			} else if (card.getRank().ordinal() > highCard.getRank().ordinal()) {
				highCard = card;
				winner = player;
			}
		}
		
		return new BattleResolution(winner, getPot(), highCard);
    	
    }
    		
    protected void awardWinner(BattleResolution resolution) {
		Player battleWinner = resolution.getWinner();
		for(Card card : getPot()) {
			battleWinner.getDeck().addLast(card);
		}
	}
	
	protected boolean anyPlayersHaveEmptyDeck() {
		for(Player player : getPlayers()) {
			if(player.getDeck().isEmpty()) {
    			return true;
    		}
		}
		return false;
	}

	
	protected HashSet<BattleEntry> initWar() {
		ArrayList<Player> allPlayers = getPlayers();
    	HashSet<BattleEntry> battleEntries = new HashSet<BattleEntry>();
    	
    	removeIneligiblePlayers();
    	burnACard();
    	
    	for(Player player : allPlayers) {
			Card card = player.getDeck().removeFirst();
    		battleEntries.add(new BattleEntry(card, player));
		}
		
		return battleEntries;
	}
    
	protected void burnACard() {
		for(Player player : getPlayers()) {
			Card card = player.getDeck().removeFirst();
			getPot().add(card);
		}
		removeIneligiblePlayers();
	}
	
    /**
	 * Takes in a list of BattleEntry and returns a list of ranks that had more than 1 entry
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

	protected HashSet<BattleEntry> gatherEntries() {
    	HashSet<BattleEntry> entries = new HashSet<BattleEntry>();

    	for(Player player : getPlayers()) {
    		Card card = player.getDeck().removeFirst();
    		entries.add(new BattleEntry(card, player));
    	}
    	
    	return entries;
	}
}
