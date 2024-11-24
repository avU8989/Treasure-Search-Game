package components;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import asset.GameMap;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import network.ConvertFromNetwork;

public class Game implements ConvertFromNetwork<GameState> {
	private String gameId;
	private Player player;
	private GameMap fullMap;
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public Game() {
		this.fullMap = new GameMap();

	}

	public Game(String gameId) {
		this.gameId = gameId;
		this.fullMap = new GameMap();
	}

	public Game(String gameId, Player player) {
		this.gameId = gameId;
		this.player = player;
		this.fullMap = new GameMap();

	}

	public Game(String gameId, Player player, GameMap fullMap) {
		this.gameId = gameId;
		this.player = player;
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
		this.player = player;
	}

	@Override
	public void convertFromNetwork(GameState source) {
		if (source.getPlayers() == null) {
			this.gameId = source.getGameStateId();
		}

		if (source.getPlayers() != null && source.getMap() == null) {
			PlayerState playerState = new PlayerState();
			for (PlayerState entry : source.getPlayers()) {
				if (entry.getUniquePlayerID().equals(this.player.getPlayerId())) {
					playerState = entry;
				}
			}

			this.player.convertFromNetwork(playerState);
			this.gameId = source.getGameStateId();
		}

		if (source.getPlayers() != null && source.getMap() != null) {
			PlayerState playerState = new PlayerState();
			for (PlayerState entry : source.getPlayers()) {
				if (entry.getUniquePlayerID().equals(this.player.getPlayerId())) {
					playerState = entry;
				}
			}
			this.updateGameMap(source.getMap());
			this.player.convertFromNetwork(playerState);
		}

	}

	public void updateGameMap(FullMap fullMap) {

		if (fullMap != null) {
			// GameMap beforeMap = this.fullMap;
			this.fullMap.convertFromNetwork(fullMap);
			// -> fire PropertyChange
			// changes.firePropertyChange("Update Map", beforeMap, fullMap);
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

}
