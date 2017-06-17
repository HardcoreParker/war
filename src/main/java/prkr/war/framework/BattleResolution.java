package prkr.war.framework;

import java.util.HashSet;
import java.util.Set;

public class BattleResolution {
	private final Player winner;
	private final Set<Card> pot;
	private final Card winningCard;
	
	public BattleResolution(Player winner, Set<Card> pot, Card winningCard) {
		this.winner = winner;
		this.pot = pot;
		this.winningCard = winningCard;
	}

	public Player getWinner() {
		return this.winner;
	}

	public Set<Card> getPot() {
		return this.pot;
	}

	public Card getWinningCard() {
		return winningCard;
	}
}
