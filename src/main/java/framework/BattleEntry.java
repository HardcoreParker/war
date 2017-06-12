package framework;

public class BattleEntry {
	private final Card card;
	private final Player player;
	
	public BattleEntry(Card card, Player player) {
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
