package server.main;

import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gameobject.ClientPlayer;
import gameobject.Game;
import logic.GameLogic;
import logic.MovementLogic;
import logic.PlayerLogic;
import messagesbase.ResponseEnvelope;
import messagesbase.UniqueGameIdentifier;
import messagesbase.UniquePlayerIdentifier;
import messagesbase.messagesfromclient.PlayerHalfMap;
import messagesbase.messagesfromclient.PlayerRegistration;
import messagesbase.messagesfromserver.GameState;
import server.exceptions.GenericExampleException;
import server.exceptions.PlayerNotFoundException;
import service.MapSubmissionService;
import state.PlayerStatus;

@RestController
@RequestMapping(value = "/games")
public class ServerNetwork {
	private GameLogic gameLogic;
	private MovementLogic movementLogic;
	private PlayerLogic playerLogic;
	private MapSubmissionService mapService;
	private static Logger logger = LoggerFactory.getLogger(ServerNetwork.class);
	
	public ServerNetwork() {
		this.gameLogic = new GameLogic();
		this.movementLogic = new MovementLogic();
		this.playerLogic = new PlayerLogic();
		mapService = new MapSubmissionService(gameLogic, playerLogic);
	}

	
	
	@RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody UniqueGameIdentifier newGame(
			@RequestParam(required = false, defaultValue = "false", value = "enableDebugMode") boolean enableDebugMode,
			@RequestParam(required = false, defaultValue = "false", value = "enableDummyCompetition") boolean enableDummyCompetition) {
		boolean showExceptionHandling = false;
		if (showExceptionHandling) {
			throw new GenericExampleException("Name: Something", "Message: went totally wrong");
		}
		String gameId = gameLogic.createGame();
		UniqueGameIdentifier gameIdentifier = new UniqueGameIdentifier(gameId);
		return gameIdentifier;

		// you will need to include additional logic, e.g., additional classes
		// which create, store, validate, etc. exchanged data	
	}

	@RequestMapping(value = "/{gameID}/players", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope<UniquePlayerIdentifier> registerPlayer(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerRegistration playerRegistration) {
		UniquePlayerIdentifier newPlayerID = new UniquePlayerIdentifier(UUID.randomUUID().toString());
	
		ResponseEnvelope<UniquePlayerIdentifier> playerIDMessage = new ResponseEnvelope<>(newPlayerID);
		ClientPlayer player = new ClientPlayer(newPlayerID.getUniquePlayerID(),
				playerRegistration.getStudentFirstName(), playerRegistration.getStudentLastName(),
				playerRegistration.getStudentUAccount(), PlayerStatus.ENEMYTURN);

		gameLogic.registerPlayer(gameID, player);
		return playerIDMessage;


		// you will need to include additional logic, e.g., additional classes
		// which create, store, validate, etc. exchanged data
	}

	@RequestMapping(value = "/{gameID}/halfmaps", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope receiveMap(@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @RequestBody PlayerHalfMap playerHalfMap) {
		// convert the PlayerhalfMap
		mapService.processMapSubmission(gameID, playerHalfMap);
		return new ResponseEnvelope();
	}

	@RequestMapping(value = "/{gameID}/states/{playerID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody ResponseEnvelope requestGameState(
			@Validated @PathVariable UniqueGameIdentifier gameID,
			@Validated @PathVariable UniquePlayerIdentifier playerID)
	{
		Set<ClientPlayer> players = gameLogic.retrievePlayers(gameID);
		boolean playerExists = playerLogic.verifyPlayer(players, playerID.getUniquePlayerID());

		if (playerExists) {
			Game game = gameLogic.retrieveGame(gameID, playerID.getUniquePlayerID());
			Game newGame = gameLogic.hideEnemyFortPosition(game, playerID.getUniquePlayerID());
			Game newGame3 = gameLogic.hideEnemyPlayerID(newGame, playerID.getUniquePlayerID());

			if (newGame3.getMap() != null && newGame3.getMap().getMap() != null) {
				logger.info(newGame3.getMap().toString());
				logger.info(newGame3.getPlayers().toString());
			}

			GameState state = game.convertToNetwork(newGame3);
			ResponseEnvelope<GameState> gameState = new ResponseEnvelope<>(state);
			return gameState;
		} else {
			throw new PlayerNotFoundException("Name: Request GameState", "Message: Player not found");
		}
		// you will need to include additional logic, e.g., additional classes
		// which create, store, validate, etc. exchanged data	
	}

	@ExceptionHandler({ GenericExampleException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericExampleException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());

		// reply with 200 OK as defined in the network documentation
		// Side note: We only do this here for simplicity reasons. For future projects,
		// you should check out HTTP status codes and
		// what they can be used for. Note, the WebClient used during the Client
		// implementation can react
		// to them using the .onStatus(...) method.
		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}
