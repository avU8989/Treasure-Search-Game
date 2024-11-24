package gameMap;

;

public class GameMapNode {
	private final FortState fortState;
	private final TerrainType terrainField;
	private final TreasureState treasureState;
	private final PositionState positionState;

	public GameMapNode(TerrainType terrain, FortState fortState) {
		this.terrainField = terrain;
		this.fortState = fortState;
		this.treasureState = TreasureState.NoOrUnknownTreasureState;
		this.positionState = PositionState.NoPlayerPresent;
	}

	public GameMapNode(TerrainType terrain, FortState fortState, PositionState positionState) {
		this.terrainField = terrain;
		this.fortState = fortState;
		this.treasureState = TreasureState.NoOrUnknownTreasureState;
		this.positionState = positionState;
	}

	public GameMapNode(TerrainType terrain, PositionState positionState, TreasureState treasureState,
			FortState fortState) {
		this.terrainField = terrain;
		this.positionState = positionState;
		this.treasureState = treasureState;
		this.fortState = fortState;
	}

	public FortState getFortState() {
		return this.fortState;
	}

	public TerrainType getTerrain() {
		return this.terrainField;
	}

	public TreasureState getTreasureState() {
		return this.treasureState;
	}

	public PositionState getPositionState() {
		return this.positionState;
	}

	@Override
	public String toString() {
		String ret = null;
		if (terrainField == TerrainType.GRASS) {
			ret = "|G|";
		} else if (terrainField == TerrainType.MOUNTAIN) {
			ret = "|M|";
		} else if (terrainField == TerrainType.WATER) {
			ret = "|W|";
		}

		if (fortState == FortState.MyFortPresent) {
			ret = "|F|";
		} else if (fortState == FortState.EnemyFortPresent) {
			ret = "|E|";
		}

		if (positionState == PositionState.MyPlayerPosition) {
			ret = "\u001B[31m" + "|O|" + "\u001B[0m";
		} else if (positionState == PositionState.EnemyPlayerPosition) {
			ret = "\u001B[36m" + "|X|" + "\u001B[0m";
		} else if (positionState == PositionState.BothPlayerPosition) {
			ret = "|B|";
		}

		if (treasureState == TreasureState.MyTreasurePresent) {
			ret = "\u001B[33m" + "|T|" + "\u001B[0m";
		}

		return ret;

	}


}
