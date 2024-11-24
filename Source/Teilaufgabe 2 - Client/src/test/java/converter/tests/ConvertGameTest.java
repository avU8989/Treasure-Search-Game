package converter.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import components.Game;
import components.Player;
import gameobjectchange.PlayerStatus;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;

public class ConvertGameTest {

	@Test
	public void whenPollGame_convertGame_GameStateOrPlayerStateChanged() {
		// arrange
		UniquePlayerIdentifier testId = new UniquePlayerIdentifier("playerId432");
		PlayerState playerState = new PlayerState("Anh", "Vu", "vua36", EPlayerGameState.MustAct, testId, false);
		PlayerStatus state = PlayerStatus.YOURTURN;
		Set<PlayerState> players = new HashSet<>();
		players.add(playerState);
		GameState gameState = new GameState(players, "gameId89");
		Player player = new Player("playerId432", "Anh", "Vu", "vua36", PlayerStatus.ENEMYTURN);
		Game game = new Game();
		game.setPlayer(player);

		// act
		game.convertFromNetwork(gameState);

		// assert
		assertEquals(testId.getUniquePlayerID(), game.getPlayer().getPlayerId());
		assertEquals(state, game.getPlayer().getPlayerState());
		assertEquals(gameState.getGameStateId(), game.getGameStateID());

	}

	@Test
	public void whenPollGame_convertGame_TreasureCollected() {
		// arrange
		UniquePlayerIdentifier testId = new UniquePlayerIdentifier("playerId432");
		PlayerState playerState = new PlayerState("Anh", "Vu", "vua36", EPlayerGameState.MustWait, testId, true);
		PlayerStatus state = PlayerStatus.ENEMYTURN;
		Set<PlayerState> players = new HashSet<>();
		players.add(playerState);
		GameState gameState = new GameState(players, "gameId89");
		Player player = new Player("playerId432", "Anh", "Vu", "vua36", PlayerStatus.YOURTURN);
		Game game = new Game();
		game.setPlayer(player);
		// act
		game.convertFromNetwork(gameState);

		// assert
		assertEquals(testId.getUniquePlayerID(), game.getPlayer().getPlayerId());
		assertTrue(game.getGameMap().getFigure().getTreasureCollected());
		assertEquals(state, game.getPlayer().getPlayerState());

	}

}
