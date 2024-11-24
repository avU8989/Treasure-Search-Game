package gamestatechange;

import java.beans.PropertyChangeListener;

import components.Game;

public class GameStateVisualisation {
	private GameController gameController;

	public GameStateVisualisation(Game game, GameController gameController) {
		this.gameController = gameController;
		game.addPropertyChangeListener(gameModelChangedListener);

	}

	final PropertyChangeListener gameModelChangedListener = event -> {
		Object model = event.getSource();
		Object newValue = event.getNewValue();

		if (model instanceof Game) {
			Game castedModel = (Game) model;
			System.out.println("UPDATING GAMESTATE:");

			if (castedModel.getPlayer() != null) {
				System.out.println("[PlayerStatus: " + castedModel.getPlayer().getPlayerState().toString() + "]");
				System.out.println("[PlayerID: " + castedModel.getPlayer().getPlayerId().toString() + "]");
				System.out.println("[GameID: " + castedModel.getGameId() + "]");
			}

			if (castedModel.getGameMap() != null) {
				if (castedModel.getGameMap().getFigure() != null) {
					System.out.println("YOUR POSITION: " + castedModel.getGameMap().getFigure().getPosition());

				}
				if (castedModel.getGameMap().getTreasure() == null) {
					System.out.println("TREASURESTATE: ???");

				} else {
					System.out.println("TREASURESTATE:" + castedModel.getGameMap().getTreasure());
				}
				if (castedModel.getGameMap().getFigure() != null) {
					if (castedModel.getGameMap().getFigure().getTreasureCollected() == true) {
						System.out.println("TREASURE COLLECTED");
					} else {
						System.out.println("TREASURE NOT COLLECTED");
					}
				}
				System.out.println(castedModel.getGameMap());
			}
		}
	};

}
