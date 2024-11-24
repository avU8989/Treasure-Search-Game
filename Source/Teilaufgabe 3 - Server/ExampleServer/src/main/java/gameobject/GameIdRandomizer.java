package gameobject;

import java.util.UUID;

public class GameIdRandomizer {
    public final String gameId;
    private static final int ID_LENGTH = 5; 
    public GameIdRandomizer() {
        String id = UUID.randomUUID().toString().replace("-", "");
        this.gameId = id.substring(0, Math.min(ID_LENGTH,id.length()));
    }

	public String getGameId() {
    	return this.gameId;
    }
}
