package gameObjectState;

import messagesbase.messagesfromserver.EPlayerGameState;

public enum PlayerStatus {
	WON, LOST, YOURTURN, ENEMYTURN;

	public static EPlayerGameState convertPlayerStateToNetwork(PlayerStatus state) {
		return EPlayerGameState.values()[state.ordinal()];
	}

	public static PlayerStatus convertPlayerStateFromNetwork(EPlayerGameState state) {
		return PlayerStatus.values()[state.ordinal()];
	}
}
