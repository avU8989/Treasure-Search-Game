package gameMap;

import converter.ConvertFromNetwork;
import converter.ConvertToNetwork;
import messagesbase.messagesfromserver.ETreasureState;
import server.exceptions.ConverterException;

public enum TreasureState
		implements ConvertFromNetwork<TreasureState, ETreasureState>, ConvertToNetwork<ETreasureState, TreasureState> {
	NoOrUnknownTreasureState, MyTreasurePresent;

	@Override
	public ETreasureState convertToNetwork(TreasureState source) {
		if (source == null) {
			throw new ConverterException("Name: Treasure Converter", "Message: TreasureState from client is null");
		}
		switch (source) {
		case NoOrUnknownTreasureState:
			return ETreasureState.NoOrUnknownTreasureState;
		case MyTreasurePresent:
			return ETreasureState.MyTreasureIsPresent;
		default:
			throw new ConverterException("Name: Treasure Converter", "Message: TreasureState from client is invalid");

		}
	}

	@Override
	public TreasureState convertFromNetwork(ETreasureState target) {
		if (target == null) {
			throw new ConverterException("Name: Treasure Converter", "Message: TreasureState from server is null");
		}
		switch (target) {
		case NoOrUnknownTreasureState:
			return TreasureState.NoOrUnknownTreasureState;
		case MyTreasureIsPresent:
			return TreasureState.MyTreasurePresent;
		default:
			throw new ConverterException("Name: Treasure Converter", "Message: TreasureState from server is invalid");

		}
	}

}
