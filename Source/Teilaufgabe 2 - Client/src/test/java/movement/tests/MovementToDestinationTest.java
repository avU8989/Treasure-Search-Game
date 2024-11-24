package movement.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import asset.ClientMapNode;
import asset.GameMap;
import components.PlayerFigure;
import exceptions.MovementException;
import physics.Direction;
import physics.MovementToDestination;
import physics.PathNode;
import physics.Position;
import terrain.TerrainType;
import terrain.TreasureState;

public class MovementToDestinationTest {
	@Test
	public void TreasureLocated_StartMoving_TowardsTreasure() throws MovementException {
		GameMap updatedMockMap = Mockito.mock(GameMap.class);
		List<Direction> expectedDirection = new LinkedList<>(
				Arrays.asList(Direction.RIGHT, Direction.RIGHT, Direction.DOWN, Direction.DOWN, Direction.DOWN,
						Direction.DOWN, Direction.DOWN, Direction.DOWN, Direction.DOWN, Direction.DOWN));
		List<Direction> expectedDirection2 = new LinkedList<>(
				Arrays.asList(Direction.DOWN, Direction.DOWN, Direction.DOWN, Direction.DOWN, Direction.DOWN,
						Direction.DOWN, Direction.DOWN, Direction.DOWN, Direction.RIGHT, Direction.RIGHT));

		HashMap<Position, ClientMapNode> updatedMockedGameMap = new HashMap<>();
		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 20; ++j) {
				updatedMockedGameMap.put(new Position(j, i), new ClientMapNode(TerrainType.GRASS));
			}
		}
		ClientMapNode node = new ClientMapNode(TerrainType.GRASS);
		node.setTreasureState(TreasureState.MyTreasurePresent);
		updatedMockedGameMap.replace(new Position(1, 4), node);

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
		MovementToDestination treasureStrategy = new MovementToDestination(updatedMockMap,
				new PathNode(new Position(0, 0)), new PathNode(new Position(1, 4)));
		List<Direction> directions = treasureStrategy.sendDirection();
		assertTrue(expectedDirection.equals(directions) || expectedDirection2.equals(directions));

	}
}
