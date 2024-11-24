package terrain;

import exceptions.ConvertFortStateException;
import messagesbase.messagesfromserver.EFortState;
import network.ConvertFromNetwork;
import network.ConvertToNetwork;

public enum FortState implements ConvertFromNetwork<FortState, EFortState>, ConvertToNetwork<EFortState, FortState> {
	NoOrUnknownFortState, MyFortPresent, EnemyFortPresent;

	@Override
	public EFortState convertToNetwork(FortState source) {
		if (source == null) {
			throw new ConvertFortStateException("FortState from server is null");
		}
		switch (source) {
		case NoOrUnknownFortState:
			return EFortState.NoOrUnknownFortState;
		case MyFortPresent:
			return EFortState.MyFortPresent;
		case EnemyFortPresent:
			return EFortState.EnemyFortPresent;
		default:
			throw new ConvertFortStateException("FortState from server is invalid");
		}
	}

	@Override
	public FortState convertFromNetwork(EFortState target) {
		if (target == null) {
			throw new ConvertFortStateException("FortState from server is null");
		}
		switch (target) {
		case NoOrUnknownFortState:
			return FortState.NoOrUnknownFortState;
		case MyFortPresent:
			return FortState.MyFortPresent;
		case EnemyFortPresent:
			return FortState.EnemyFortPresent;
		default:
			throw new ConvertFortStateException("FortState from server is invalid");
		}
	}

}
