package prkr.war;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import prkr.war.Card.Rank;
import prkr.war.Card.Suit;
import prkr.war.exceptions.DuplicatePlayerException;
import prkr.war.exceptions.TooManyPlayersException;

@RunWith(MockitoJUnitRunner.class)
public class App_IT {

	@Mock
	WarGame warGame ;
	
	Game game;
	
	@Before
	public void setUpBefore() {
		String input = "Player1"+"\n"+"Player2"+"\n"+"next\n"+"1\n";
	    InputStream in = new ByteArrayInputStream(input.getBytes());
	    System.setIn(in);
		Scanner scanner = new Scanner(System.in);
		
		game = new Game(warGame, scanner);
	}
	
	@Test
	public void test() {		
		BattleEntry mockBattleEntry1 = mock(BattleEntry.class);
		BattleEntry mockBattleEntry2 = mock(BattleEntry.class);
		
		HashSet<BattleEntry> battleEntries = new HashSet<BattleEntry>();
		battleEntries.add(mockBattleEntry1);
		battleEntries.add(mockBattleEntry2);
		
		when(warGame.gatherEntries()).thenReturn(battleEntries);
		
		BattleResolution mockResolution = mock(BattleResolution.class);
		when(warGame.initiateBattle(battleEntries)).thenReturn(mockResolution);

		when(mockResolution.getWinner()).thenReturn(new Player("Player 1"));
		when(mockResolution.getPot()).thenReturn(new HashSet<Card>());
		when(mockResolution.getWinningCard()).thenReturn(new Card(Rank.ACE, Suit.SPADES));

		game.startGame();
		
		try {
			verify(warGame, times(2)).addPlayer(anyString());
		} catch (DuplicatePlayerException e) {
			fail();
		} catch (TooManyPlayersException e) {
			fail();
		}
		
		verify(warGame, times(1)).distributeCards(any(Deck.class));
		verify(warGame, times(1)).gatherEntries();
		verify(warGame, times(1)).initiateBattle(battleEntries);
		verify(warGame, times(1)).awardWinner(mockResolution);
		verify(warGame, times(1)).refreshPot();
		verify(warGame, times(1)).removeIneligiblePlayers();
		verify(warGame, times(3)).getPlayers();
		Mockito.verifyNoMoreInteractions(warGame);
	}
	

}
