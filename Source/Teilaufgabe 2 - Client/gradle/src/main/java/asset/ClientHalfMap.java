package asset;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import network.ConvertToNetwork;
import physics.Position;
import terrain.TerrainType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement()
public class ClientHalfMap implements ConvertToNetwork <Collection<PlayerHalfMapNode>, HashMap<Position, ClientMapNode>>{

	protected HashMap<Position, ClientMapNode> clientMap;
	protected Position fortPosition;
	private final int height = 5;
	private final int width = 10;

	public ClientHalfMap() {
		this.clientMap = new HashMap<Position, ClientMapNode>();
		this.fortPosition = new Position();
	}

	public ClientHalfMap(Map<Position, ClientMapNode> nodes) {
		this.clientMap = new HashMap<Position, ClientMapNode>(nodes);
	}

	public ClientHalfMap(Map<Position, ClientMapNode> nodes, Position fort) {
		this.clientMap = new HashMap<Position, ClientMapNode>(nodes);
		this.fortPosition = fort;
	}

	private boolean isEmpty() {
		return clientMap.isEmpty();
	}

	public HashMap<Position, ClientMapNode> getMap() {
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

	public void setMap(Map<Position, ClientMapNode> halfMap) {
		this.clientMap = new HashMap<Position, ClientMapNode>(halfMap);
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
	public Collection<PlayerHalfMapNode> convertToNetwork(HashMap<Position, ClientMapNode> target) {
		Set<PlayerHalfMapNode> nodes = new HashSet<>();
		if (fortPosition != null) {
			ETerrain terrain = TerrainType.convertTerrainToNetwork(clientMap.get(fortPosition).getTerrain());
			PlayerHalfMapNode fort = new PlayerHalfMapNode(fortPosition.getCoordinateX(), fortPosition.getCoordinateY(),
					true, terrain);
			nodes.add(fort);
		}

		for (var entry : target.entrySet()) {
			if (entry.getKey() == fortPosition) {
				continue;
			}

			if (entry.getKey() != null && entry.getValue() != null) {
				if (entry.getValue().getTerrain() == TerrainType.WATER) {
					PlayerHalfMapNode node = new PlayerHalfMapNode(entry.getKey().getCoordinateX(),
							entry.getKey().getCoordinateY(), ETerrain.Water);
					nodes.add(node);
				}

				if (entry.getValue().getTerrain() == TerrainType.GRASS) {
					PlayerHalfMapNode node = new PlayerHalfMapNode(entry.getKey().getCoordinateX(),
							entry.getKey().getCoordinateY(), ETerrain.Grass);
					nodes.add(node);
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
