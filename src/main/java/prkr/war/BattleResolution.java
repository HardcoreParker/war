package prkr.war;

import java.util.ArrayList;

public class BattleResolution {
	private final Player winner;
	private final ArrayList<Card> pot;
	private final Card winningCard;
	
	BattleResolution(Player winner, ArrayList<Card> pot, Card winningCard) {
		this.winner = winner;
		this.pot = pot;
		this.winningCard = winningCard;
	}

	public Player getWinner() {
		return winner;
	}

	public ArrayList<Card> getPot() {
		return pot;
	}

	public Card getWinningCard() {
		return winningCard;
	}
}
