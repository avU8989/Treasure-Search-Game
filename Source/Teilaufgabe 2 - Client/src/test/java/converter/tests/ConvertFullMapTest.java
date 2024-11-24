package converter.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import asset.ClientMapNode;
import asset.GameMap;
import components.PlayerFigure;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import physics.Position;
import terrain.FortState;
import terrain.PositionState;
import terrain.TerrainType;
import terrain.TreasureState;

public class ConvertFullMapTest {
	@Test
	public void playerPositionChanged_convert_newPlayerPosition() {
		// arrange
		Position assertNewPos = new Position(9, 4);
		ClientMapNode assertNewNode = new ClientMapNode(TerrainType.GRASS);

		Map<Position, ClientMapNode> map = new HashMap<>();
		GameMap testMap = new GameMap(map);
		PlayerFigure figure = new PlayerFigure("vua36", new Position(8, 4), new ClientMapNode(TerrainType.MOUNTAIN));
		testMap.setPlayerFigure(figure);
		Set<FullMapNode> nodes = new HashSet<>();
		nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.MyPlayerPosition,
				ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, 9, 4));
		FullMap serverMap = new FullMap(nodes);

		// act
		testMap.convertFromNetwork(serverMap);

		// assert
		assertEquals(assertNewPos, testMap.getFigure().getPosition());
		assertEquals(assertNewNode, testMap.getMap().get(testMap.getFigure().getPosition()));
		assertEquals(TreasureState.NoOrUnknownTreasureState, testMap.getMap().get(assertNewPos).getTreasureState());
		assertEquals(FortState.NoOrUnknownFortState, testMap.getMap().get(assertNewPos).getFortState());

	}

	@Test
	public void MyFortPresent_convert_FortLocation() {
		// arrange
		Position assertMyFortLocation = new Position(9, 4);

		Map<Position, ClientMapNode> map = new HashMap<>();
		GameMap testMap = new GameMap(map);
		Set<FullMapNode> nodes = new HashSet<>();
		nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
				ETreasureState.NoOrUnknownTreasureState, EFortState.MyFortPresent, 9, 4));
		FullMap serverMap = new FullMap(nodes);

		// act
		testMap.convertFromNetwork(serverMap);

		// assert
		assertEquals(assertMyFortLocation, testMap.getFort());
		assertEquals(FortState.MyFortPresent, testMap.getMap().get(testMap.getFort()).getFortState());

	}

	@Test
	public void EnemyFortPresent_convert_EnemyFortLocation() {
		Position assertEnemyFortLocation = new Position(9, 4);

		Map<Position, ClientMapNode> map = new HashMap<>();
		GameMap testMap = new GameMap(map);
		Set<FullMapNode> nodes = new HashSet<>();
		nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
				ETreasureState.NoOrUnknownTreasureState, EFortState.EnemyFortPresent, 9, 4));
		FullMap serverMap = new FullMap(nodes);

		testMap.convertFromNetwork(serverMap);

		assertEquals(assertEnemyFortLocation, testMap.getEnemyFort());
		assertEquals(FortState.EnemyFortPresent, testMap.getMap().get(testMap.getEnemyFort()).getFortState());

	}

	@Test
	public void TreasurePresent_convert_TreasureLocation() {
		Position assertTreasureLocation = new Position(9, 4);
		Map<Position, ClientMapNode> map = new HashMap<>();
		GameMap testMap = new GameMap(map);
		Set<FullMapNode> nodes = new HashSet<>();
		nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
				ETreasureState.MyTreasureIsPresent, EFortState.NoOrUnknownFortState, 9, 4));
		FullMap serverMap = new FullMap(nodes);

		testMap.convertFromNetwork(serverMap);

		assertEquals(assertTreasureLocation, testMap.getTreasure());
		assertEquals(TreasureState.MyTreasurePresent, testMap.getMap().get(testMap.getTreasure()).getTreasureState());

	}

	@Test
	public void EnemyPosition_convert_EnemyLocation() {
		Position assertEnemyLocation = new Position(9, 4);
		Map<Position, ClientMapNode> map = new HashMap<>();
		GameMap testMap = new GameMap(map);
		Set<FullMapNode> nodes = new HashSet<>();
		nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.EnemyPlayerPosition,
				ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, 9, 4));
		FullMap serverMap = new FullMap(nodes);

		testMap.convertFromNetwork(serverMap);

		assertEquals(PositionState.EnemyPlayerPosition, testMap.getMap().get(assertEnemyLocation).getPositionState());

	}

	@Test
	public void BothPlayerPosition_convert_BothPlayerLocation() {
		Position assertBothPlayerLocation = new Position(9, 4);
		Map<Position, ClientMapNode> map = new HashMap<>();
		GameMap testMap = new GameMap(map);
		PlayerFigure figure = new PlayerFigure("vua36", new Position(8, 4), new ClientMapNode(TerrainType.MOUNTAIN));
		testMap.setPlayerFigure(figure);
		Set<FullMapNode> nodes = new HashSet<>();
		nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.BothPlayerPosition,
				ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, 9, 4));
		FullMap serverMap = new FullMap(nodes);

		// act
		testMap.convertFromNetwork(serverMap);

		assertEquals(PositionState.BothPlayerPosition,
				testMap.getMap().get(assertBothPlayerLocation).getPositionState());
		assertEquals(PositionState.BothPlayerPosition,
				testMap.getMap().get(testMap.getFigure().getPosition()).getPositionState());
		assertEquals(assertBothPlayerLocation, testMap.getFigure().getPosition());
	}

	@Test
	public void ETerrain_convert_ClientTerainType() {
		Position assertGrass = new Position(9, 4);
		Position assertWater = new Position(10, 4);
		Position assertMountain = new Position(11, 4);
		Map<Position, ClientMapNode> map = new HashMap<>();
		GameMap testMap = new GameMap(map);
		Set<FullMapNode> nodes = new HashSet<>();
		nodes.add(new FullMapNode(ETerrain.Grass, EPlayerPositionState.NoPlayerPresent,
				ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, 9, 4));
		nodes.add(new FullMapNode(ETerrain.Water, EPlayerPositionState.NoPlayerPresent,
				ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, 10, 4));
		nodes.add(new FullMapNode(ETerrain.Mountain, EPlayerPositionState.NoPlayerPresent,
				ETreasureState.NoOrUnknownTreasureState, EFortState.NoOrUnknownFortState, 11, 4));
		FullMap serverMap = new FullMap(nodes);

		// act
		testMap.convertFromNetwork(serverMap);

		assertEquals(TerrainType.GRASS, testMap.getMap().get(assertGrass).getTerrain());
		assertEquals(TerrainType.WATER, testMap.getMap().get(assertWater).getTerrain());
		assertEquals(TerrainType.MOUNTAIN, testMap.getMap().get(assertMountain).getTerrain());
	}

}
