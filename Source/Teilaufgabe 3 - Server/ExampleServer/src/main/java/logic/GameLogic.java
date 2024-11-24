package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameMap.FortState;
import gameMap.GameMap;
import gameMap.GameMapGenerator;
import gameMap.PositionState;
import gameobject.ClientPlayer;
import gameobject.Game;
import messagesbase.UniqueGameIdentifier;
import server.exceptions.GameNotFoundException;
import server.exceptions.NoPlayersPresentException;
import state.PlayerStatus;
import validator.GameValidator;

public class GameLogic {
	private Queue<Game> games;
	private final int MAX_GAMES = 99;
	private GameValidator validator;
	private GameMapGenerator mapGenerator;
	private static Logger logger = LoggerFactory.getLogger(GameLogic.class);

	public GameLogic() {
		this.games = new LinkedList<>();
		validator = new GameValidator();
		mapGenerator = new GameMapGenerator();

	}

	public synchronized String createGame() {
		if (games.size() == MAX_GAMES) {
			// throw new NewGameException("Name: New Game", "Message: Maximal amount of
			// queued games reached");
			try {
				Thread.sleep(120000); // 2 minutes in milliseconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Game game = new Game();
		games.add(game);
		return game.getGameId();
	}

	public boolean validateGameId(UniqueGameIdentifier gameId) {
		if (this.validator.validateGame(games, gameId.getUniqueGameID())) {
			return true;
		}

		return false;

	}

	public void registerPlayer(UniqueGameIdentifier gameId, ClientPlayer player) {
		this.validateGameId(gameId);
		Optional<Game> optionalGame = games.stream().filter(c -> c.getGameId().equals(gameId.getUniqueGameID()))
				.findAny();

		if (optionalGame.isPresent()) {
			Game game = optionalGame.get();
			game.addPlayer(player); // game object shouldnt implement an add method, maybe in gameLogic class
			if (game.getPlayers().size() == 2) {
				Game afterRegister = chooseFirstPlayerTurn(game);
				this.replaceGameInQueue(afterRegister);
				return;
			}
			this.changeTurnEnemy(gameId, player.getPlayerId());

		} else {
			logger.info("Games are {}", games.toString());
			logger.info("Game Id is {}", gameId.getUniqueGameID());
			throw new GameNotFoundException("Name: Register", "Message: Cannot Register, because GameId doesn't exist");
		}
	}

	public Game chooseFirstPlayerTurn(Game game) {
		// randomly select a player to start the game
		if (game.getPlayers().size() == 2) {
			List<ClientPlayer> playerList = new ArrayList<>(game.getPlayers());
			Collections.shuffle(playerList); // Randomly shuffle the players

			ClientPlayer firstPlayer = playerList.get(0);
			firstPlayer.setState(PlayerStatus.YOURTURN);

			ClientPlayer secondPlayer = playerList.get(1);
			secondPlayer.setState(PlayerStatus.ENEMYTURN);

			Set<ClientPlayer> shuffledPlayers = new HashSet<>(playerList);
			return new Game(game.getGameId(), shuffledPlayers);
		}

		return game;
	}

	public Set<ClientPlayer> retrievePlayers(UniqueGameIdentifier gameId) {
		this.validator.validateGame(games, gameId.getUniqueGameID());

		Optional<Set<ClientPlayer>> optionalPlayers = games.stream()
				.filter(c -> c.getGameId().equals(gameId.getUniqueGameID()))
				.map(Game::getPlayers).findFirst();

		if (optionalPlayers.isEmpty()) {
			throw new NoPlayersPresentException("Name: Send map or move",
					"Message: Players are not registered for the game");
		} else {
			Set<ClientPlayer> playerSet = optionalPlayers.get();
			return playerSet;
		}

	}

	private void changeTurnEnemy(UniqueGameIdentifier gameId, String playerId) {
		Set<ClientPlayer> players = this.retrievePlayers(gameId);

		Optional<ClientPlayer> optionalEnemy = players.stream().filter(c -> !c.getPlayerId().equals(playerId))
				.findFirst();
		Optional<ClientPlayer> optionalPlayer = players.stream().filter(c -> c.getPlayerId().equals(playerId))
				.findFirst();

		if (optionalEnemy.isPresent() && optionalPlayer.isPresent()) {
			ClientPlayer enemy = optionalEnemy.get();
			ClientPlayer player = optionalPlayer.get();

			if (player.getState() == PlayerStatus.ENEMYTURN) {
				enemy.setState(PlayerStatus.YOURTURN);
			} else if (player.getState() == PlayerStatus.YOURTURN) {
				enemy.setState(PlayerStatus.ENEMYTURN);
			}

		}
	}


	public void replaceGameInQueue(Game game) {
		if (games.contains(game)) {
			List<Game> gameList = new LinkedList<>(games);
			int index = gameList.indexOf(game);
			gameList.set(index, game);
			games = new LinkedList<>(gameList);
		} else {
			logger.info(games.toString());
			logger.info("Game not found is {}", game.getGameId());
			throw new GameNotFoundException("Name: Updating GameState", "Message: Game not found in the queue");
		}
	}

	public void updateGameMapWithHalfMap(UniqueGameIdentifier gameId, GameMap gameMap, String playerId) {
		this.changeTurnEnemy(gameId, playerId);
		Game game = this.retrieveGame(gameId, playerId);
		Set<ClientPlayer> players = this.retrievePlayers(gameId);

		Optional<ClientPlayer> optionalEnemyPlayer = game.getPlayers().stream()
				.filter(player -> !player.getPlayerId().equals(playerId)).findFirst();

		if (game.getMap() != null && !game.getMap().getMap().isEmpty() && optionalEnemyPlayer.isPresent()) {
			ClientPlayer enemy = optionalEnemyPlayer.get();
			GameMap combinedMap = mapGenerator.combineGameMap(enemy.getPlayerId(), playerId, game.getMap(),
					gameMap);
			Game newGame = new Game(gameId.getUniqueGameID(), players, combinedMap);
			this.replaceGameInQueue(newGame);
		} else {
			Game newGame = new Game(gameId.getUniqueGameID(), players, gameMap);
			this.replaceGameInQueue(newGame);

		}

	}


	public Game retrieveGame(UniqueGameIdentifier gameId, String playerID) {
		this.validator.validateGame(games, gameId.getUniqueGameID());

		Optional<Game> optionalGame = games.stream().filter(c -> c.getGameId().equals(gameId.getUniqueGameID()))
				.findAny();

		if (optionalGame.isPresent()) {
			Game game = optionalGame.get();
			return game;
		} else {
			throw new GameNotFoundException("Name: Request GameState",
					"Message: Cannot send GameState, because GameId doesn't exist");
		}

	}

	public Game hideEnemyFortPosition(Game game, String playerID) {
		Optional<ClientPlayer> optionalEnemyPlayer = game.getPlayers().stream()
				.filter(player -> !player.getPlayerId().equals(playerID)).findFirst();

		if (game.getMap() != null && game.getMap().getMap().size() == 100) {
			if (optionalEnemyPlayer.isPresent()) {
				GameMap hiddenMap = mapGenerator.shuffleEnemyPosition(game.getMap().getMap(),
						optionalEnemyPlayer.get().getPlayerId(), playerID);
				if (hiddenMap != null && hiddenMap.getMap() != null && hiddenMap.getMap().size() == 100) {
					long myFortPresentCount = hiddenMap.getMap().values().stream()
							.filter(node -> node.getFortState() == FortState.MyFortPresent).count();
					long enemyPlayerPositionCount = hiddenMap.getMap().values().stream()
							.filter(node -> node.getPositionState() == PositionState.EnemyPlayerPosition).count();
					logger.info("Client Turn is {}", playerID);

					if (myFortPresentCount == 1) {
						// Print the positions with more than one MyFortPresent
						hiddenMap.getMap().forEach((position, node) -> {
							if (node.getFortState() == FortState.MyFortPresent) {
								logger.info("MyFortPresent at position: {}", position);
							} else if (node.getFortState() == FortState.EnemyFortPresent) {
								logger.info("EnemyFortPresent at position: {}", position);
							}
						});
					} else if (myFortPresentCount == 0) {
						logger.info("No than one MyFortPresent.");
					}

					if (enemyPlayerPositionCount == 1) {
						// Print the positions with more than one EnemyPlayerPosition
						hiddenMap.getMap().forEach((position, node) -> {
							if (node.getPositionState() == PositionState.EnemyPlayerPosition) {
								logger.info("EnemyPlayerPosition at position: {}", position);
							} else if (node.getPositionState() == PositionState.MyPlayerPosition) {
								logger.info("MyPlayerPosition at position: {}", position);
							}
						});
					}
				}
				return new Game(game.getGameId(), game.getPlayers(), hiddenMap);
			}
		}
		// Return the original game if the enemy player is not found
		return game;
	}



	public Game hideEnemyPlayerID(Game game, String playerID) {
		Set<ClientPlayer> updatedPlayers = new HashSet<>();

		// Find the enemy player in the game's players set
		Optional<ClientPlayer> optionalEnemyPlayer = game.getPlayers().stream()
				.filter(player -> !player.getPlayerId().equals(playerID)).findFirst();

		Optional<ClientPlayer> optionalPlayer = game.getPlayers().stream()
				.filter(player -> player.getPlayerId().equals(playerID)).findFirst();

		optionalPlayer.ifPresent(currentPlayer -> {
			updatedPlayers.add(currentPlayer);
		});


		// If the enemy player is found, replace their ID with a fake ID
		optionalEnemyPlayer.ifPresent(enemyPlayer -> {
			String fakeID = "Enemy Player"; // Replace this with your fake ID generation logic
			ClientPlayer newEnemy = new ClientPlayer(fakeID, enemyPlayer.getFirstName(), enemyPlayer.getLastName(),
					enemyPlayer.getUaccount());
			updatedPlayers.add(newEnemy);
		});

		return new Game(game.getGameId(), updatedPlayers, game.getMap());
	}


	public Queue<Game> getGames() {
		return this.games;
	}

}
