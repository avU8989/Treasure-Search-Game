package converter.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import asset.ClientHalfMap;
import asset.ClientMapNode;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import physics.Position;
import terrain.FortState;
import terrain.TerrainType;

public class ConvertHalfMapTest {
	@Test
	public void ClientHalfMap_convertToServer_TerrainTypeCheck() {
		Position assertGrass = new Position(9, 4);
		Position assertMountain = new Position(10, 4);
		Position assertWater = new Position(11, 4);
		HashMap<Position, ClientMapNode> halfMap = new HashMap<>();
		Set<PlayerHalfMapNode> actualServerMap = new HashSet<>();
		actualServerMap.add(new PlayerHalfMapNode(9, 4, ETerrain.Grass));
		actualServerMap.add(new PlayerHalfMapNode(10, 4, ETerrain.Mountain));
		actualServerMap.add(new PlayerHalfMapNode(11, 4, ETerrain.Water));
		halfMap.put(assertGrass, new ClientMapNode(TerrainType.GRASS));
		halfMap.put(assertMountain, new ClientMapNode(TerrainType.MOUNTAIN));
		halfMap.put(assertWater, new ClientMapNode(TerrainType.WATER));
		ClientHalfMap clientMap = new ClientHalfMap(halfMap);

		Set<PlayerHalfMapNode> expectedServerMap = new HashSet<>(clientMap.convertToNetwork(halfMap));

		assertEquals(actualServerMap, expectedServerMap);

	}

	@Test
	public void ClientHalfMap_convertToServer_FortCheck() {
		Position assertFort = new Position(9, 4);
		HashMap<Position, ClientMapNode> halfMap = new HashMap<>();
		Set<PlayerHalfMapNode> falseServerMap = new HashSet<>();
		falseServerMap.add(new PlayerHalfMapNode(9, 4, false, ETerrain.Grass));
		halfMap.put(assertFort, new ClientMapNode(TerrainType.GRASS, FortState.MyFortPresent));
		ClientHalfMap clientMap = new ClientHalfMap(halfMap);

		Set<PlayerHalfMapNode> expectedServerMap = new HashSet<>(clientMap.convertToNetwork(halfMap));

		assertFalse(expectedServerMap.equals(falseServerMap));
	}
}
