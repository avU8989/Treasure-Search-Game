package terrain;

import messagesbase.messagesfromclient.ETerrain;

public enum TerrainType{
	WATER, 
	GRASS,
	MOUNTAIN;
	
	public static ETerrain convertTerrainToNetwork(TerrainType terrain) {
		return ETerrain.values()[terrain.ordinal()];
	}
	
	public static TerrainType convertTerrainFromNetwork(ETerrain terrain) {
		return TerrainType.values()[terrain.ordinal()];
	}
	

}
