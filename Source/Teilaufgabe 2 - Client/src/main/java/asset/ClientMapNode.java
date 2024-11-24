package asset;

import terrain.FortState;
import terrain.PositionState;
import terrain.TerrainType;
import terrain.TreasureState;

public class ClientMapNode {
	private FortState fortState;
	private TerrainType terrainField;
	private TreasureState treasureState;
	private PositionState positionState;

	public ClientMapNode() {
		this.fortState = FortState.NoOrUnknownFortState;
		this.treasureState = TreasureState.NoOrUnknownTreasureState;
	}

	public ClientMapNode(TerrainType terrainField) {
		this.fortState = FortState.NoOrUnknownFortState;
		this.treasureState = TreasureState.NoOrUnknownTreasureState;
		this.terrainField = terrainField;
	}

	public ClientMapNode(TerrainType terrainField, FortState myFortPresent) {
		this(terrainField);
		this.fortState = myFortPresent;
	}

	public void setTerrain(TerrainType terrainField) {
		this.terrainField = terrainField;
	}

	public void setFortState(FortState fort) {
		this.fortState = fort;
	}

	public void setTreasureState(TreasureState treasure) {
		this.treasureState = treasure;
	}

	public void setPositionState(PositionState state) {
		this.positionState = state;
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

	public TerrainType getTerrain() {
		return this.terrainField;
	}

	public FortState getFortState() {
		return this.fortState;
	}

	public TreasureState getTreasureState() {
		return this.treasureState;
	}

	public PositionState getPositionState() {
		return this.positionState;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ClientMapNode other = (ClientMapNode) obj;
		return fortState == other.fortState && terrainField == other.terrainField
				&& treasureState == other.treasureState;
	}

}
