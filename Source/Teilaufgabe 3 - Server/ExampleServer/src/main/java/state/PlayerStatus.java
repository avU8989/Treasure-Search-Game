package state;

import converter.ConvertFromNetwork;
import converter.ConvertToNetwork;
import messagesbase.messagesfromserver.EPlayerGameState;
import server.exceptions.ConverterException;

public enum PlayerStatus implements ConvertFromNetwork<PlayerStatus, EPlayerGameState>,
		ConvertToNetwork<EPlayerGameState, PlayerStatus> {
	WON, LOST, YOURTURN, ENEMYTURN;

	@Override
	public EPlayerGameState convertToNetwork(PlayerStatus source) {
		if (source == null) {
			throw new ConverterException("Name: PlayerState Converter", "Message: PlayerState from client is null");
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
			throw new ConverterException("Name: PlayerState Converter",
					"Message: PositionState from client is invalid");

		}
	}

	@Override
	public PlayerStatus convertFromNetwork(EPlayerGameState target) {
		if (target == null) {
			throw new ConverterException("Name: PlayerState Converter", "Message: PositionState from server is null");
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
			throw new ConverterException("Name: PlayerState Converter",
					"Message: PositionState from server is invalid");

		}
	}
}
