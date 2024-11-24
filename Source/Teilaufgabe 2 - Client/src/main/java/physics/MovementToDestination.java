package physics;

import java.util.List;
import java.util.Set;

import asset.GameMap;
import exceptions.MovementIntoWaterException;

public class MovementToDestination implements MovementStrategy {
	private GameMap fullMap;
	private PathNode sourceNode; // starting point
	private List<Direction> dirs;
	private PathNode destination;
	private Set<Position> visited;
	private Dijkstra dijkstra;

	public MovementToDestination(GameMap fullMap, PathNode sourceNode, PathNode destination) {
		this.fullMap = fullMap;
		this.sourceNode = sourceNode;
		this.destination = destination;
		this.dijkstra = new Dijkstra(fullMap, sourceNode.getPosition(), destination.getPosition());
	}

	private void moveToDestination() throws MovementIntoWaterException {
		if (destination != null && (!sourceNode.getPosition().equals(destination.getPosition()))) {
			this.dijkstra.findShortestPath();
			this.dirs = this.dijkstra.generateDirection();
		}
	}

	@Override
	public List<Direction> sendDirection() {
		try {
			this.moveToDestination();
		} catch (MovementIntoWaterException e) {
			e.printStackTrace();
		}
		return this.dirs;
	}

	@Override
	public void setVisitedFields(Set<Position> visitedFields) {
		this.visited = visitedFields;

	}

	@Override
	public Set<Position> getVisitedFields() {
		return visited;
	}

}
