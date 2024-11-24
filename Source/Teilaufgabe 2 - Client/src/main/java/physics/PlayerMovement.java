package physics;

import exceptions.ConvertMovementException;
import messagesbase.messagesfromclient.EMove;
import messagesbase.messagesfromclient.PlayerMove;
import network.ConvertToNetwork;

public class PlayerMovement implements ConvertToNetwork<PlayerMove, PlayerMovement> {
	private String playerId;
	private Direction direction;
	private boolean legitMove;

	public PlayerMovement(String playerId) {
		this.playerId = playerId;
	}

	public PlayerMovement(Direction direction, String playerId) {
		this.direction = direction;
		this.playerId = playerId;
	}

	private boolean isLegitMove() {
		return false;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public void setDirection(Direction dir) {
		this.direction = dir;
	}

	@Override
	public PlayerMove convertToNetwork(PlayerMovement source) {
		if (source == null) {
			throw new ConvertMovementException("Player movement from client is null");
		}

		switch (source.direction) {
		case DOWN:
			return PlayerMove.of(playerId, EMove.Down);
		case UP:
			return PlayerMove.of(playerId, EMove.Up);
		case RIGHT:
			return PlayerMove.of(playerId, EMove.Right);
		case LEFT:
			return PlayerMove.of(playerId, EMove.Left);
		default:
			throw new ConvertMovementException("Player movement from client is invalid");

		}
	}

}
