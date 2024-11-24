package physics;

import java.util.Collection;
import java.util.stream.Collectors;

import asset.GameMap;
import terrain.FortDirection;
import terrain.TerrainType;

public abstract class AbstractMapSideEvaluator {
	FortDirection fortSide;
	protected final int HALF_WIDTH_HORIZONTAL = 9;
	protected final int MAX_WIDTH_HORIZONTAL = 19;
	protected final int MAX_HEIGHT_HORIZONTAL = 5;
	protected final int MAX_HEIGHT_VERTICAL = 9;
	protected final int MAX_WIDTH_VERTICAL = 9;
	protected final int HALF_HEIGHT_VERTICAL = 5;

	public AbstractMapSideEvaluator(GameMap fullMap) {
		this.evaluateHalfMapSide(fullMap);
	}

	// To Do:
	// - private int halfheightTop = 5;
	// - private int halfwidth;
	// - private int
	// - private final MAX_WIDTH_HORIZONTAL = 19;
	// - private final MAX_HEIGHT_HORIZONTAL = 4;
	// - private int MAX_HEIGHT_VERTICAL = 9;
	// - private int MAX_WIDTH_VERTICAL = 9;
	// - make the filteTerrainPositionOfHalfMap abstract

	protected void evaluateHalfMapSide(GameMap fullMap) {
		if (fullMap.getWidth() == MAX_WIDTH_HORIZONTAL) {
			if (fullMap.getFort().getCoordinateX() <= HALF_WIDTH_HORIZONTAL) {
				this.fortSide = FortDirection.LEFTSIDE;// leftSide
			} else {
				this.fortSide = FortDirection.RIGHTSIDE;// rightside
			}
		} else if (fullMap.getHeight() == MAX_HEIGHT_VERTICAL) {
			if (fullMap.getFort().getCoordinateY() < HALF_HEIGHT_VERTICAL) {
				this.fortSide = FortDirection.TOP;
			} else {
				this.fortSide = FortDirection.BOTTOM;
			}
		}

	}

	protected Collection<Position> filterTerrainPositionOfHalfMap(GameMap fullMap, TerrainType terrain) {
		switch (this.fortSide) {
		case RIGHTSIDE:
			return fullMap.getMap().keySet().stream()
					.filter(entry -> fullMap.getMap().get(entry).getTerrain() == terrain
							&& entry.getCoordinateX() > this.HALF_WIDTH_HORIZONTAL)
					.collect(Collectors.toSet());
		case LEFTSIDE:
			return fullMap.getMap().keySet().stream()
					.filter(entry -> fullMap.getMap().get(entry).getTerrain() == terrain
							&& entry.getCoordinateX() <= this.MAX_WIDTH_HORIZONTAL)
					.collect(Collectors.toSet());
		case BOTTOM:
			return fullMap.getMap().keySet().stream()
					.filter(entry -> fullMap.getMap().get(entry).getTerrain() == terrain
							&& entry.getCoordinateY() >= this.HALF_HEIGHT_VERTICAL)
					.collect(Collectors.toSet());
		case TOP:
			return fullMap.getMap().keySet().stream()
					.filter(entry -> fullMap.getMap().get(entry).getTerrain() == terrain
							&& entry.getCoordinateY() < this.HALF_HEIGHT_VERTICAL)
					.collect(Collectors.toSet());
		default:
			throw new IllegalArgumentException("We dont know where our side of the map is");
		}
	}

}
