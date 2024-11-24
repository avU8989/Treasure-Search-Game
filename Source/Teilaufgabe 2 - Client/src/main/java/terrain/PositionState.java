package terrain;

import exceptions.ConvertPositionException;
import messagesbase.messagesfromserver.EPlayerPositionState;
import network.ConvertFromNetwork;
import network.ConvertToNetwork;

public enum PositionState implements ConvertFromNetwork<PositionState, EPlayerPositionState>,
		ConvertToNetwork<EPlayerPositionState, PositionState> {
	BothPlayerPosition, EnemyPlayerPosition, MyPlayerPosition, NoPlayerPresent;

	@Override
	public EPlayerPositionState convertToNetwork(PositionState source) {
		if (source == null) {
			throw new ConvertPositionException("PositionState from client is null");
		}

		switch (source) {
		case BothPlayerPosition:
			return EPlayerPositionState.BothPlayerPosition;
		case EnemyPlayerPosition:
			return EPlayerPositionState.EnemyPlayerPosition;
		case MyPlayerPosition:
			return EPlayerPositionState.MyPlayerPosition;
		case NoPlayerPresent:
			return EPlayerPositionState.NoPlayerPresent;
		default:
			throw new ConvertPositionException("PositionState from client is invalid");

		}
	}

	@Override
	public PositionState convertFromNetwork(EPlayerPositionState target) {
		if (target == null) {
			throw new ConvertPositionException("PositionState from server is null");
		}

		switch (target) {
		case BothPlayerPosition:
			return PositionState.BothPlayerPosition;
		case EnemyPlayerPosition:
			return PositionState.EnemyPlayerPosition;
		case MyPlayerPosition:
			return PositionState.MyPlayerPosition;
		case NoPlayerPresent:
			return PositionState.NoPlayerPresent;
		default:
			throw new ConvertPositionException("PositionState from server is invalid");

		}
	}
}
