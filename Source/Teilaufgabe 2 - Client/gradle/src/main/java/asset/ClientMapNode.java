package asset;

import terrain.FortState;
import terrain.TerrainType;
import terrain.TreasureState;

public class ClientMapNode {
	private FortState fortState;
	private TerrainType terrainField;
	private TreasureState treasureState;

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String FORTINITE = "\u001B[1;42;37m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	public ClientMapNode() {
		this.fortState = FortState.NoOrUnknownFortState;
		this.treasureState = TreasureState.NoOrUnknownTreasureState;
	}

	public ClientMapNode(TerrainType terrain) {
		this.fortState = FortState.NoOrUnknownFortState;
		this.treasureState = TreasureState.NoOrUnknownTreasureState;
		this.terrainField = terrain;
	}

	public ClientMapNode(TerrainType terrainField, FortState fort) {
		this.terrainField = terrainField;
		this.fortState = fort;
	}

	public ClientMapNode(TerrainType terrainField, FortState fort, TreasureState treasure) {
		this.fortState = fort;
		this.terrainField = terrainField;
		this.treasureState = treasure;
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

	@Override
	public String toString() {
		String ret = null;

		if (terrainField == TerrainType.GRASS) {
			ret = ANSI_GREEN_BACKGROUND + "  " + ANSI_RESET;
		}

		if (terrainField == TerrainType.MOUNTAIN) {
			ret = ANSI_WHITE_BACKGROUND + "  " + ANSI_RESET;
		}

		if (terrainField == TerrainType.WATER) {
			ret = ANSI_BLUE_BACKGROUND + "  " + ANSI_RESET;
		}

		if (fortState.equals(FortState.MyFortPresent)) {
			ret = ANSI_RED_BACKGROUND + "  " + ANSI_RESET;
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
