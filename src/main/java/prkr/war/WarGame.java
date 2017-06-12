package prkr.war;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import framework.BattleEntry;
import framework.BattleResolution;
import framework.Card;
import framework.Deck;
import framework.Player;
import framework.Card.Rank;
import prkr.war.exceptions.DuplicatePlayerException;
import prkr.war.exceptions.GameOverException;
import prkr.war.exceptions.TooManyPlayersException;
import util.PrintingUtil;

public class WarGame {
	
	private ArrayList<Player> players = new ArrayList<Player>();
	private HashSet<Card> pot = new HashSet<Card>();
	private static PrintingUtil printingUtil;
	
	WarGame(PrintingUtil printingUtil) {
		this.printingUtil = printingUtil;
	}
	
	/**
	 * Adds a player to the game. Does not accept more than 6 players 
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
    	if(players.size() >= 6) {
    		throw new TooManyPlayersException();
    	}
    	
    	Player player = new Player(name);
    	players.add(player);
    	printingUtil.announcePlayerAdded(player);
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

    /**
     * Refreshes the pot owned by WarGame to be empty
     */
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

	/**
	 * Checks every player maintained by the WarGame and removes them from the game if their deck size is < 1
	 * @throws GameOverException 
	 */
	protected void removeIneligiblePlayers() throws GameOverException {
    	ArrayList<Player> playersToRemove = new ArrayList<Player>();
    	for(Player player : getPlayers()) {
    		if(player.getDeck().size() < 1) {
    			playersToRemove.add(player);
    		}
    	}
    	
    	if(playersToRemove.size() == getPlayers().size()) {
        	throw new GameOverException();
    	}
    	
    	for(Player player : playersToRemove) {
        	players.remove(player);
        	printingUtil.announcePlayerEliminated(player);
    	}
    }

	/** 
     * This method contains the core logic for starting and resolving a battle, including initiating a war
     * 
     * It first determines if there are any matches among the ranks of cards submitted
     * If there are matches, it initiates a war, adds all cards that are popped from said war to the pot, and then checks
     * 	those new cards for matches in order to initiate another war.
     * 
     * When there is finally no matches in the resolved cards, it takes the latest round of cards, 
     * finds the high card, and returns the results
     * 
     * @param battleEntries
     * @return RoundResolution
     */
    public BattleResolution initiateBattle(HashSet<BattleEntry> battleEntries) {
    	addCardsToPot(battleEntries);
    	
    	HashMap<Rank, HashSet<BattleEntry>> mapOfPairs = identifyPairs(battleEntries);
    	HashSet<BattleEntry> entriesForLatestWar = null;
    	while(!mapOfPairs.isEmpty()) { // Decides if there will be a war
    		printingUtil.announceWar(mapOfPairs);
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
    	
		for(BattleEntry entry : entriesEligibleForWinning) {
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

		BattleResolution resolution = new BattleResolution(winner, getPot(), highCard);
		printingUtil.announceResolvedBattle(resolution);
		return resolution;
    	
    }
   
    /**
     * Takes the pot stored in WarGame and awards it to the winner stored in a BattleResolution
     * Note: The Pot object stored in BattleResolution is primarily used for reporting and is not the source of this award
     * @param resolution
     */
    protected void awardWinner(BattleResolution resolution) {
		Player battleWinner = resolution.getWinner();
		for(Card card : getPot()) {
			battleWinner.getDeck().addLast(card);
		}
	}

	protected HashSet<BattleEntry> initWar() {
    	burnACard();
		
		return gatherEntries();
	}
    
	/**
	 * Convenience method for removing a card from each players deck and adding to pot. Checks for empty decks.
	 */
	protected void burnACard() {
		try {
			removeIneligiblePlayers();
		} catch (GameOverException e) {
			printingUtil.announceAllPlayersAreEliminated();
		}
		
		for(Player player : getPlayers()) {
			Card card = player.getDeck().removeFirst();
			getPot().add(card);
		}
	}
	
    /**
     * Convenience method for gathering the first card from every player. Empty deck safe
     * @return
     */
	protected HashSet<BattleEntry> gatherEntries() {
		try {
			removeIneligiblePlayers();
		} catch (GameOverException e) {
			printingUtil.announceAllPlayersAreEliminated();
		}
		
    	HashSet<BattleEntry> entries = new HashSet<BattleEntry>();

    	for(Player player : getPlayers()) {
    		Card card = player.getDeck().removeFirst();
    		entries.add(new BattleEntry(card, player));
    	}
    	
    	return entries;
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
}
