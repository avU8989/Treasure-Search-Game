package gameobject;

import converter.ConvertFromNetwork;
import converter.ConvertToNetwork;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.PlayerState;
import server.exceptions.ConverterException;
import state.PlayerStatus;

public class ClientPlayer
		implements ConvertFromNetwork<ClientPlayer, PlayerState>, ConvertToNetwork<PlayerState, ClientPlayer> {
	private final String playerId;
	private final String firstName;
	private final String lastName;
	private PlayerStatus state;
	private final String uaccount;
	
	public ClientPlayer(String playerId, String firstName, String lastName, String uaccount) {
		this.playerId = playerId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.uaccount = uaccount;
	}

	public ClientPlayer(String playerId, String firstName, String lastName, String uaccount, PlayerStatus playerState) {
		this(playerId, firstName, lastName, uaccount);
		this.state = playerState;
	}

	public String getPlayerId() {
		return this.playerId;
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

	public PlayerStatus getState() {
		return this.state;
	}

	@Override
	public PlayerState convertToNetwork(ClientPlayer target) {
		if (target == null) {
			throw new ConverterException("Name: NullCheck Register", "Message: Player from client is null");
		}
		String newPlayerId = target.playerId;
		String newFirstName = target.firstName;
		String newLastName = target.lastName;
		String newUaccount = target.uaccount;
		EPlayerGameState status;
		if (this.state != null) {
			status = target.state.convertToNetwork(this.state);
		} else {
			status = EPlayerGameState.MustWait;
		}
		return new PlayerState(newFirstName, newLastName, newUaccount, status, new UniquePlayerIdentifier(newPlayerId),
				false);
	}

	public void setState(PlayerStatus state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "ClientPlayer [playerId=" + playerId + "]";
	}

	@Override
	public ClientPlayer convertFromNetwork(PlayerState target) {
		PlayerStatus status = PlayerStatus.ENEMYTURN;
		PlayerStatus newStatus = status.convertFromNetwork(target.getState());
		return new ClientPlayer(target.getUniquePlayerID(), target.getFirstName(), target.getLastName(),
				target.getUAccount(), newStatus);

	}


}
