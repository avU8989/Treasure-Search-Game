package map.tests;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import asset.ClientHalfMapGenerator;
import asset.ClientHalfMapValidator;
import asset.ClientMapNode;
import exceptions.ClientMapValidatorException;
import physics.Position;
import terrain.FortState;
import terrain.TerrainType;
import terrain.TreasureState;

public class ClientHalfMapValidatorTest {

	@Test
	public void whenFloodFill_replacingAdjacentTiles_checkTerrain() {
		ClientHalfMapGenerator generator = new ClientHalfMapGenerator(new HashMap<>());

		Position position = new Position(2, 2);
		generator.getMap().get(position).setTerrain(TerrainType.MOUNTAIN);
		generator.getMap().get(new Position(position.getCoordinateX() - 1, position.getCoordinateY()))
				.setTerrain(TerrainType.WATER);
		generator.getMap().get(new Position(position.getCoordinateX() + 1, position.getCoordinateY()))
				.setTerrain(TerrainType.WATER);
		generator.getMap().get(new Position(position.getCoordinateX(), position.getCoordinateY() - 1))
				.setTerrain(TerrainType.WATER);
		generator.getMap().get(new Position(position.getCoordinateX(), position.getCoordinateY() + 1))
				.setTerrain(TerrainType.WATER);

		ClientHalfMapValidator validator = new ClientHalfMapValidator(generator.getMap(), generator.getFort());

		// Verify the position and its surrounding tiles
		assertTrue(validator.getMap().get(position).getTerrain() == TerrainType.MOUNTAIN);
		assertTrue(validator.getMap().get(new Position(position.getCoordinateX() - 1, position.getCoordinateY()))
				.getTerrain() == TerrainType.GRASS);

		assertTrue(validator.getMap().get(new Position(position.getCoordinateX() + 1, position.getCoordinateY()))
				.getTerrain() == TerrainType.GRASS);

		assertTrue(validator.getMap().get(new Position(position.getCoordinateX(), position.getCoordinateY() - 1))
				.getTerrain() == TerrainType.GRASS);

		assertTrue(validator.getMap().get(new Position(position.getCoordinateX(), position.getCoordinateY() + 1))
				.getTerrain() == TerrainType.GRASS);

	}

	@Test
	public void requiredTerrains_repairMap_checkTerrainTypesFullfilled() throws ClientMapValidatorException {
		// arrange
		ClientHalfMapGenerator generator = new ClientHalfMapGenerator(new HashMap<>());

		long requiredWater = 7;
		long requiredMountain = 5;
		long requiredGrass = 24;

		// set current number of terrainTypes
		long currentGrass = generator.getMap().values().stream()
				.filter(entry -> entry.getTerrain() == TerrainType.GRASS).count();
		long currentMountain = generator.getMap().values().stream()
				.filter(entry -> entry.getTerrain() == TerrainType.MOUNTAIN).count();
		long currentWater = generator.getMap().values().stream()
				.filter(entry -> entry.getTerrain() == TerrainType.WATER).count();

		ClientHalfMapValidator validator = new ClientHalfMapValidator(generator.getMap(), generator.getFort());

		long newGrass = validator.getMap().values().stream().filter(entry -> entry.getTerrain() == TerrainType.GRASS)
				.count();
		long newMountain = validator.getMap().values().stream()
				.filter(entry -> entry.getTerrain() == TerrainType.MOUNTAIN).count();
		long newWater = validator.getMap().values().stream().filter(entry -> entry.getTerrain() == TerrainType.WATER)
				.count();

		// assert
		assertTrue(requiredWater <= newWater);
		assertTrue(requiredMountain <= newMountain);
		assertTrue(requiredGrass <= newGrass);

		assertNotEquals(currentGrass, newGrass);
		assertNotEquals(currentMountain, newMountain);
		assertNotEquals(currentWater, newWater);

	}

