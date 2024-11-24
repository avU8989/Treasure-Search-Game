package movement.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import asset.ClientMapNode;
import asset.GameMap;
import components.PlayerFigure;
import exceptions.MovementException;
import physics.Direction;
import physics.MovementLogic;
import physics.MovementStrategy;
import physics.MovementToDestination;
import physics.MovementToUnknownEnemyFort;
import physics.Position;

public class MovementLogicTest {

	@Mock
	private GameMap mockMap;

	@Mock
	private MovementStrategy mockStrategy;

	private MovementLogic movementLogic;

	@BeforeEach
	void setUp() {
		// Create mock objects
		GameMap mockMap = Mockito.mock(GameMap.class);
		PlayerFigure mockFigure = Mockito.mock(PlayerFigure.class);
		Position mockPosition = Mockito.mock(Position.class);
		HashMap<Position, ClientMapNode> mockedGameMap = new HashMap<>();

		// Stub method calls on mock objects
		when(mockMap.getFigure()).thenReturn(mockFigure);
		when(mockFigure.getPosition()).thenReturn(mockPosition);
		when(mockMap.getMap()).thenReturn(mockedGameMap);
		Mockito.when(mockMap.getFort()).thenReturn(new Position(3, 0));
		Mockito.when(mockMap.getWidth()).thenReturn(19);

		// Create the MovementLogic object with the mocked GameMap
		mockStrategy = Mockito.mock(MovementStrategy.class);
		movementLogic = new MovementLogic(mockMap, mockStrategy);

	}

	@Test
	void setVisitedFields_emptySet() {
		// Arrange
		Set<Position> visitedFields = Collections.emptySet();

		// Act
		movementLogic.setVisitedFields(visitedFields);

		// Assert - Check the expected behavior or outcome

		// Verify that a certain method of mockStrategy is called with the expected
		// arguments
		verify(mockStrategy).setVisitedFields(visitedFields);

		// Assert that the state of movementLogic is updated based on the visitedFields
		// You can use getters or other methods of movementLogic to check the expected
		// state

		// Example assertions:
		// Assert that a certain field or property of movementLogic is updated based on
		// the visitedFields
		assertEquals(Collections.emptySet(), movementLogic.getVisited());

		// Assert that a certain method of movementLogic is called with the expected
		// arguments
		verify(mockStrategy).setVisitedFields(visitedFields);

	}

	@Test
	public void TreasureAndKnownEnemyFort_setStrategy_MovementToDestination() {
		GameMap updatedMockMap = Mockito.mock(GameMap.class);
		Mockito.when(updatedMockMap.getFigure()).thenReturn(Mockito.mock(PlayerFigure.class));
		Mockito.when(updatedMockMap.getFigure().getPosition()).thenReturn(new Position(0, 0));
		Mockito.when(updatedMockMap.getFigure().getTreasureCollected()).thenReturn(true);
		Mockito.when(updatedMockMap.getEnemyFort()).thenReturn(new Position(10, 0));
		GameMap mockMap = Mockito.mock(GameMap.class);
		PlayerFigure mockFigure = Mockito.mock(PlayerFigure.class);
		Position mockPosition = Mockito.mock(Position.class);
		HashMap<Position, ClientMapNode> mockedGameMap = new HashMap<>();
		when(mockMap.getMap()).thenReturn(mockedGameMap);
		when(mockMap.getFigure()).thenReturn(mockFigure);
		when(mockFigure.getPosition()).thenReturn(mockPosition);
		Mockito.when(mockMap.getFort()).thenReturn(new Position(3, 0));
		Mockito.when(mockMap.getWidth()).thenReturn(19);
		MovementLogic movementLogic = new MovementLogic(mockMap);

		// act
		movementLogic.setStrategy(updatedMockMap);

		// Assert
		assertTrue(movementLogic.getStrategy() instanceof MovementToDestination);

	}

