package components;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import asset.GameMap;
import exceptions.ConvertGameException;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import network.ConvertFromNetwork;

public class Game implements ConvertFromNetwork<Game, GameState> {
	private String gameId;
	private String gameStateID;
	private Player player;
	private GameMap fullMap;
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public Game() {
		this.fullMap = new GameMap();
		this.player = new Player();

	}

	public Game(String gameId) {
		this.gameId = gameId;
		this.fullMap = new GameMap();
	}

	public Game(String gameId, Player player) {
		this(gameId);
		this.player = player;
		this.fullMap = new GameMap();

	}

	public Game(String gameId, Player player, GameMap fullMap) {
		this(gameId, player);
		this.fullMap = fullMap;
	}

	public String getGameId() {
		return this.gameId;
	}

	public Player getPlayer() {
		return this.player;
	}

	public GameMap getGameMap() {
		return this.fullMap;
	}

	public void setPlayer(Player player) {
		Player beforePlayer = this.player;
		this.player = player;
		changes.firePropertyChange("Register Player", beforePlayer, this.player);
	}

	@Override
	public Game convertFromNetwork(GameState source) {
		if (source == null) {
			throw new ConvertGameException("GameState not found");
		}

		if (source.getPlayers() != null && source.getMap() == null) {
			PlayerState playerState = new PlayerState();
			for (PlayerState entry : source.getPlayers()) {
				if (entry.getUniquePlayerID().equals(this.player.getPlayerId())) {
					playerState = entry;
				}
			}

			this.player.convertFromNetwork(playerState);
		}

		if (source.getPlayers() != null && source.getMap() != null) {
			PlayerState playerState = new PlayerState();
			for (PlayerState entry : source.getPlayers()) {
				if (entry.getUniquePlayerID().equals(this.player.getPlayerId())) {
					playerState = entry;
				}
			}
			this.player.convertFromNetwork(playerState);
			PlayerFigure figure = new PlayerFigure(this.player.getPlayerId());
			if (playerState.hasCollectedTreasure()) {
				figure.collectTreasure();
			}
			this.fullMap.setPlayerFigure(figure);
			this.setGameMap(source.getMap());

		}
		this.setGameState(source.getGameStateId());
		return this;

	}

	public void setGameState(String gameStateID) {
		String beforeGameID = this.gameStateID;
		this.gameStateID = gameStateID;
		changes.firePropertyChange("Update GameState", beforeGameID, this.gameStateID);

	}

	public void setGameMap(FullMap fullMap) {

		if (fullMap != null) {
			GameMap beforeMap = this.fullMap;
			this.fullMap.convertFromNetwork(fullMap);
			// -> fire PropertyChange
			changes.firePropertyChange("Update Map", beforeMap, this.fullMap);
		}

	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// enables to register new listeners
		if (listener == null) {
			throw new IllegalArgumentException("no events added");
		} else {
			changes.addPropertyChangeListener(listener);
		}
	}

	public String getGameStateID() {
		return this.gameStateID;
	}

}