	@Test
	public void mapBorderWater_repairLong_checkRepairment() throws ClientMapValidatorException {
		ClientHalfMapGenerator generator = new ClientHalfMapGenerator(new HashMap<>());

		generator.getMap().get(new Position(1, 0)).setTerrain(TerrainType.WATER);
		generator.getMap().get(new Position(2, 0)).setTerrain(TerrainType.WATER);
		generator.getMap().get(new Position(3, 0)).setTerrain(TerrainType.WATER);
		generator.getMap().get(new Position(4, 0)).setTerrain(TerrainType.WATER);
		generator.getMap().get(new Position(5, 0)).setTerrain(TerrainType.WATER);

		ClientHalfMapValidator validator = new ClientHalfMapValidator(generator.getMap(), generator.getFort());

		// Verify the repairment of the long map border
		boolean isCorrectTerrain = false;

		for (int x = 1; x <= 5; ++x) {
			if (validator.getMap().get(new Position(x, 0)).getTerrain() == TerrainType.GRASS
					|| validator.getMap().get(new Position(x, 0)).getTerrain() == TerrainType.MOUNTAIN) {
				isCorrectTerrain = true;
				break; // Found a valid terrain, no need to continue the loop
			}
		}

		assertTrue(isCorrectTerrain);

	}

	@Test
	public void mapBorderWater_repairShort_checkRepairment() throws ClientMapValidatorException {
		ClientHalfMapGenerator generator = new ClientHalfMapGenerator(new HashMap<>());
		generator.getMap().get(new Position(0, 0)).setTerrain(TerrainType.WATER);
		generator.getMap().get(new Position(0, 1)).setTerrain(TerrainType.WATER);
		generator.getMap().get(new Position(0, 2)).setTerrain(TerrainType.WATER);

		ClientHalfMapValidator validator = new ClientHalfMapValidator(generator.getMap(), generator.getFort());

		// Verify the repairment of the short map border
		boolean isCorrectTerrain = false;
		for (int x = 1; x <= 3; ++x) {
			if (validator.getMap().get(new Position(x, 0)).getTerrain() == TerrainType.GRASS
					|| validator.getMap().get(new Position(x, 0)).getTerrain() == TerrainType.MOUNTAIN) {
				isCorrectTerrain = true;
				break; // Found a valid terrain, no need to continue the loop
			}
		}

		assertTrue(isCorrectTerrain);
	}

	@Test
	public void testValidateFort_RepairFort_FortTreasureBothSameField() {
		// Create a map with a valid position for the fort
		ClientHalfMapGenerator generator = new ClientHalfMapGenerator(new HashMap<>());
		Position validFortPosition = new Position(2, 2);
		ClientMapNode node = new ClientMapNode(TerrainType.GRASS);
		node.setFortState(FortState.MyFortPresent);
		node.setTreasureState(TreasureState.MyTreasurePresent);
		generator.getMap().put(validFortPosition, node);

		// Create the ClientHalfMapValidator instance
		ClientHalfMapValidator validator = new ClientHalfMapValidator(generator.getMap(), validFortPosition);

		assertTrue(!(validator.getMap().get(validator.getFort()).getTreasureState() == TreasureState.MyTreasurePresent
				&& validator.getMap().get(validator.getFort()).getFortState() == FortState.MyFortPresent));

	}

	@Test
	public void testValidateFort_RepairFort_FortNotInvalidField() {
		ClientHalfMapGenerator generator = new ClientHalfMapGenerator(new HashMap<>());
		Position invalidFortPosition = new Position(3, 3);
		ClientMapNode node = new ClientMapNode(TerrainType.MOUNTAIN);
		node.setFortState(FortState.MyFortPresent);
		generator.getMap().put(invalidFortPosition, node);

		// Create the ClientHalfMapValidator instance
		ClientHalfMapValidator validator = new ClientHalfMapValidator(generator.getMap(), invalidFortPosition);

		assertTrue(validator.getMap().get(validator.getFort()).getTerrain() == TerrainType.GRASS
				&& validator.getMap().get(validator.getFort()).getFortState() == FortState.MyFortPresent);
	}
}
