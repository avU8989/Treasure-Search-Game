package gameMap;

import java.util.Objects;

public class Position {
	private int coordinateX;
	private int coordinateY;

	public Position(int coordinateX, int coordinateY) {
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
	}

	@Override
	public String toString() {
		String position = "Position";
		return position + "[" + coordinateX + "," + coordinateY + "]";
	}

	public int getCoordinateX() {
		return this.coordinateX;
	}

	public int getCoordinateY() {
		return this.coordinateY;
	}

	@Override
	public int hashCode() {
		return Objects.hash(coordinateX, coordinateY);
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
		Position other = (Position) obj;
		return coordinateX == other.coordinateX && coordinateY == other.coordinateY;
	}
}
