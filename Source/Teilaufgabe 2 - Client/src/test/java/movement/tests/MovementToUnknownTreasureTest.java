package movement.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import asset.ClientMapNode;
import asset.GameMap;
import components.PlayerFigure;
import exceptions.MovementException;
import physics.Direction;
import physics.MovementToUnknownTreasure;
import physics.Position;
import terrain.TerrainType;

public class MovementToUnknownTreasureTest {
	@Test
	public void MapOnlyGrass_whenSendDestination_thenMoveInAllDirections() throws MovementException {
		GameMap updatedMockMap = Mockito.mock(GameMap.class);
		HashMap<Position, ClientMapNode> updatedMockedGameMap = new HashMap<>();
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 20; ++j) {
				updatedMockedGameMap.put(new Position(j, i), new ClientMapNode(TerrainType.GRASS));
			}
		}

		when(updatedMockMap.getMap()).thenReturn(updatedMockedGameMap);
		Mockito.when(updatedMockMap.getFigure()).thenReturn(Mockito.mock(PlayerFigure.class));
		Mockito.when(updatedMockMap.getFigure().getPosition()).thenReturn(new Position(0, 0));
		Mockito.when(updatedMockMap.getFigure().getTreasureCollected()).thenReturn(false);
		Mockito.when(updatedMockMap.getEnemyFort()).thenReturn(null);
		Mockito.when(updatedMockMap.getFort()).thenReturn(new Position(0, 0));
		Mockito.when(updatedMockMap.getWidth()).thenReturn(19);
		Mockito.when(updatedMockMap.getHeight()).thenReturn(4);
		Mockito.when(updatedMockMap.getMaxWidthHorizontal()).thenReturn(19);

		// act
		MovementToUnknownTreasure treasureStrategy = new MovementToUnknownTreasure(updatedMockMap, new Position(0, 0));
		List<Direction> directions = treasureStrategy.sendDirection();

		assertTrue(directions.contains(Direction.RIGHT));
		assertTrue(directions.contains(Direction.DOWN));
		assertTrue(directions.contains(Direction.LEFT));
		assertTrue(directions.contains(Direction.UP));
	}

	@Test
	public void mapWithMountains_whenCheckMountainPeaks_thenPathToMountain() throws MovementException {
		// we have to move 8x in the right direction to move to the specific
		// mountainFields
		// that mountainField must have more than 3 grassFields to be visited
		GameMap updatedMockMap = Mockito.mock(GameMap.class);
		List<Direction> expectedDirections = new LinkedList<>(Arrays.asList(Direction.RIGHT, Direction.RIGHT,
				Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT));
		Queue<Position> expectedTreasureCanyon = new LinkedList<>(
				Arrays.asList(new Position(1, 0), new Position(5, 1)));
		HashMap<Position, ClientMapNode> updatedMockedGameMap = new HashMap<>();
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 20; ++j) {
				updatedMockedGameMap.put(new Position(j, i), new ClientMapNode(TerrainType.GRASS));
			}
		}

		updatedMockedGameMap.replace(new Position(2, 0), new ClientMapNode(TerrainType.MOUNTAIN));
		updatedMockedGameMap.replace(new Position(5, 0), new ClientMapNode(TerrainType.MOUNTAIN));

		when(updatedMockMap.getMap()).thenReturn(updatedMockedGameMap);
		Mockito.when(updatedMockMap.getFigure()).thenReturn(Mockito.mock(PlayerFigure.class));
		Mockito.when(updatedMockMap.getFigure().getPosition()).thenReturn(new Position(0, 0));
		Mockito.when(updatedMockMap.getFigure().getTreasureCollected()).thenReturn(false);
		Mockito.when(updatedMockMap.getEnemyFort()).thenReturn(null);
		Mockito.when(updatedMockMap.getFort()).thenReturn(new Position(0, 0));
		Mockito.when(updatedMockMap.getWidth()).thenReturn(19);
		Mockito.when(updatedMockMap.getHeight()).thenReturn(4);
		Mockito.when(updatedMockMap.getMaxWidthHorizontal()).thenReturn(19);

		// act
		MovementToUnknownTreasure treasureStrategy = new MovementToUnknownTreasure(updatedMockMap, new Position(0, 0));
		List<Direction> directions = treasureStrategy.sendDirection();
		// subList because the treasureStrategy will send after the first 8 directions
		// more directions to find the treasure
		directions = directions.subList(0, 8);

		assertEquals(expectedDirections, directions);
	}
}
