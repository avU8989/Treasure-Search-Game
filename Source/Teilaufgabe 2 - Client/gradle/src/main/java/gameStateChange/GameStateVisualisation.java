package gameStateChange;

import java.beans.PropertyChangeListener;

import asset.GameMap;
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

		System.out.println("Display of the changed value:" + newValue);

		if (model instanceof Game) {
			System.out.println("You even get the whole model");

			Game castedModel = (Game) model;
			System.out.println("After casting I can accesss the data of the model" + castedModel.getClass());
		}
	};

	public String print(GameMap fullMap) {
		String ret = fullMap.toString();
		return ret;

	}
}