	@Test
	public void TreasureCollectedAndUnknownEnemyFort_setStrategy_MovementToUnknownTreasure() {
		GameMap updatedMockMap = Mockito.mock(GameMap.class);
		Mockito.when(updatedMockMap.getFigure()).thenReturn(Mockito.mock(PlayerFigure.class));
		Mockito.when(updatedMockMap.getFigure().getPosition()).thenReturn(new Position(0, 0));
		Mockito.when(updatedMockMap.getFigure().getTreasureCollected()).thenReturn(true);
		Mockito.when(updatedMockMap.getEnemyFort()).thenReturn(null);
		Mockito.when(updatedMockMap.getFort()).thenReturn(new Position(9, 4));
		Mockito.when(updatedMockMap.getWidth()).thenReturn(19);
		GameMap mockMap = Mockito.mock(GameMap.class);
		PlayerFigure mockFigure = Mockito.mock(PlayerFigure.class);
		Position mockPosition = Mockito.mock(Position.class);
		HashMap<Position, ClientMapNode> mockedGameMap = new HashMap<>();
		when(mockMap.getMap()).thenReturn(mockedGameMap);
		when(mockMap.getFigure()).thenReturn(mockFigure);
		when(mockFigure.getPosition()).thenReturn(mockPosition);
		Mockito.when(mockMap.getFort()).thenReturn(new Position(3, 0));
		Mockito.when(mockMap.getWidth()).thenReturn(19);
		MovementLogic movementLogic = new MovementLogic(mockMap);

		// act
		movementLogic.setStrategy(updatedMockMap);

		// Assert
		assertTrue(movementLogic.getStrategy() instanceof MovementToUnknownEnemyFort);

	}

	@Test
	public void TreasureNotCollected_setStrategy_MovementToUnknownTreasure() {
		GameMap updatedMockMap = Mockito.mock(GameMap.class);
		HashMap<Position, ClientMapNode> updatedMockedGameMap = new HashMap<>();
		when(updatedMockMap.getMap()).thenReturn(updatedMockedGameMap);
		Mockito.when(updatedMockMap.getFigure()).thenReturn(Mockito.mock(PlayerFigure.class));
		Mockito.when(updatedMockMap.getFigure().getPosition()).thenReturn(new Position(0, 0));
		Mockito.when(updatedMockMap.getFigure().getTreasureCollected()).thenReturn(false);
		Mockito.when(updatedMockMap.getEnemyFort()).thenReturn(null);
		Mockito.when(updatedMockMap.getTreasure()).thenReturn(new Position(5, 2));
		GameMap mockMap = Mockito.mock(GameMap.class);
		PlayerFigure mockFigure = Mockito.mock(PlayerFigure.class);
		Position mockPosition = Mockito.mock(Position.class);
		HashMap<Position, ClientMapNode> mockedGameMap = new HashMap<>();
		when(mockMap.getMap()).thenReturn(mockedGameMap);
		when(mockMap.getFigure()).thenReturn(mockFigure);
		when(mockFigure.getPosition()).thenReturn(mockPosition);
		Mockito.when(mockMap.getFort()).thenReturn(new Position(3, 0));
		Mockito.when(mockMap.getWidth()).thenReturn(19);
		MovementLogic movementLogic = new MovementLogic(mockMap);

		// act
		movementLogic.setStrategy(updatedMockMap);

		// Assert
		assertTrue(movementLogic.getStrategy() instanceof MovementToDestination);

	}

	@Test
	public void sendDirectionEmptyTest() throws MovementException {

		Mockito.when(mockStrategy.sendDirection()).thenReturn(Collections.emptyList());
		assertThrows(MovementException.class, () -> {
			movementLogic.sendDirection(); // should throw exception since directions list is empty
		});
	}

	@Test
	public void startMovingTest() {
		List<Direction> directions = Arrays.asList(Direction.UP, Direction.DOWN, Direction.RIGHT, Direction.LEFT);
		Mockito.when(mockStrategy.sendDirection()).thenReturn(directions);

		movementLogic.startMoving();

		assertEquals(directions, movementLogic.getDirections());

	}

}
