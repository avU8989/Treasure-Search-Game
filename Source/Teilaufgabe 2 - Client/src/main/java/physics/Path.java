package physics;

import java.util.ArrayList;
import java.util.List;

public class Path {
	private ArrayList<PathNode> unvisited;
	private ArrayList<PathNode> visited;

	public Path() {
		this.unvisited = new ArrayList<>();
		this.visited = new ArrayList<>();
	}

	public Path(List<PathNode> visited) {
		this.unvisited = new ArrayList<>();
		this.visited = new ArrayList<>(visited);
	}

	public void addVisited(PathNode position) {
		this.visited.add(position);

	}

	public void addUnvisited(PathNode position) {
		this.unvisited.add(position);
	}

	public void updatePathNode(int i, PathNode position, int newDistance) {
		PathNode adjNode = unvisited.remove(i);
		adjNode.setWeight(newDistance); // set the new weight
		unvisited.add(i, position);
	}

	public ArrayList<PathNode> getVisited() {
		return this.visited;
	}

	public ArrayList<PathNode> getUnvisited() {
		return this.unvisited;
	}

	public int getUnvisitedSize() {
		return this.unvisited.size();
	}

	public int getVisitedSize() {
		return this.visited.size();
	}

	public void clear() {
		this.unvisited.clear();
	}
}
