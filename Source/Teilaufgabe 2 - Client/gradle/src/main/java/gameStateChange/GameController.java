package gameStateChange;

import asset.ClientHalfMap;
import asset.ClientHalfMapGenerator;
import asset.ClientHalfMapValidator;
import components.Game;
import components.KI;
import components.Player;
import gameObjectState.PlayerStatus;
import messagesbase.messagesfromserver.GameState;
import network.ClientNetworkRequest;

public class GameController {
	private Game game;
	private KI AI;
	private static GameStateVisualisation view;
	private String gameStateId;
	private ClientNetworkRequest requester;

	public GameController() {

	}

	public GameController(Game gameModel) {
		this.game = gameModel;
	}

	public GameController(Game gameModel, ClientNetworkRequest requester) {
		this.game = gameModel;
		this.requester = requester;
	}

	public void startGame() {
		this.registerClient();
		this.pollGameState();
		this.sendMap();
		this.updateGameState();
		System.out.println(game.getGameMap().toString());

		// set Status for client till (status is PlayerTurn)
		// sendMap()
		// do other stuff
	}

	public void updateGameState() {
		GameState uniqueGameId = requester.requestGameState();
		this.gameStateId = uniqueGameId.getGameStateId();
		game.convertFromNetwork(uniqueGameId);
	}

	public void registerClient() {
		Player player = new Player("Anh", "Vu", "vua36");
		this.requester.requestClientRegistration(player);
		player.setPlayerID(requester.getPlayerId());
		game.setPlayer(player);
		System.out.println(player.getPlayerId());

	}

	public void pollGameState() {
		if (game.getPlayer() == null) {
			throw new IllegalArgumentException("No players in Game");
		}
		while (game.getPlayer().getPlayerState() == PlayerStatus.ENEMYTURN) {
			try {
				Thread.sleep(4000);
				this.updateGameState();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void sendMap() {
		if (game.getPlayer() == null) {
			throw new IllegalArgumentException("No players in Game");
		}

		if (game.getPlayer().getPlayerState() == PlayerStatus.YOURTURN) {
			ClientHalfMap halfMap = new ClientHalfMap();
			ClientHalfMapGenerator generator = new ClientHalfMapGenerator(halfMap.getMap());
			ClientHalfMapValidator validator = new ClientHalfMapValidator(generator.getMap(), generator.getFort());
			validator.validate();
			System.out.println(validator.toString());
			this.requester.requestSendMap(validator);
		}
	}

	private void collectTreasure() {

	}

	private void changePosition() {

	}

}
