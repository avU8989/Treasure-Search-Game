package service;

import java.util.Set;

import org.springframework.stereotype.Service;

import gameMap.GameMap;
import gameobject.ClientPlayer;
import logic.GameLogic;
import logic.PlayerLogic;
import messagesbase.UniqueGameIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import server.exceptions.MapAlreadySentException;
import server.exceptions.PlayerNotFoundException;
import server.exceptions.PlayerTurnException;
import server.exceptions.WaitForTwoPlayersBeforeMapSendingException;

@Service
public class MapSubmissionService {
	private final GameLogic gameLogic;
	private final PlayerLogic playerLogic;

	public MapSubmissionService(GameLogic gameLogic, PlayerLogic playerLogic) {
		this.gameLogic = gameLogic;
		this.playerLogic = playerLogic;
	}

	public void processMapSubmission(UniqueGameIdentifier gameID, PlayerHalfMap playerHalfMap) {
		Set<ClientPlayer> players = gameLogic.retrievePlayers(gameID);
		String playerID = playerHalfMap.getUniquePlayerID();

		// Check if the player exists
        if (!playerLogic.verifyPlayer(players, playerID)) {
            throw new PlayerNotFoundException("Name: Send map", "Message: Player not found");
        }

		// Check if the game has two registered players
		if (players.size() != 2) {
			throw new WaitForTwoPlayersBeforeMapSendingException("Name: Send map",
					"Message: Server should wait for two players finish their registration before allowing to send maps.");
		}

        // Check if it's the player's turn
        if (!playerLogic.checkPlayerTurn(players, playerID)) {
            throw new PlayerTurnException("Name: Send map", "Message: Not player's turn");
        }

        // Check if the player has already submitted a half map
        if (playerLogic.hasPlayerSubmittedMap(playerID)) {
            throw new MapAlreadySentException("Name: Send map", "Message: Map already submitted");
        }

        // Mark the player's map as submitted
        playerLogic.markPlayerMapSubmitted(playerID);

        // Handle player action and change turn
        playerLogic.handlePlayerAction(players, playerID);

        // Convert the PlayerhalfMap to GameMap and update the game map
        GameMap convertedGameMap = new GameMap().convertFromNetwork(playerHalfMap);
        gameLogic.updateGameMapWithHalfMap(gameID, convertedGameMap, playerID);
    }

}
