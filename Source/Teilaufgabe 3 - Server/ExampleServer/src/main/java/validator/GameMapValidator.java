package validator;

public interface GameMapValidator {
	public boolean checkMapSize();

	public boolean requireMultipleTerrainType();

	public boolean checkFortPlacement();

	public boolean islandPresent();

	public boolean checkMapBorderForWater();

	public boolean checkFortPosition();
}
