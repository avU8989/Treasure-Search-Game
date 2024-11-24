package gameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import server.exceptions.GameNotFoundException;

public class GameMapGenerator {

	private static Logger logger = LoggerFactory.getLogger(GameMapGenerator.class);
	private final int HALFMAP_HEIGHT = 5;
	private final int HALFMAP_WIDTH = 10;
	private Map<String, Position> playerForts;

	public GameMapGenerator() {
		this.playerForts = new HashMap<>();
	}

	public GameMap combineGameMap(String enemyID, String playerID, GameMap enemyMap, GameMap currentPlayerMap) {
		Random random = new Random();
		boolean combineLongSide = random.nextBoolean(); // Randomly choose between true (combineLongSide) and false
														// (combineShortSide)
		boolean swapMaps = random.nextBoolean(); // Randomly choose whether to swap the order of the map halves

		Optional<Position> enemyFortPosition = enemyMap.getMap().entrySet().stream()
				.filter(c -> c.getValue().getFortState() == FortState.MyFortPresent).map(Map.Entry::getKey).findFirst();

		if (enemyFortPosition.isPresent()) {
			Position enemyFort = enemyFortPosition.get();

		}

		if (swapMaps) {
			GameMap temp = enemyMap;
			enemyMap = currentPlayerMap;
			currentPlayerMap = temp;

			String tempId = enemyID;
			enemyID = playerID;
			playerID = tempId;
		}

		if (combineLongSide) {
			return combineGameMapLongSide(enemyID, playerID, enemyMap, currentPlayerMap);
		} else {
			return combineGameMapShortSide(enemyID, playerID, enemyMap, currentPlayerMap);
		}

	}


	private GameMap combineGameMapShortSide(String enemyID, String playerId, GameMap player1Map, GameMap player2Map) {
		// Create a new GameMap with the combined dimensions
		Map<Position, GameMapNode> combinedMap = new HashMap<>();


		// Copy the tiles from player1Map to the combined map
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				Position position = new Position(x, y);
				if (player1Map.getMap().get(position).getFortState() == FortState.MyFortPresent) {
					playerForts.put(enemyID, position); // problem
				}
				GameMapNode node = player1Map.getMap().get(position);
				combinedMap.put(position, node);
			}
		}

		// Copy the tiles from player2Map to the combined map, adjusting the
		// x-coordinate
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				Position position = new Position(x, y);
				Position newPosition = new Position(x + player1Map.getWidth() + 1, y);
				if (player2Map.getMap().get(position).getFortState() == FortState.MyFortPresent) {
					playerForts.put(playerId, newPosition);
				}
				GameMapNode node = player2Map.getMap().get(position);
				combinedMap.put(newPosition, node);
			}
		}

		return new GameMap(combinedMap);
	}

	private GameMap combineGameMapLongSide(String enemyID, String playerId, GameMap player1Map, GameMap player2Map) {
		// Create a new GameMap with the combined dimensions
		Map<Position, GameMapNode> combinedMap = new HashMap<>();

		// Copy the tiles from player1Map to the combined map
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				Position position = new Position(x, y);
				if (player1Map.getMap().get(position).getFortState() == FortState.MyFortPresent) {
					playerForts.put(enemyID, position);
				}
				GameMapNode node = player1Map.getMap().get(position);
				combinedMap.put(position, node);
			}
		}

		// Copy the tiles from player2Map to the combined map, adjusting the
		// x-coordinate
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 10; x++) {
				Position position = new Position(x, y);
				Position newPosition = new Position(x, y + 5);
				if (player2Map.getMap().get(position).getFortState() == FortState.MyFortPresent) {
					playerForts.put(playerId, newPosition);
				}
				GameMapNode node = player2Map.getMap().get(position);
				combinedMap.put(newPosition, node);
			}
		}

		return new GameMap(combinedMap);
	}

	public Position locateEnemyFortInCombinedMap(String playerId) {
		if (playerForts.containsKey(playerId)) {
			return playerForts.get(playerId);
		} else {
			throw new GameNotFoundException("Name: Locating Enemy Fort", "Message: Game was not found");
		}

	}

	public GameMap shuffleEnemyPosition(Map<Position, GameMapNode> gameMap, String enemyID, String playerID) {
		Random random = new Random();

		Position enemyFort = playerForts.get(enemyID);
		Map<Position, GameMapNode> shuffledMap = new HashMap<>(gameMap);
		if (shuffledMap.containsKey(enemyFort) && shuffledMap.get(enemyFort).getFortState() == FortState.MyFortPresent) {
			GameMapNode fortNode = gameMap.get(enemyFort);
			GameMapNode hideFort = new GameMapNode(fortNode.getTerrain(), FortState.NoOrUnknownFortState,
					PositionState.NoPlayerPresent);
			shuffledMap.replace(enemyFort, hideFort);
		}

		Position myFort = playerForts.get(playerID);

		if (shuffledMap.containsKey(myFort)
				&& shuffledMap.get(myFort).getFortState() == FortState.NoOrUnknownFortState) {
			GameMapNode fortNode = gameMap.get(enemyFort);
			GameMapNode hideFort = new GameMapNode(fortNode.getTerrain(), FortState.MyFortPresent,
					PositionState.MyPlayerPosition);
			shuffledMap.replace(enemyFort, hideFort);
		}

		// Check if an enemy player position already exists in the map
		boolean hasEnemyPosition = shuffledMap.values().stream()
				.anyMatch(node -> node.getPositionState() == PositionState.EnemyPlayerPosition);

		if (hasEnemyPosition) {
			// Update the existing enemy player position to NoPlayerPresent
			shuffledMap.entrySet().stream()
					.filter(entry -> entry.getValue().getPositionState() == PositionState.EnemyPlayerPosition)
					.findFirst().ifPresent(entry -> {
						Position existingPosition = entry.getKey();
						GameMapNode existingNode = entry.getValue();
						GameMapNode newNode = new GameMapNode(existingNode.getTerrain(), existingNode.getFortState(),
								PositionState.NoPlayerPresent);
						shuffledMap.replace(existingPosition, newNode);
					});
		}

		// Get a random key (position) from the map
		List<Position> positions = new ArrayList<>(shuffledMap.keySet());
		Position randomPosition = positions.get(random.nextInt(positions.size()));
		GameMapNode node = shuffledMap.get(randomPosition);
		GameMapNode newNode = new GameMapNode(node.getTerrain(), node.getFortState(),
				PositionState.EnemyPlayerPosition);
		if (node.getPositionState() == PositionState.MyPlayerPosition) {
			newNode = new GameMapNode(node.getTerrain(), node.getFortState(), PositionState.BothPlayerPosition);
		}

		shuffledMap.replace(randomPosition, newNode);

		return new GameMap(shuffledMap);
	}

}
