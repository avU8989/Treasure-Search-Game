package physics;

import java.util.Objects;

public class Position {
	private int coordinateX;
	private int coordinateY;
	
	public Position() {
		
	}
	
	public Position(int coordinateX, int coordinateY) {
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
	}
	
	public int getCoordinateX() {
		return this.coordinateX;
	}
	
	public int getCoordinateY() {
		return this.coordinateY;
	}
	
	public void setCoordinates(int x, int y) {
		this.coordinateX = x;
		this.coordinateY = y;
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

	@Override
	public String toString() {
		return "Position [coordinateX=" + coordinateX + ", coordinateY=" + coordinateY + "]";
	}

	
}
