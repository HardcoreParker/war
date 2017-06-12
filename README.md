# War

This is my solution for implementing the card game War. 

To run it, you can perform a `java -jar target/war-0.0.1.SNAPSHOT.jar`, or compile it yourself with a `mvn install` followed by the aformentioned `java -jar target/war-0.0.1.SNAPSHOT.jar`.

You will then be provided instructions on how to set up the game. New rounds are initiated by submitting anything through the terminal.

![Reminds you of college, doesn't it?](http://i.imgur.com/CcB1hTjg.png)

## Rules

This version of War follows the traditional rules for the most part:

Each player turns a card (this is called a battle)
If the ranks of any cards match, a war is initiated

War is resolved with each player adding 1 card to the pot (referred to as a 'burn', and then flipping the next card on their deck. This is repeated until a high card is determined. 
The winner then takes all the cards in the pot, and adds them to the bottom of their deck.

Any player who cannot perform the 'burn' or the submission to a War is disqualified. The winner is resolved when there are no other players with cards.

### Caveat

Unlike traditional War, this game allows configuration for more than 2 players. For simplicity (and to follow the most common implementation of >2 player rules), if there is any matches among the cards flipped during a battle, every player is involved in the ensuing war and 1 player eventually emerges the winner of the cumulative pot.

Due to this implementation, the game is currently limited to instantiating 6 players maximum. Having 5+ players often leads to enough cumulative Wars to disqualify every player within the first round, and that isn't very fun at all.

## Metrics
### Tests

At the time of writing this, there are 43 tests hitting this - mostly unit tests. While I did make sure to include some Inversion of Control (viewable in [Game.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/Game.java) + [WarGame.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/WarGame.java) and leveraged in [APP_IT.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/test/java/prkr/war/App_IT.java) to inject Mocks), in my opionion, the Integration Testing is what needs the most lovin' on this project.

### Coverage

src/main/java is currently sitting at 91.9% code coverage, primarily from unit testing.

## The Quick Look

### prkr.war

Main package dealing with the orchestration of game flow. 

[App.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/App.java) - Consists of the entry point and main method.

[Game.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/Game.java)- the `Scanner` listener and flow manager

[WarGame.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/WarGame.java) - the object used to instantiate a game of war and more granular methods to move the flow along.

### prkr.war.framework

Objects needed to build the playing field for War.

[Player.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/framework/Player.java)

[Deck.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/framework/Deck.java)

[Card.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/framework/Card.java)

[BattleResolution.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/framework/BattleResolution.java) - For encapsulating the winner, winnings, and winning card of a Battle

[BattleEntry.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/framework/BattleEntry.java) - For encapsulating a <Card, Player> pair. Mostly keeps code clean and data structures prettier looking while allowing for verbose reporting.

### prkr.war.util

[PrintingUtil.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/util/PrintingUtil.java) - For extracting all the yucky `System.out.println()` work away from application logic

### prkr.war.exceptions

[DuplicatePlayerException.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/exceptions/DuplicatePlayerException.java) - Duplicate name is defined by String.equals

[GameOverException.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/exceptions/GameOverException.java) - For that edge case easter egg when every player remaining is disqualified for not having enough cards to fulfill a Battle/cumulative War (translation - the game started with 5+ players)

[TooManyPlayersException.java](https://github.com/HardcoreParker/NWEA-war/blob/master/src/main/java/prkr/war/exceptions/TooManyPlayersException.java) - To communicate an exceedence of allocated players (currently set to 6)


Happy reading! And thank you for your time!
