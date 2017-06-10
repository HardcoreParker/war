package prkr.war;

public class BattleEntry {
	private final Card card;
	private final Player player;
	
	BattleEntry(Card card, Player player) {
		this.card = card;
		this.player = player;
	}
	
	public Card getCard() {
		return this.card;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}
