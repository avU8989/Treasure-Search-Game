package physics;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asset.GameMap;
import exceptions.MovementException;

public class MovementLogic {
	private MovementStrategy strategy;
	private GameMap fullMap;
	private PlayerMovement movement;
	public List<Direction> directions;
	private static Logger logger = LoggerFactory.getLogger(MovementLogic.class);

	public MovementLogic(GameMap fullMap) {
		this.directions = new LinkedList<>();
		this.fullMap = fullMap;
		this.strategy = new MovementToUnknownTreasure(fullMap, fullMap.getFigure().getPosition());
	}

	public MovementLogic(GameMap fullMap, MovementStrategy strategy) {
		this.directions = new LinkedList<>();
		this.fullMap = fullMap;
		this.strategy = strategy;
	}

	public void startMoving() {
		this.directions = this.strategy.sendDirection();
	}

	public void setVisitedFields(Set<Position> visitedFields) {
		if (visitedFields.isEmpty()) {
			logger.warn("There are no visited Fields");
		}
		this.strategy.setVisitedFields(visitedFields);
	}

	public void setStrategy(GameMap fullMap) {
		if (fullMap.getFigure().getTreasureCollected()) {
			if (fullMap.getEnemyFort() != null) {
				logger.info("Set the pathfinding on the strategy with known enemy Fort");
				this.strategy = new MovementToDestination(fullMap, new PathNode(fullMap.getFigure().getPosition()),
						new PathNode(fullMap.getEnemyFort()));
			} else {
				logger.info("Collected the Treasure therefore searching for unknown enemyFort");
				this.strategy = new MovementToUnknownEnemyFort(fullMap, fullMap.getFigure().getPosition());
			}
		} else {
			logger.info("We found the treasure, time to dig it up");
			this.strategy = new MovementToDestination(fullMap, new PathNode(fullMap.getFigure().getPosition()),
					new PathNode(fullMap.getTreasure()));

		}
		this.directions.clear();
	}

	public Direction sendDirection() throws MovementException {
		Direction dir = null;
		if (!this.directions.isEmpty()) {
			logger.debug("This are our movement actions: {}", this.directions.toString());
			dir = this.directions.remove(0);
		} else {
			throw new MovementException("Can't send movement");
		}
		return dir;
	}

	public MovementStrategy getStrategy() {
		return this.strategy;
	}

	public Set<Position> getVisited() {
		return this.strategy.getVisitedFields();
	}

	public PlayerMovement getPlayerMovement() {
		return this.movement;
	}

	public List<Direction> getDirections() {
		return this.directions;
	}

}
