package components;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

import exceptions.ConvertPlayerException;
import gameobjectchange.PlayerStatus;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.PlayerState;
import network.ConvertFromNetwork;

public class Player implements ConvertFromNetwork<Player, PlayerState> {

	private String firstName;
	private String lastName;
	private String playerId;
	private String uaccount;
	private PlayerStatus state = PlayerStatus.ENEMYTURN;
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

	public Player() {

	}

	public Player(String playerId) {
		this.playerId = playerId;
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

	@Override
	public Player convertFromNetwork(PlayerState source) {
		if (source == null) {
			throw new ConvertPlayerException("Player from server is null");
		}
		this.firstName = source.getFirstName();
		this.lastName = source.getLastName();
		this.uaccount = source.getUAccount();
		this.playerId = source.getUniquePlayerID();
		this.setPlayerState(source.getState());
		return this;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("no events added");
		} else {
			changes.addPropertyChangeListener(listener);
		}
	}

	public void setPlayerState(EPlayerGameState source) {
		if (source != null) {
			PlayerStatus beforeStatus = this.state;
			this.state = this.state.convertFromNetwork(source);
			changes.firePropertyChange("Change turn", beforeStatus, this.state);

		} else {
			throw new ConvertPlayerException("PlayerState from server is null");
		}

	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, playerId, uaccount);
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
		Player other = (Player) obj;
		return Objects.equals(firstName, other.firstName) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(playerId, other.playerId) && Objects.equals(uaccount, other.uaccount);
	}

}
