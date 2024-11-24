package gameobject;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import converter.ConvertToNetwork;
import gameMap.GameMap;
import messagesbase.messagesfromserver.FullMap;
import messagesbase.messagesfromserver.GameState;
import messagesbase.messagesfromserver.PlayerState;
import server.exceptions.ConverterException;
import server.exceptions.MaxPlayerException;


public class Game implements ConvertToNetwork<GameState, Game> {
	private final String gameId;
	private final Set<ClientPlayer> players;
	private final int MAX_PLAYERS = 2;
	private GameMap gameMap;
	private static Logger logger = LoggerFactory.getLogger(Game.class);
	
	public Game() {
		GameIdRandomizer randomizer = new GameIdRandomizer();
		this.gameId = randomizer.gameId;
		players = new HashSet<>();

	}
	
	public Game(String gameId){
		this.gameId = gameId;
		players = new HashSet<>();
	}
	
	public Game(String gameId, Collection<ClientPlayer> players) {
		this.gameId = gameId;
		this.players = new HashSet<>(players);

	}

	public Game(String gameId, Collection<ClientPlayer> players, GameMap map) {
		this(gameId, players);
		this.gameMap = map;
	}

	@Override
	public String toString() {
		return "Game [gameId=" + gameId + ", players=" + players + "]";
	}

	public String getGameId() {
		return this.gameId;
	}
	
	public Set<ClientPlayer> getPlayers() {
		return this.players;
	}

	public void setMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}

	public void addPlayer(ClientPlayer player) {
		if (players.size() == MAX_PLAYERS) {
			throw new MaxPlayerException("Name: Max players", "Message: The game already has two players");
		}
		this.players.add(player);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gameId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Game other = (Game) obj;
		return Objects.equals(gameId, other.gameId);
	}

	@Override
	public GameState convertToNetwork(Game source) {
		if (source == null) {
			throw new ConverterException("Name: GameState Converter", "Message: GameState not found");
		}

		if (source.getPlayers() != null && source.gameMap == null) {
			Set<PlayerState> players = new HashSet<>();
			for (var entry : source.players) {
				PlayerState player = entry.convertToNetwork(entry);
				players.add(player);
			}
			return new GameState(players, source.gameId);
		}

		if (source.getPlayers() != null && !source.gameMap.getMap().isEmpty() && source.gameMap != null) {
			Set<PlayerState> players = new HashSet<>();
			for(var entry : source.players) {
				PlayerState player = entry.convertToNetwork(entry);
				players.add(player);
			}

			for (var entry : source.gameMap.getMap().values()) {
				if (entry == null) {
					logger.info("Converting GameMap has only null values");
					break;
					// this is good
				}
			}

			FullMap map = source.gameMap.convertToNetwork(source.gameMap);
			for (var entry : map.getMapNodes()) {
				if (entry == null) {
					logger.info("Converting GameMap has only null values");
					break;
					// this is good
				}
			}
			return new GameState(map, players, source.gameId);
		}

		return new GameState(source.gameId);

	}

	public GameMap getMap() {
		return this.gameMap;
	}


}
