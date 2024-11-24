package gamestatechange;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asset.ClientHalfMap;
import asset.ClientHalfMapGenerator;
import asset.ClientHalfMapValidator;
import components.Game;
import components.Player;
import exceptions.ClientMapException;
import exceptions.MovementException;
import gameobjectchange.PlayerStatus;
import messagesbase.messagesfromserver.GameState;
import network.ClientNetworkRequest;
import physics.Direction;
import physics.MovementLogic;
import physics.PlayerMovement;
import physics.Position;

public class GameController {
	private Game game;
	private static GameStateVisualisation view;
	private String gameStateId;
	private ClientNetworkRequest requester;
	private static Logger logger = LoggerFactory.getLogger(GameController.class);

	public GameController(Game gameModel) {
		this.game = gameModel;
	}

	public GameController(Game gameModel, ClientNetworkRequest requester) {
		this.game = gameModel;
		this.requester = requester;
	}

	public void startGame() throws MovementException {
		this.registerClient();
		try {
			this.sendMap();
		} catch (ClientMapException e) {
			e.printStackTrace();
		}
		this.executeGameStrategy();

	}

	private void updateGameState() {
		GameState uniqueGameId = requester.requestGameState();
		this.gameStateId = uniqueGameId.getGameStateId();
		game.convertFromNetwork(uniqueGameId);
	}

	private void registerClient() {
		Player player = new Player("Anh", "Vu", "vua36");
		this.requester.requestClientRegistration(player);
		player.setPlayerID(requester.getPlayerId());
		logger.trace("Registered Player {}", player.getPlayerId());
		game.setPlayer(player);
	}

	private void pollGameState() {
		if (game.getPlayer() == null) {
			throw new IllegalArgumentException("No players in Game");
		}
		while (game.getPlayer().getPlayerState() == PlayerStatus.ENEMYTURN) {
			try {
				Thread.sleep(400);
				this.updateGameState();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (game.getPlayer().getPlayerState() == PlayerStatus.WON
				|| game.getPlayer().getPlayerState() == PlayerStatus.LOST) {
			System.exit(0);
		}

	}

	private void sendMap() throws ClientMapException {
		this.pollGameState();
		if (game.getPlayer() == null) {
			throw new IllegalArgumentException("No players in Game");
		}

		if (game.getPlayer().getPlayerState() == PlayerStatus.YOURTURN) {
			ClientHalfMap halfMap = new ClientHalfMap();
			ClientHalfMapGenerator generator = new ClientHalfMapGenerator(halfMap.getMap());
			ClientHalfMapValidator validator = new ClientHalfMapValidator(generator.getMap(), generator.getFort());
			this.requester.requestSendMap(validator);
		}
	}

	private void executeGameStrategy() throws MovementException {
		this.updateGameState();
		MovementLogic movementLogic = new MovementLogic(game.getGameMap());
		movementLogic.startMoving();
		Set<Position> visited = new HashSet<>();
		while (game.getPlayer().getPlayerState() != PlayerStatus.WON
				|| game.getPlayer().getPlayerState() != PlayerStatus.LOST) {
			this.pollGameState();

			// strategy movement to destination
			if (game.getGameMap().getTreasure() != null) {
				movementLogic.setStrategy(game.getGameMap());
				movementLogic.startMoving();

				if (!visited.isEmpty()) {
					movementLogic.setVisitedFields(visited);
				}
				if (movementLogic.getVisited() != null) {
					visited.addAll(movementLogic.getVisited());
				}
			}

			// strategy movement to unknown fort
			if (game.getGameMap().getFigure().getTreasureCollected()) {
				movementLogic.setStrategy(game.getGameMap());
				movementLogic.startMoving();
				if (!visited.isEmpty()) {
					movementLogic.setVisitedFields(visited);
				}
				if (movementLogic.getVisited() != null) {
					visited.addAll(movementLogic.getVisited());
				}
				break;
			}

			Direction dir = movementLogic.sendDirection();

			if (game.getPlayer().getPlayerState() == PlayerStatus.YOURTURN) {
				this.sendMove(dir, game.getPlayer().getPlayerId()); // fire property change needed
			}
		}

		while (game.getPlayer().getPlayerState() != PlayerStatus.WON
				|| game.getPlayer().getPlayerState() != PlayerStatus.LOST) {
			this.pollGameState();

			// set strategy to movement to destination
			if (game.getGameMap().getEnemyFort() != null) {
				movementLogic.setStrategy(game.getGameMap());
				movementLogic.startMoving();
				if (!visited.isEmpty()) {
					movementLogic.setVisitedFields(visited);
				}
				if (movementLogic.getVisited() != null) {
					visited.addAll(movementLogic.getVisited());
				}
			}

			Direction dir = movementLogic.sendDirection();
			if (game.getPlayer().getPlayerState() == PlayerStatus.YOURTURN) {
				this.sendMove(dir, game.getPlayer().getPlayerId()); // fire property change needed
			}
		}
	}

	private void sendMove(Direction dir, String playerId) {

		if (game.getGameMap() != null) {
			PlayerMovement move = new PlayerMovement(dir, playerId);
			if (this.requester.requestMovementAction(move)) {
				logger.info("Movement sent {}", dir);
				this.updateGameState();
			}
		}
	}

	public Game getGame() {
		return this.game;
	}

}
