package asset;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import physics.Position;
import terrain.FortState;
import terrain.TerrainType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement()
public class ClientHalfMapGenerator extends ClientHalfMap {
	private final double grassRandom = 0.4;
	private final double mountainRandom = 0.9;
	private final double waterRandom = 1;
	private static Logger logger = LoggerFactory.getLogger(ClientHalfMapGenerator.class);

	public ClientHalfMapGenerator(Map<Position, ClientMapNode> map) {
		super(map);
		generateMap();
		placeFort();
	}

	public ClientHalfMapGenerator() {
		super();
	}

	/*
	 * generates the map through setting a position and node in puts it in the
	 * HashMap<Position, ClientMapNode>
	 */

	public void generateMap() {
		Random random = new Random();
		for (int x = 0; x < super.getWidth(); ++x) {
			for (int y = 0; y < super.getHeight(); ++y) {
				Position position = new Position(x, y);
				float randomNumber = random.nextFloat();
				ClientMapNode node = this.generateClientMapNode(randomNumber);
				super.clientMap.put(position, node);
			}
		}
		logger.info("Number of HalfMap fields are {} !", super.clientMap.size());
	}
	/*
	 * generates the ClientMapNode through setting a terrainType on the position key
	 */

	private ClientMapNode generateClientMapNode(float random) {
		ClientMapNode node = new ClientMapNode();

		if (random < grassRandom) {
			node.setTerrain(TerrainType.GRASS);
		} else if (random < mountainRandom) {
			node.setTerrain(TerrainType.MOUNTAIN);
		} else if (random < waterRandom) {
			node.setTerrain(TerrainType.WATER);
		}

		return node;
	}

	/*
	 * filters the grassFields of the Map and changes it to a list -> generate a
	 * random number to pick a random entry of the list
	 * 
	 */
	private void placeFort() {
		Random random = new Random();
		List<Entry<Position, ClientMapNode>> grassFields = super.clientMap.entrySet().stream()
				.filter(entry -> entry.getValue().getTerrain() == TerrainType.GRASS).collect(Collectors.toList());
		int x = random.nextInt(grassFields.size());
		Entry<Position, ClientMapNode> fort = grassFields.get(x);
		super.clientMap.get(fort.getKey()).setFortState(FortState.MyFortPresent);
		super.fortPosition = fort.getKey();
	}
}
