package components;

import asset.ClientMapNode;
import physics.PlayerMovement;
import physics.Position;
import terrain.TreasureState;

public class PlayerFigure {
	private String playerId;
	private Position position;
	private ClientMapNode node;
	private PlayerMovement movement;
	boolean treasureCollected;

	public PlayerFigure(String playerId) {
		this.playerId = playerId;
	}

	public PlayerFigure(String playerId, Position position, ClientMapNode node) {
		this(playerId);
		this.position = position;
		this.node = node;
		if (this.node.getTreasureState() == TreasureState.MyTreasurePresent) {
			treasureCollected = true;
		}
	}

	public void setMovement(PlayerMovement move) {
		this.movement = move;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void collectTreasure() {
		this.treasureCollected = true;
	}

	public boolean getTreasureCollected() {
		return this.treasureCollected;
	}

	public String getPlayerId() {
		return this.playerId;
	}

	public Position getPosition() {
		return this.position;
	}

	public PlayerMovement getPlayerMovement() {
		return this.movement;
	}

}
