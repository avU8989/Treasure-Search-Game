package logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import gameobject.ClientPlayer;
import server.exceptions.NoPlayersPresentException;
import state.PlayerStatus;

public class PlayerLogic {

	private Map<String, Boolean> playerMapSubmissionStatus;

	public PlayerLogic() {
		this.playerMapSubmissionStatus = new HashMap<>();
	}

	public boolean verifyPlayer(Set<ClientPlayer> players, String playerId) {
		if (players.isEmpty() || players == null) {
			throw new NoPlayersPresentException("Name: Verify Player", "Message: Player should be registered");
		}

		return players.stream().anyMatch(player -> player.getPlayerId().equals(playerId));
	}

	public boolean checkPlayerTurn(Set<ClientPlayer> players, String playerId) {
		Optional<ClientPlayer> optionalPlayer = players.stream().filter(c -> c.getPlayerId().equals(playerId))
				.findAny();

		if (optionalPlayer.isPresent()) {
			ClientPlayer player = optionalPlayer.get();
			if (player.getState() == PlayerStatus.YOURTURN) {
				return true;
			}
		}

		return false;
	}


	public ClientPlayer handlePlayerAction(Set<ClientPlayer> players, String playerId) {
		ClientPlayer player = null;
		if (this.checkPlayerTurn(players, playerId)) {
			Optional<ClientPlayer> optionalPlayer = players.stream().filter(c -> c.getPlayerId().equals(playerId))
					.findAny();

			if (optionalPlayer.isPresent()) {
				player = optionalPlayer.get();
				if (player.getState() == PlayerStatus.YOURTURN) {
					player.setState(PlayerStatus.ENEMYTURN);
				}
			}
		}
		return player;
	}

	public void initializePlayerMapSubmissionStatus(Set<ClientPlayer> players) {
		for (ClientPlayer player : players) {
			if (!playerMapSubmissionStatus.containsKey(player.getPlayerId())) {
				playerMapSubmissionStatus.put(player.getPlayerId(), false);
			}
		}
	}

	public boolean hasPlayerSubmittedMap(String playerId) {
		return playerMapSubmissionStatus.getOrDefault(playerId, false);
	}

	public void markPlayerMapSubmitted(String playerId) {
		playerMapSubmissionStatus.put(playerId, true);
	}




}
