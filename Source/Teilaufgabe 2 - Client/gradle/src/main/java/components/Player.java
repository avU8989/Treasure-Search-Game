package components;

import java.beans.PropertyChangeSupport;

import gameObjectState.PlayerStatus;
import messagesbase.messagesfromserver.PlayerState;
import network.ConvertFromNetwork;

public class Player implements ConvertFromNetwork<PlayerState> {
	private String firstName;
	private String lastName;
	private String playerId;
	private String uaccount;
	private PlayerStatus state = PlayerStatus.ENEMYTURN;
	private PlayerFigure figure;
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public Player() {

	}

	public Player(String firstName, String lastName, String uaccount) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.uaccount = uaccount;
	}

	public Player(String playerId, String firstName, String lastName, String uaccount) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.uaccount = uaccount;
		this.playerId = playerId;
	}

	public Player(String playerId, String firstName, String lastName, String uaccount, PlayerStatus state) {
		this.playerId = playerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.uaccount = uaccount;
		this.state = state;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getUaccount() {
		return this.uaccount;
	}

	public void setPlayerID(String playerIdentifier) {
		this.playerId = playerIdentifier;
	}

	public String getPlayerId() {
		return this.playerId;
	}

	public PlayerStatus getPlayerState() {
		return this.state;
	}

	public void setPlayerState(PlayerStatus state) {
		this.state = state;
	}

	@Override
	public void convertFromNetwork(PlayerState source) {
		Player player = new Player();
		this.firstName = source.getFirstName();
		this.lastName = source.getLastName();
		this.uaccount = source.getUAccount();
		this.playerId = source.getUniquePlayerID();
		this.updatePlayer(source, player);
	}

	public void updatePlayer(PlayerState source, Player player) {
		PlayerStatus beforeStatus = this.state;
		this.state = player.state.convertPlayerStateFromNetwork(source.getState());

		changes.firePropertyChange("Change turn", beforeStatus, this.state);

	}

}
