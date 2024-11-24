package terrain;

import exceptions.ConvertTreasureStateException;
import messagesbase.messagesfromserver.ETreasureState;
import network.ConvertFromNetwork;
import network.ConvertToNetwork;

public enum TreasureState
		implements ConvertFromNetwork<TreasureState, ETreasureState>, ConvertToNetwork<ETreasureState, TreasureState> {
	NoOrUnknownTreasureState, MyTreasurePresent;

	@Override
	public ETreasureState convertToNetwork(TreasureState source) {
		if (source == null) {
			throw new ConvertTreasureStateException("TreasureState from client is null");
		}
		switch (source) {
		case NoOrUnknownTreasureState:
			return ETreasureState.NoOrUnknownTreasureState;
		case MyTreasurePresent:
			return ETreasureState.MyTreasureIsPresent;
		default:
			throw new ConvertTreasureStateException("TreasureState from client is invalid");

		}
	}

	@Override
	public TreasureState convertFromNetwork(ETreasureState target) {
		if (target == null) {
			throw new ConvertTreasureStateException("TreasureState from server is null");
		}
		switch (target) {
		case NoOrUnknownTreasureState:
			return TreasureState.NoOrUnknownTreasureState;
		case MyTreasureIsPresent:
			return TreasureState.MyTreasurePresent;
		default:
			throw new ConvertTreasureStateException("TreasureState from server is invalid");

		}
	}

}
