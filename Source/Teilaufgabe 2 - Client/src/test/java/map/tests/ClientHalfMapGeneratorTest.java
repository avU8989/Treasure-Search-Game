package map.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import asset.ClientHalfMapGenerator;
import asset.ClientMapNode;
import physics.Position;
import terrain.FortState;
import terrain.TerrainType;

public class ClientHalfMapGeneratorTest {

	@Test
	public void whenGenerateMap_thenCorrectNumberOfNodesWithTerrain() {
		ClientHalfMapGenerator generator = new ClientHalfMapGenerator();
		int expectedSize = generator.getWidth() * generator.getHeight();

		generator.generateMap();

		assertEquals(expectedSize, generator.getMap().size());

		for (ClientMapNode node : generator.getMap().values()) {
			assertNotNull(node.getTerrain());
		}

	}

	@Test
	public void whenPlaceFort_thenFortPlacement_GrassTerrain() {
		ClientHalfMapGenerator generator = new ClientHalfMapGenerator(new HashMap<>());
		Position fortPos = generator.getFort();
		ClientMapNode fortNode = generator.getMap().get(fortPos);

		assertEquals(TerrainType.GRASS, fortNode.getTerrain());
		assertEquals(FortState.MyFortPresent, fortNode.getFortState());

	}

	@Test
	public void whenPlaceFort_thenFortPlacement_Single() {
		ClientHalfMapGenerator generator = new ClientHalfMapGenerator(new HashMap<>());

		long fortCount = generator.getMap().values().stream()
				.filter(entry -> entry.getFortState() == FortState.MyFortPresent).count();

		assertEquals(1, fortCount);
	}

}
