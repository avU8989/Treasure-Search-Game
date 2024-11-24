package physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import asset.ClientMapNode;
import asset.GameMap;
import exceptions.MovementIntoWaterException;
import terrain.TerrainType;

public class Dijkstra {
	private GameMap fullMap;
	private PathNode sourceNode; // starting point
	private PathNode destination;
	private Set<Position> visited;
	private LinkedHashMap<PathNode, Path> adjacency;
	private List<PathNode> shortestPath;
	private List<PathNode> possiblePaths;
	private final int penaltyMovementAction = 2;
	private final int grassToGrass = 2;
	private final int grassToMountain = 3;
	private final int mountainToMountain = 4;
	private final int mountainToGrass = 3;
	private static Logger logger = LoggerFactory.getLogger(Dijkstra.class);

	// starting from fort
	public Dijkstra(GameMap fullMap, Position currentPos, Position destination) {
		this.fullMap = fullMap;
		this.sourceNode = new PathNode(fullMap.getFigure().getPosition(),
				fullMap.getMap().get(fullMap.getFigure().getPosition()));
		this.destination = new PathNode(destination);
		this.adjacency = new LinkedHashMap<>();
		this.adjacency.put(this.sourceNode, new Path());
		this.possiblePaths = new ArrayList<>();
		this.visited = new HashSet<>();
		this.shortestPath = new ArrayList<>();

	}

	// starting from current Position
	public Dijkstra(GameMap fullMap, Position currentPos) {
		this.fullMap = fullMap;
		this.sourceNode = new PathNode(currentPos, fullMap.getMap().get(currentPos));
		this.destination = new PathNode();
		this.adjacency = new LinkedHashMap<>();
		this.adjacency.put(this.sourceNode, new Path());
		this.possiblePaths = new ArrayList<>();
		this.shortestPath = new ArrayList<>();
		this.visited = new HashSet<>();

	}

	private void findAdjacentNodes() {
		int directionX[] = { 1, 0, -1, 0 };
		int directionY[] = { 0, 1, 0, -1 };
		for (var entry : fullMap.getMap().entrySet()) {
			if (entry.getValue().getTerrain() == TerrainType.WATER) {
				continue;
			}
			PathNode path = new PathNode(entry.getKey(), entry.getValue());
			this.adjacency.put(path, new Path());
			for (int i = 0; i < 4; ++i) {
				int newX = entry.getKey().getCoordinateX() + directionX[i];
				int newY = entry.getKey().getCoordinateY() + directionY[i];

				if (fullMap.getWidth() > 0) {
					if (newX < 0 || newY < 0 || newY > 4 || newX > 19) {
						continue;
					}
				} else if (fullMap.getHeight() > 0) {
					if (newX < 0 || newY < 0 || newY > 9 || newX > 9) {
						continue;
					}
				}

				Position newPos = new Position(newX, newY);// set the coordinates
				ClientMapNode node = fullMap.getMap().get(newPos);

				if (node.getTerrain() == TerrainType.WATER) {
					continue;
				}
				PathNode pathNode = new PathNode(newPos, node);

				if (newPos.equals(this.sourceNode.getPosition())) {
					pathNode.setWeight(0);
				}
				this.adjacency.get(path).addUnvisited(pathNode);

			}
		}
	}

	public List<PathNode> findShortestPath() throws MovementIntoWaterException {
		this.findAdjacentNodes();
		PriorityQueue<PathNode> queue = new PriorityQueue<>((u, v) -> (u.getWeight() - v.getWeight()));
		Set<PathNode> visited = new HashSet<>();
		this.sourceNode.setWeight(0);
		this.possiblePaths.add(this.sourceNode);
		queue.add(this.sourceNode);

		while (!queue.isEmpty()) {
			PathNode top = queue.poll();

			if (top.getMapNode().getTerrain() == TerrainType.WATER) {
				continue;
			}

			if (top.getPosition().equals(destination.getPosition())) {
				break;
			}

			if (visited.contains(top)) {
				continue;
			}

			visited.add(top);
			int distance = top.getWeight();

			for (int i = 0; i < this.adjacency.get(top).getUnvisitedSize(); ++i) {
				PathNode adjPathNode = this.adjacency.get(top).getUnvisited().get(i);
				int movementActions = this.evaluateMovementAction(top.getPosition(), adjPathNode.getPosition());
				int newDistance = distance + movementActions;
				if (!possiblePaths.contains(adjPathNode)) {
					logger.info("Possible path already visited that Position");
					if (this.visited.contains(adjPathNode.getPosition())) {
						// if we already visited that PathNode crank the weight to that PathNode up
						movementActions += penaltyMovementAction;
					}
					if (newDistance < adjPathNode.getWeight()) {
						adjPathNode.setWeight(newDistance);
						adjPathNode.setPrevious(top);
						if (adjPathNode.getPosition().equals(destination.getPosition())) {
							this.destination = adjPathNode;
						}
						this.adjacency.put(adjPathNode, this.adjacency.get(adjPathNode));
						this.adjacency.get(top).updatePathNode(i, adjPathNode, newDistance);
						if (!this.adjacency.get(top).getVisited().contains(adjPathNode)) {
							this.adjacency.get(top).addVisited(adjPathNode);
						}
						this.possiblePaths.add(adjPathNode);
						queue.add(adjPathNode);
					}
				}
			}
		}

		// Build the shortest path by starting at the destination and following the
		// parent nodes
		PathNode currentNode = this.destination;
		while (currentNode != this.sourceNode && currentNode.getPrevious() != null) {
			this.shortestPath.add(0, currentNode);
			currentNode = currentNode.getPrevious();
		}
		this.shortestPath.add(0, this.sourceNode);

		return this.shortestPath;
	}

