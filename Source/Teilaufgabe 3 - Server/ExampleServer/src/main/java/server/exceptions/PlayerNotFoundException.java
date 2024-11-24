package server.exceptions;

public class PlayerNotFoundException extends GenericExampleException {

	public PlayerNotFoundException(String errorName, String errorMessage) {
		super(errorName, errorMessage);
	}

}
