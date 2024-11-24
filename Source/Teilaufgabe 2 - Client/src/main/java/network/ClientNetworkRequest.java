package network;

import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import asset.ClientHalfMapValidator;
import components.Player;
import messagesbase.ResponseEnvelope;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.ERequestState;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerHalfMapNode;
import messagesbase.messagesfromclient.PlayerMove;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import physics.PlayerMovement;
import reactor.core.publisher.Mono;

/**
 * @author Anh
 *
 */
public class ClientNetworkRequest {
	private static WebClient webClient;
	private String serverBaseUrl;
	private String gameId;
	private String gameStateId;
	private String playerId;
	private String gameMode;

	public ClientNetworkRequest() {
		webClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
	}

	public ClientNetworkRequest(String gameIdentifier, String serverBaseUrl) {
		this.gameId = gameIdentifier;
		this.serverBaseUrl = serverBaseUrl;

		webClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
	}

	public ClientNetworkRequest(String gameIdentifier, String serverBaseUrl, String gameMode) {
		this(gameIdentifier, serverBaseUrl);
		this.gameMode = gameMode;

		webClient = WebClient.builder().baseUrl(serverBaseUrl + "/games")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE).build();
	}

	public void requestClientRegistration(Player playerRegistration) {
		PlayerRegistration playerReg = new PlayerRegistration(playerRegistration.getFirstName(),
				playerRegistration.getLastName(), playerRegistration.getUaccount());
		Mono<ResponseEnvelope> webAccess = webClient.method(HttpMethod.POST).uri("/" + gameId + "/players")
				.body(BodyInserters.fromValue(playerReg)).retrieve().bodyToMono(ResponseEnvelope.class);

		ResponseEnvelope<UniquePlayerIdentifier> resultReg = webAccess.block();

		if (resultReg.getState() == ERequestState.Error) {
			System.err.println("Client error, errormessage: " + resultReg.getExceptionMessage());
		} else {
			UniquePlayerIdentifier uniqueId = resultReg.getData().get();
			this.playerId = uniqueId.getUniquePlayerID();
		}

	}

	public boolean requestSendMap(ClientHalfMapValidator halfMap) {

		Collection<PlayerHalfMapNode> nodes = halfMap.convertToNetwork(halfMap.getMap());
		PlayerHalfMap playerHalfMap = new PlayerHalfMap(this.playerId, nodes);
		Mono<ResponseEnvelope> webAccess = webClient.method(HttpMethod.POST).uri("/" + this.gameId + "/halfmaps")
				.body(BodyInserters.fromValue(playerHalfMap)).retrieve().bodyToMono(ResponseEnvelope.class);

		ResponseEnvelope<PlayerHalfMap> resultHalfMap = webAccess.block();

		if (resultHalfMap.getState() == ERequestState.Error) {
			System.err.println("Client error, errormessage: " + resultHalfMap.getExceptionMessage());
			return false;
		} else {
			System.out.println("MAP SENT");
		}
		return true;

	}

	public boolean requestMovementAction(PlayerMovement movement) {
		PlayerMove playerMove = movement.convertToNetwork(movement);
		Mono<ResponseEnvelope> webAccess = webClient.method(HttpMethod.POST).uri("/" + this.gameId + "/moves")
				.body(BodyInserters.fromValue(playerMove)).retrieve().bodyToMono(ResponseEnvelope.class);

		ResponseEnvelope<PlayerMove> resultPlayerMove = webAccess.block();

		if (resultPlayerMove.getState() == ERequestState.Error) {
			System.err.println("Client error, errormessage: " + resultPlayerMove.getExceptionMessage());
			return false;
		}
		return true;
	}

	public String getPlayerId() {
		return this.playerId;
	}

	public GameState requestGameState() {
		GameState uniqueGameId = new GameState();
		Mono<ResponseEnvelope> webAccess = webClient.method(HttpMethod.GET).uri("/" + gameId + "/states/" + playerId)
				.retrieve().bodyToMono(ResponseEnvelope.class);

		// WebClient support asynchronous message exchange. In SE1 we use a synchronous
		// one for the sake of simplicity. So calling block is fine.
		ResponseEnvelope<GameState> requestResult = webAccess.block();

		// always check for errors, and if some are reported, at least print them to the
		// console (logging should always be preferred!)
		// so that you become aware of them during debugging! The provided server gives
		// you constructive error messages.
		if (requestResult.getState() == ERequestState.Error) {
			System.err.println("Client error, errormessage: " + requestResult.getExceptionMessage());
		} else {
			uniqueGameId = requestResult.getData().get();
			this.gameStateId = uniqueGameId.getGameStateId();
		}

		return uniqueGameId;
	}

	public String getGameId() {
		return this.gameId;
	}

	public String getGameStateID() {
		return this.gameStateId;
	}
}
