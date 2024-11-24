package physics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import asset.GameMap;
import exceptions.MovementIntoWaterException;
import terrain.FortDirection;
import terrain.TerrainType;

public class MovementToUnknownTreasure extends AbstractMapSideEvaluator implements MovementStrategy {
	private GameMap fullMap;
	private Position sourceNode; // starting point
	private Dijkstra dijkstra;
	private Position destination;
	private Set<Position> visited;
	private Queue<Position> treasureCanyon;
	private Set<Position> unvisitedGrassFields;
	private final int grassStrategy = 4;
	private final int mountainDistanceStrategy = 2;

	public MovementToUnknownTreasure(GameMap fullMap, Position sourceNode) {
		super(fullMap);
		this.treasureCanyon = new LinkedList<>();
		this.visited = new HashSet<>();
		this.sourceNode = sourceNode;
		this.fullMap = fullMap;
		this.dijkstra = new Dijkstra(fullMap, sourceNode);
		this.unvisitedGrassFields = new HashSet<>(super.filterTerrainPositionOfHalfMap(fullMap, TerrainType.GRASS));
	}

	private boolean peakForTreasure(Position mountainPos) {
		int[] directionX = { 1, 1, 1, 0, 0, -1, -1, -1 };
		int[] directionY = { 1, 0, -1, 1, -1, 0, 1, 1 };
		int newX = 0;
		int newY = 0;
		int grassCounter = 0;
		int waterCounter = 0;
		int mountainCounter = 0;
		for (int i = 0; i < directionX.length; ++i) {
			newX = mountainPos.getCoordinateX() + directionX[i];
			newY = mountainPos.getCoordinateY() + directionY[i];
			Position newP = new Position(newX, newY);
			if (super.fortSide == FortDirection.LEFTSIDE) {
				if (newX < 0 || newY < 0 || newX > super.HALF_WIDTH_HORIZONTAL || newY >= super.MAX_HEIGHT_HORIZONTAL
						|| newX > super.MAX_WIDTH_HORIZONTAL) {
					continue;

				}
			}
			if (super.fortSide == FortDirection.RIGHTSIDE) {
				if (newX < 0 || newY < 0 || newX <= super.HALF_WIDTH_HORIZONTAL || newY >= super.MAX_HEIGHT_HORIZONTAL
						|| newX > super.MAX_WIDTH_HORIZONTAL) {
					continue;

				}
			}

			if (super.fortSide == FortDirection.BOTTOM) {
				if (newX < 0 || newY < 0 || newX > super.MAX_WIDTH_VERTICAL || newY < super.HALF_HEIGHT_VERTICAL
						|| newY > super.MAX_HEIGHT_VERTICAL) {
					continue;

				}
			}

			if (super.fortSide == FortDirection.TOP) {
				if (newX < 0 || newY < 0 || newX > super.MAX_WIDTH_VERTICAL || newY >= super.HALF_HEIGHT_VERTICAL
						|| newY > super.MAX_HEIGHT_VERTICAL) {
					continue;

				}
			}

			if (fullMap.getMap().get(newP).getTerrain() == TerrainType.GRASS) {
				this.visited.add(newP);
				this.unvisitedGrassFields.remove(newP);
				++grassCounter;
			}

			if (fullMap.getMap().get(newP).getTerrain() == TerrainType.MOUNTAIN) {
				++mountainCounter;
			}

			if (fullMap.getMap().get(newP).getTerrain() == TerrainType.WATER) {
				++waterCounter;
			}
		}

		if (grassCounter >= grassStrategy) {
			return true;
		}

		return false;

	}

