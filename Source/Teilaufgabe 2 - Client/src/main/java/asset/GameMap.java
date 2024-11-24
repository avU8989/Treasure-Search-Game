package asset;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import components.PlayerFigure;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import network.ConvertFromNetwork;
import physics.Position;
import terrain.FortState;
import terrain.PositionState;
import terrain.TerrainType;
import terrain.TreasureState;

public class GameMap implements ConvertFromNetwork<GameMap, FullMap> {
	private HashMap<Position, ClientMapNode> fullMap;
	private PlayerFigure figure;
	private Position fortPosition;
	private Position enemyFort;
	private Position treasure;
	private int width;
	private int height;
	private final int WIDTH_VERTICAL = 9;
	private final int HEIGHT_VERTICAL = 9;
	private final int WIDTH_HORIZONTAL = 19;
	private final int HEIGHT_HORIZONTAL = 4;
	private static Logger logger = LoggerFactory.getLogger(GameMap.class);

	public GameMap() {
		this.fullMap = new HashMap<Position, ClientMapNode>();
	}

	public GameMap(Map<Position, ClientMapNode> fullMap) {
		this.fullMap = new HashMap<Position, ClientMapNode>(fullMap);
	}

	public Position getFort() {
		return this.fortPosition;
	}

	public HashMap<Position, ClientMapNode> getMap() {
		return this.fullMap;
	}

	public PlayerFigure getFigure() {
		return this.figure;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setPlayerFigure(PlayerFigure figure) {
		this.figure = figure;
	}

	public void setTreasure(Position pos, ClientMapNode node) {
		this.treasure = pos;
		node.setTreasureState(TreasureState.MyTreasurePresent);
		this.fullMap.replace(pos, node);
	}

	public void setEnemyFort(Position pos, ClientMapNode node) {
		this.fortPosition = pos;
		node.setFortState(FortState.EnemyFortPresent);
		this.fullMap.replace(pos, node);
	}

	@Override
	public GameMap convertFromNetwork(FullMap target) {
		// set x the highest position in x from the FullMapNode of the Server
		// set y the highest position in y from the FullMapNode of the Server
		for (var entry : target.getMapNodes()) {
			Position position = new Position(entry.getX(), entry.getY());
			ClientMapNode node = new ClientMapNode();

			if (entry.getTerrain() == ETerrain.Grass) {
				node.setTerrain(TerrainType.values()[ETerrain.Grass.ordinal()]);
			}

			if (entry.getTerrain() == ETerrain.Water) {
				node.setTerrain(TerrainType.values()[ETerrain.Water.ordinal()]);
			}

			if (entry.getTerrain() == ETerrain.Mountain) {
				node.setTerrain(TerrainType.values()[ETerrain.Mountain.ordinal()]);
			}

			if (entry.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition) {
				figure.setPosition(position);
				node.setPositionState(PositionState.MyPlayerPosition);
			}

			if (entry.getFortState() == EFortState.MyFortPresent) {
				node.setFortState(FortState.MyFortPresent);
				this.fortPosition = position;
			}

			if (entry.getFortState() == EFortState.EnemyFortPresent) {
				node.setFortState(FortState.EnemyFortPresent);
				this.enemyFort = position;
			}

			if (entry.getPlayerPositionState() == EPlayerPositionState.EnemyPlayerPosition) {
				node.setPositionState(PositionState.EnemyPlayerPosition);
			}

			if (entry.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition) {
				figure.setPosition(position);
				node.setPositionState(PositionState.BothPlayerPosition);
			}

			if (entry.getX() == WIDTH_HORIZONTAL) {
				width = WIDTH_HORIZONTAL;
			}

			if (entry.getY() == HEIGHT_VERTICAL) {
				height = WIDTH_VERTICAL;
			}

			if (entry.getTreasureState().equals(ETreasureState.MyTreasureIsPresent)) {
				logger.info("TREASURE LOCATED");
				node.setTreasureState(TreasureState.MyTreasurePresent);
				this.treasure = position;
			}

			this.fullMap.put(position, node);
		}

		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (!this.fullMap.isEmpty() && this.fullMap != null) {
			builder.append("FullMap: ");
			builder.append("\n");

			if (width == WIDTH_HORIZONTAL) {
				for (int y = 0; y < HEIGHT_HORIZONTAL + 1; ++y) {
					for (int x = 0; x < WIDTH_HORIZONTAL + 1; ++x) {
						Position position = new Position(x, y);
						if (this.fullMap.get(position) != null && this.fullMap.containsKey(position)) {
							builder.append(this.fullMap.get(position).toString());
						}
					}
					builder.append("\n");

				}
			} else if (height == HEIGHT_VERTICAL) {
				for (int y = 0; y < HEIGHT_VERTICAL + 1; ++y) {
					for (int x = 0; x < WIDTH_VERTICAL + 1; ++x) {
						Position position = new Position(x, y);
						if (this.fullMap.get(position) != null && this.fullMap.containsKey(position)) {
							builder.append(this.fullMap.get(position).toString());
						}
					}
					builder.append("\n");

				}

			}

		}
		return builder.toString();
	}

	public Position getTreasure() {
		return this.treasure;
	}

	public Position getEnemyFort() {
		return this.enemyFort;
	}

	public int getMaxWidthHorizontal() {
		return this.WIDTH_HORIZONTAL;
	}

	public int getMaxHeightVertical() {
		return this.HEIGHT_VERTICAL;

	}

	public void setFort(Position fort) {
		this.fortPosition = fort;
	}
}