	public void setDestination(PathNode destination) {
		this.destination = destination;
	}

	public PathNode getDestination() {
		return this.destination;
	}

	public List<Direction> generateDirection() {
		List<Direction> dir = new LinkedList<>();
		for (int i = 0; i < this.shortestPath.size(); ++i) {
			PathNode node = this.shortestPath.get(i);

			int movementActions = 0;
			try {
				movementActions = this.evaluateMovementAction(this.sourceNode.getPosition(), node.getPosition());
			} catch (MovementIntoWaterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (movementActions == 0) {
				logger.error("There will be no movement actions generated from the Dijsktra");
			}

			if (node.getPosition().equals(sourceNode.getPosition())) {
				continue;
			}

			if (fullMap.getMap().get(node.getPosition()).getTerrain() == TerrainType.WATER) {
				continue;
			}

			if (node.getPosition().getCoordinateX() == this.sourceNode.getPosition().getCoordinateX()) {
				if ((node.getPosition().getCoordinateY() - this.sourceNode.getPosition().getCoordinateY()) > 0) {
					while (movementActions != 0) {
						dir.add(Direction.DOWN);
						--movementActions;
					}
				} else if ((node.getPosition().getCoordinateY() - this.sourceNode.getPosition().getCoordinateY()) < 0) {
					while (movementActions != 0) {
						dir.add(Direction.UP);
						--movementActions;
					}

				}
			}

			if (node.getPosition().getCoordinateY() == this.sourceNode.getPosition().getCoordinateY()) {
				if ((node.getPosition().getCoordinateX() - this.sourceNode.getPosition().getCoordinateX()) > 0) {
					while (movementActions != 0) {
						dir.add(Direction.RIGHT);
						--movementActions;
					}

				} else if ((node.getPosition().getCoordinateX() - this.sourceNode.getPosition().getCoordinateX()) < 0) {
					while (movementActions != 0) {
						dir.add(Direction.LEFT);
						--movementActions;
					}

				}
			}

			this.sourceNode = node;
		}

		if (dir.isEmpty()) {
			logger.error("No directions will be sent from Dijsktra");
		}
		return dir;

	}

	private int evaluateMovementAction(Position currentPos, Position nextPos) throws MovementIntoWaterException {
		int movementAction = 0;
		if (fullMap.getMap().get(currentPos).getTerrain() == TerrainType.GRASS) {
			switch (fullMap.getMap().get(nextPos).getTerrain()) {
			case GRASS:
				movementAction = grassToGrass; // 1 leaving the grass 1 entering the grass
				break;

			case MOUNTAIN:
				movementAction = grassToMountain; // 1 leaving the grass 2 entering the mountain
				break;
			default:
				throw new MovementIntoWaterException("Going swimming");
			}
		}

		if (fullMap.getMap().get(currentPos).getTerrain() == TerrainType.MOUNTAIN) {
			switch (fullMap.getMap().get(nextPos).getTerrain()) {
			case GRASS:
				movementAction = mountainToGrass; // 2 leaving the mountain 1 entering the grass
				break;

			case MOUNTAIN:
				movementAction = mountainToMountain; // 2 leaving the mountain 2 entering the mountain
				break;
			default:
				throw new MovementIntoWaterException("Going swimming");
			}

		}
		return movementAction;
	}

	public void clearPossiblePath() {
		this.possiblePaths.clear();
	}

	public void clearShortestPath() {
		this.shortestPath.clear();
	}

	public void clearAdjacent() {
		this.adjacency.clear();
	}

	public List<PathNode> getShortestPath() {
		return this.shortestPath;
	}

	public PathNode getSourceNode() {
		return this.sourceNode;
	}

	public void setSourceNode(PathNode currentPosition) {
		this.sourceNode = currentPosition;
	}

	public void setGameMap(GameMap fullMap) {
		this.fullMap = fullMap;
	}

	public void setVisited(Set<Position> visited) {
		this.visited = visited;

	}

}
