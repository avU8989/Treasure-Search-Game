package terrain;

import exceptions.ConvertTerrainException;
import messagesbase.messagesfromclient.ETerrain;
import network.ConvertFromNetwork;
import network.ConvertToNetwork;

public enum TerrainType implements ConvertFromNetwork<TerrainType, ETerrain>, ConvertToNetwork<ETerrain, TerrainType> {
	WATER, GRASS, MOUNTAIN;

	@Override
	public TerrainType convertFromNetwork(ETerrain target) {
		if (target == null) {
			throw new ConvertTerrainException("TerrainType from server is null");
		}
		switch (target) {
		case Grass:
			return TerrainType.GRASS;
		case Mountain:
			return TerrainType.MOUNTAIN;
		case Water:
			return TerrainType.WATER;
		default:
			throw new ConvertTerrainException("Invalid TerrainType from server");
		}
	}

	@Override
	public ETerrain convertToNetwork(TerrainType source) {
		if (source == null) {
			throw new ConvertTerrainException("Invalid terrainType to server");
		}
		switch (source) {
		case GRASS:
			return ETerrain.Grass;
		case MOUNTAIN:
			return ETerrain.Mountain;
		case WATER:
			return ETerrain.Water;
		default:
			throw new ConvertTerrainException("Invalid TerrainType from client");
		}
	}

}
