package converter.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import components.Player;
import gameobjectchange.PlayerStatus;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.PlayerState;

public class ConvertPlayerTest {
	@Test
	public void PlayerState_converted_TurnsChanged() {
		UniquePlayerIdentifier testId = new UniquePlayerIdentifier("playerId432");
		PlayerStatus assertState = PlayerStatus.ENEMYTURN;
		PlayerState playerState = new PlayerState("Anh", "Vu", "vua36", EPlayerGameState.MustWait, testId, false);

		Player testPlayer = new Player();
		testPlayer.convertFromNetwork(playerState);

		assertEquals(assertState, testPlayer.getPlayerState());
		assertEquals(playerState.getFirstName(), testPlayer.getFirstName());
		assertEquals(playerState.getLastName(), testPlayer.getLastName());
		assertEquals(playerState.getUAccount(), testPlayer.getUaccount());
		assertEquals(playerState.getUniquePlayerID(), testPlayer.getPlayerId());

	}
}
