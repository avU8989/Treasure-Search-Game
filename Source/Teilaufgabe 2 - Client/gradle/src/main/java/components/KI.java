package components;

import asset.ClientHalfMap;

public class KI {
	private PlayerFigure playerFigure;
	private ClientHalfMap halfMap;
	
	public KI() {
		
	}
	
	public KI(ClientHalfMap halfMap) {
		this.halfMap = halfMap;
	}
	
	public KI(PlayerFigure playerFigure, ClientHalfMap halfMap){
		this.playerFigure = playerFigure;
		this.halfMap = halfMap;
	}
}
