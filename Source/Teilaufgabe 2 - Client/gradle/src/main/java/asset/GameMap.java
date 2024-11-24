package asset;

import java.util.HashMap;
import java.util.Map;

import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import network.ConvertFromNetwork;
import physics.Position;
import terrain.FortState;
import terrain.TerrainType;
import terrain.TreasureState;

public class GameMap implements ConvertFromNetwork<FullMap> {
	Map<Position, ClientMapNode> fullMap;
	private final int MAX_WIDTH = 19;
	private final int MAX_HEIGHT = 9;
	private int x;
	private int y;

	public GameMap() {
		this.fullMap = new HashMap<Position, ClientMapNode>();
	}

	public GameMap(Map<Position, ClientMapNode> fullMap) {
		this.fullMap = new HashMap<Position, ClientMapNode>(fullMap);
	}

	@Override
	public void convertFromNetwork(FullMap target) {
		// set x the highest position in x from the FullMapNode of the Server
		// set y the highest position in y from the FullMapNode of the Server
		for (var entry : target.getMapNodes()) {
			Position position = new Position(entry.getX(), entry.getY());
			TerrainType terrain = TerrainType.convertTerrainFromNetwork(entry.getTerrain());
			ClientMapNode node = new ClientMapNode(terrain);
			if (entry.getFortState() == EFortState.MyFortPresent) {
				node.setFortState(FortState.MyFortPresent);
			} else if (entry.getFortState() == EFortState.EnemyFortPresent) {
				node.setFortState(FortState.EnemyFortPresent);
			}
			if (entry.getTreasureState() == ETreasureState.MyTreasureIsPresent) {
				node.setTreasureState(TreasureState.MyTreasurePresent);
			}
			this.fullMap.put(position, node);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FullMap: ");
		builder.append("\n");
		if (!this.fullMap.isEmpty()) {
			builder.append("\n");

			for (int x = 0; x < 20; ++x) {
				for (int y = 0; y < 10; ++y) {
					Position position = new Position(x, y);
					if (this.fullMap.get(position) != null) {
						builder.append(this.fullMap.get(position).toString());
					}
				}
				builder.append("\n");

			}

			for (int x = 0; x < 10; ++x) {
				for (int y = 0; y < 10; ++y) {
					Position position = new Position(x, y);
					if (this.fullMap.get(position) != null) {
						builder.append(this.fullMap.get(position).toString());
					}
				}
				builder.append("\n");

			}
		}

		return builder.toString();
	}
}
