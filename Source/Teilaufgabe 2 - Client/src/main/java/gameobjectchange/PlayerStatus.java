package gameobjectchange;

import exceptions.ConvertPlayerStatusException;
import exceptions.ConvertPositionException;
import messagesbase.messagesfromserver.EPlayerGameState;
import network.ConvertFromNetwork;
import network.ConvertToNetwork;

public enum PlayerStatus implements ConvertFromNetwork<PlayerStatus, EPlayerGameState>,
		ConvertToNetwork<EPlayerGameState, PlayerStatus> {
	WON, LOST, YOURTURN, ENEMYTURN;

	@Override
	public EPlayerGameState convertToNetwork(PlayerStatus source) {
		if (source == null) {
			throw new ConvertPositionException("PositionState from client is null");
		}

		switch (source) {
		case WON:
			return EPlayerGameState.Won;
		case LOST:
			return EPlayerGameState.Lost;
		case YOURTURN:
			return EPlayerGameState.MustAct;
		case ENEMYTURN:
			return EPlayerGameState.MustWait;
		default:
			throw new ConvertPlayerStatusException("PositionState from client is invalid");

		}
	}

	@Override
	public PlayerStatus convertFromNetwork(EPlayerGameState target) {
		if (target == null) {
			throw new ConvertPositionException("PositionState from server is null");
		}

		switch (target) {
		case MustAct:
			return PlayerStatus.YOURTURN;
		case MustWait:
			return PlayerStatus.ENEMYTURN;
		case Lost:
			return PlayerStatus.LOST;
		case Won:
			return PlayerStatus.WON;
		default:
			throw new ConvertPositionException("PositionState from server is invalid");

		}
	}
}
