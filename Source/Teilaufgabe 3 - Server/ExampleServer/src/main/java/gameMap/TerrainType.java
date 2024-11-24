package gameMap;

import converter.ConvertFromNetwork;
import converter.ConvertToNetwork;
import messagesbase.messagesfromclient.ETerrain;
import server.exceptions.ConverterException;

public enum TerrainType implements ConvertFromNetwork<TerrainType, ETerrain>, ConvertToNetwork<ETerrain, TerrainType> {
	WATER, GRASS, MOUNTAIN;

	@Override
	public TerrainType convertFromNetwork(ETerrain target) {
		if (target == null) {
			throw new ConverterException("Name: Terain Converter", "Message: TerrainType from server is null");
		}
		switch (target) {
		case Grass:
			return TerrainType.GRASS;
		case Mountain:
			return TerrainType.MOUNTAIN;
		case Water:
			return TerrainType.WATER;
		default:
			throw new ConverterException("Name: Terain Converter", "Message: Invalid TerrainType from server");
		}
	}

	@Override
	public ETerrain convertToNetwork(TerrainType source) {
		if (source == null) {
			throw new ConverterException("Name: Terain Converter", "Message: Invalid terrainType to server");
		}
		switch (source) {
		case GRASS:
			return ETerrain.Grass;
		case MOUNTAIN:
			return ETerrain.Mountain;
		case WATER:
			return ETerrain.Water;
		default:
			throw new ConverterException("Name: Terain Converter", "Message: Invalid TerrainType from client");
		}
	}

}