	private void checkMountainPeaks() {
		List<Position> mountains = new ArrayList<>(
				super.filterTerrainPositionOfHalfMap(this.fullMap, TerrainType.MOUNTAIN));

		// Calculate distances between starting point and each destination
		Map<Position, Double> distances = new HashMap<>();

		double distance = 0;
		for (int i = 0; i < mountains.size(); ++i) {
			Position currentMountain = mountains.get(i);
			double minDistance = Double.MAX_VALUE;
			for (int j = i + 1; j < mountains.size() - 1; ++j) {
				Position nextMountain = mountains.get(j);
				distance = Math.sqrt(Math.pow(currentMountain.getCoordinateX() - nextMountain.getCoordinateX(), 2)
						+ Math.pow(currentMountain.getCoordinateY() - nextMountain.getCoordinateY(), 2));

				if (distance < minDistance) {
					minDistance = distance;
				}
			}
			distances.put(currentMountain, distance);

		}

		// sort the distance closes to the nearest mountains
		mountains = distances.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue))
				.map(Map.Entry::getKey).collect(Collectors.toList());

		for (int i = 0; i < mountains.size() - 1; ++i) {
			int dx = mountains.get(i).getCoordinateX() - mountains.get(i + 1).getCoordinateX();
			int dy = mountains.get(i).getCoordinateY() - mountains.get(i + 1).getCoordinateY();

			// get the distance between two of the mountain nodes and see if they are worth
			// visiting
			double sum = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

			if (sum > mountainDistanceStrategy) {
				if (!mountains.isEmpty()) {
					if (!visited.contains(mountains.get(i))) {
						if (peakForTreasure(mountains.get(i))) {
							treasureCanyon.add(mountains.get(i));
						}
					}
				}
			}
		}

	}

	private boolean lookingForDestination() {
		if (!treasureCanyon.isEmpty()) {
			this.destination = treasureCanyon.poll();
			this.dijkstra.setDestination(new PathNode(this.destination));
			return true;
		}

		return false;
	}

	@Override
	public List<Direction> sendDirection() {
		List<Direction> dirs = new ArrayList<>();

		checkMountainPeaks();

		try {
			dirs.addAll(findPathToMountains());
		} catch (MovementIntoWaterException e) {
			e.printStackTrace();
		}

		if (treasureCanyon.isEmpty()) {
			try {
				dirs.addAll(findPathToGrass());
			} catch (MovementIntoWaterException e) {
				e.printStackTrace();
			}
		}

		return dirs;
	}

	private List<Direction> findPathToMountains() throws MovementIntoWaterException {
		List<Direction> dirs = new ArrayList<>();
		while (lookingForDestination()) {
			dijkstra.setVisited(visited);
			List<PathNode> pathToMountain = this.dijkstra.findShortestPath();
			if (!pathToMountain.isEmpty()) {
				dirs.addAll(dijkstra.generateDirection());
				updateVisitedPositions(pathToMountain);
				resetDijkstra(dijkstra.getSourceNode().getPosition());
			}
		}
		return dirs;
	}

	private List<Direction> findPathToGrass() throws MovementIntoWaterException {
		List<Direction> dirs = new ArrayList<>();
		for (var entry : unvisitedGrassFields) {
			dijkstra.setDestination(new PathNode(entry));
			dijkstra.setVisited(visited);
			List<PathNode> pathToGrass = this.dijkstra.findShortestPath();
			if (!pathToGrass.isEmpty()) {
				dirs.addAll(dijkstra.generateDirection());
				for (var visitedFields : pathToGrass) {
					if (this.unvisitedGrassFields.contains(visitedFields.getPosition())) {
						this.visited.add(visitedFields.getPosition());
					}
				}
				resetDijkstra(dijkstra.getSourceNode().getPosition());
			}
		}
		return dirs;
	}

	private void updateVisitedPositions(List<PathNode> path) {
		for (var node : path) {
			if (unvisitedGrassFields.contains(node.getPosition())) {
				visited.add(node.getPosition());
				unvisitedGrassFields.remove(node.getPosition());
			}
		}
	}

	private void resetDijkstra(Position dijkstraSourceNode) {
		this.sourceNode = dijkstraSourceNode;
		this.dijkstra.setSourceNode(new PathNode(this.sourceNode, fullMap.getMap().get(this.sourceNode)));
		this.dijkstra.clearPossiblePath();
		this.dijkstra.clearAdjacent();
		this.dijkstra.clearShortestPath();

	}

	public Position getSourceNode() {
		return this.sourceNode;
	}

	@Override
	public void setVisitedFields(Set<Position> visitedFields) {
		this.visited = visitedFields;

	}

	@Override
	public Set<Position> getVisitedFields() {
		return this.visited;
	}

	public Queue<Position> getTreasureCanyon() {
		return this.treasureCanyon;
	}
}
