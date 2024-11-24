package validator;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gameobject.Game;
import server.exceptions.GameNotFoundException;

public class GameValidator {
	private final int GAME_ID_LENGTH = 5;
	private static Logger logger = LoggerFactory.getLogger(GameValidator.class);

	public boolean validateGame(Collection<Game> games, String gameId) {
		if (games.isEmpty() || gameId.length() > GAME_ID_LENGTH) {
			throw new GameNotFoundException("Name: GameIDNotExists", "Message: No game exists");
		}
		for (var entry : games) {
			if (entry.getGameId().equals(gameId)) {
				return true;
			}
		}
		return false;
	}

}
