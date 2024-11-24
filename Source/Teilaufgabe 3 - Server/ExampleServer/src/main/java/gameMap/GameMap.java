package gameMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import converter.ConvertFromNetwork;
import converter.ConvertToNetwork;
import messagesbase.messagesfromclient.ETerrain;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.ETreasureState;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.FullMapNode;
import server.exceptions.ConverterException;

public class GameMap implements ConvertToNetwork<FullMap, GameMap>, ConvertFromNetwork<GameMap, PlayerHalfMap> {
	private Map<Position, GameMapNode> gameMap;
	private Position fortPosition;
	private Position enemyFort;
	private Position treasure;
	private int width = 9;
	private int height = 4;
	private final int WIDTH_VERTICAL = 9;
	private final int HEIGHT_VERTICAL = 9;
	private final int WIDTH_HORIZONTAL = 19;
	private final int HEIGHT_HORIZONTAL = 4;
	private static Logger logger = LoggerFactory.getLogger(GameMap.class);

	public GameMap() {
		this.gameMap = new HashMap<Position, GameMapNode>();
	}

	public GameMap(Map<Position, GameMapNode> fullMap) {
		this.gameMap = new HashMap<Position, GameMapNode>(fullMap);
	}

	public Map<Position, GameMapNode> getMap() {
		return this.gameMap;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	@Override
	public FullMap convertToNetwork(GameMap source) {
		if (source == null || source.getMap().isEmpty()) {
			throw new ConverterException("Name: Convert to FullMap", "The object (GameMap) to be converted is null");
		}

		// set y the highest position in y from the FullMapNode of the Server
		Set<FullMapNode> nodes = new HashSet<>();
		for (var entry : source.gameMap.entrySet()) {
			if (entry != null && entry.getValue() != null) {
				TerrainType terrain = TerrainType.GRASS;
				if (entry.getValue() == null) {
					logger.info(String.valueOf(source.gameMap.size()));
					logger.info(source.gameMap.toString());
				}
				ETerrain newTerrain = terrain.convertToNetwork(entry.getValue().getTerrain());
				PositionState positionState = PositionState.NoPlayerPresent;
				positionState = entry.getValue().getPositionState();
				if (entry.getValue().getFortState() == FortState.MyFortPresent) {
					positionState = PositionState.MyPlayerPosition;
				} // problem we have 2 myFortPresent in our map, we should hide one, and the
					// hiding should be in the game Logic, so if the 2nd playerhalfmap is being sent
					// hide the fort of the 2nd player sending the map and randomize his
					// positionstate
				
				TreasureState treasureState = TreasureState.NoOrUnknownTreasureState;
				ETreasureState newTreasureState = treasureState.convertToNetwork(entry.getValue().getTreasureState());
				FortState fortState = FortState.NoOrUnknownFortState;
				EFortState newFortState = fortState.convertToNetwork(entry.getValue().getFortState());
				FullMapNode node = new FullMapNode(newTerrain, positionState.convertToNetwork(positionState),
						newTreasureState, newFortState,
						entry.getKey().getCoordinateX(), entry.getKey().getCoordinateY());
				nodes.add(node);
			}
		}

		return new FullMap(nodes);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (!this.gameMap.isEmpty() && this.gameMap != null) {
			builder.append("FullMap: ");
			builder.append("\n");

			if (19 == WIDTH_HORIZONTAL) {
				for (int y = 0; y < HEIGHT_HORIZONTAL + 1; ++y) {
					for (int x = 0; x < WIDTH_HORIZONTAL + 1; ++x) {
						Position position = new Position(x, y);
						if (this.gameMap.get(position) != null && this.gameMap.containsKey(position)) {
							builder.append(this.gameMap.get(position).toString());
						}
					}
					builder.append("\n");

				}
			} else if (height == HEIGHT_VERTICAL) {
				for (int y = 0; y < HEIGHT_VERTICAL + 1; ++y) {
					for (int x = 0; x < WIDTH_VERTICAL + 1; ++x) {
						Position position = new Position(x, y);
						if (this.gameMap.get(position) != null && this.gameMap.containsKey(position)) {
							builder.append(this.gameMap.get(position).toString());
						}
					}
					builder.append("\n");

				}

			}

		}
		return builder.toString();
	}

	@Override
	public GameMap convertFromNetwork(PlayerHalfMap target) {
		// set y the highest position in y from the FullMapNode of the Server
		Map<Position, GameMapNode> newGameMap = new HashMap<>();
		for (var entry : target.getMapNodes()) {
			Position position = new Position(entry.getX(), entry.getY());
			TerrainType terrain = TerrainType.GRASS;
			TerrainType newTerrain = terrain.convertFromNetwork(entry.getTerrain());
			boolean isFortPresent = entry.isFortPresent();
		    FortState fortState = isFortPresent ? FortState.MyFortPresent : FortState.NoOrUnknownFortState;
		    PositionState positionState = isFortPresent ? PositionState.MyPlayerPosition : PositionState.NoPlayerPresent;
		    GameMapNode node = new GameMapNode(newTerrain, fortState, positionState);

			newGameMap.put(position, node);
		}

		return new GameMap(newGameMap);
	}



}
