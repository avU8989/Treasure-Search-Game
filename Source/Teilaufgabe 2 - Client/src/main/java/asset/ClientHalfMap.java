package asset;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import network.ConvertToNetwork;
import physics.Position;
import terrain.FortState;
import terrain.TerrainType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement()
public class ClientHalfMap implements ConvertToNetwork<Collection<PlayerHalfMapNode>, Map<Position, ClientMapNode>> {

	protected Map<Position, ClientMapNode> clientMap;
	protected Position fortPosition;
	private final int height = 5;
	private final int width = 10;
	private final int MAX_SIZE = 50;

	public ClientHalfMap() {
		this.clientMap = new HashMap<Position, ClientMapNode>();
		this.fortPosition = new Position();
	}

	public ClientHalfMap(Map<Position, ClientMapNode> nodes) {
		if (nodes.size() > MAX_SIZE) {
			throw new IllegalArgumentException("cannot be over 50 fields");
		}
		this.clientMap = new HashMap<Position, ClientMapNode>(nodes);
	}

	public ClientHalfMap(Map<Position, ClientMapNode> nodes, Position fort) {
		this(nodes);
		this.fortPosition = fort;
	}

	private boolean isEmpty() {
		return clientMap.isEmpty();
	}

	public Map<Position, ClientMapNode> getMap() {
		return this.clientMap;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public Position getFort() {
		return this.fortPosition;
	}

	public void setFort(Position position) {
		this.fortPosition = position;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClientMap: ");
		builder.append("MapSize[" + this.width + "x" + this.height + "] ");
		builder.append("\n");
		builder.append("FORT POSITION: " + this.fortPosition.toString());
		if (!this.clientMap.isEmpty()) {
			builder.append("\n");

			for (int x = 0; x < this.width; ++x) {
				for (int y = 0; y < this.height; ++y) {
					Position position = new Position(x, y);
					builder.append(clientMap.get(position).toString());
				}
				builder.append("\n");

			}
		}

		return builder.toString();
	}

	@Override
	public Collection<PlayerHalfMapNode> convertToNetwork(Map<Position, ClientMapNode> target) {
		Set<PlayerHalfMapNode> nodes = new HashSet<>();
		for (var entry : target.entrySet()) {

			if (entry.getKey() != null && entry.getValue() != null) {
				if (entry.getValue().getTerrain() == TerrainType.WATER) {
					PlayerHalfMapNode node = new PlayerHalfMapNode(entry.getKey().getCoordinateX(),
							entry.getKey().getCoordinateY(), ETerrain.Water);
					nodes.add(node);
				}

				if (entry.getValue().getTerrain() == TerrainType.GRASS) {
					if (entry.getValue().getFortState() == FortState.MyFortPresent) {
						PlayerHalfMapNode fort = new PlayerHalfMapNode(entry.getKey().getCoordinateX(),
								entry.getKey().getCoordinateY(), true, ETerrain.Grass);
						nodes.add(fort);
					} else {
						PlayerHalfMapNode node = new PlayerHalfMapNode(entry.getKey().getCoordinateX(),
								entry.getKey().getCoordinateY(), ETerrain.Grass);
						nodes.add(node);
					}

				}

				if (entry.getValue().getTerrain() == TerrainType.MOUNTAIN) {
					PlayerHalfMapNode node = new PlayerHalfMapNode(entry.getKey().getCoordinateX(),
							entry.getKey().getCoordinateY(), ETerrain.Mountain);
					nodes.add(node);
				}
			}
		}
		return nodes;
	}

}
