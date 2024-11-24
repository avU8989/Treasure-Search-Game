package asset;

import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import physics.Position;
import terrain.FortState;
import terrain.TerrainType;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMapNode;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement()
public class ClientHalfMapGenerator extends ClientHalfMap {
	public ClientHalfMapGenerator(HashMap<Position, ClientMapNode> map) {
		super(map);
		generateMap();
		placeFort();
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
	}
	/*
	 * generates the ClientMapNode through setting a terrainType on the position key
	 */

	private ClientMapNode generateClientMapNode(float random) {
		ClientMapNode node = new ClientMapNode();

		if (random < 0.5) {
			node.setTerrain(TerrainType.GRASS);
		} else if (random < 0.8) {
			node.setTerrain(TerrainType.WATER);
		} else if (random < 1) {
			node.setTerrain(TerrainType.MOUNTAIN);
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
				.filter(entry -> entry.getValue().getTerrain() == TerrainType.GRASS && entry.getValue() != null)
				.collect(Collectors.toList());
		int x = random.nextInt(grassFields.size());
		Entry<Position, ClientMapNode> fort = grassFields.get(x);
		super.clientMap.get(fort.getKey()).setFortState(FortState.MyFortPresent);
		super.fortPosition = fort.getKey();
	}
}
