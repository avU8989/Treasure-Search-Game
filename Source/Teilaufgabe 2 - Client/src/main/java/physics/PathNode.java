package physics;

import java.util.Objects;

import asset.ClientMapNode;

public class PathNode implements Comparable<PathNode> {
	private Position position;
	private ClientMapNode node;
	private PathNode previous;
	private int weight;

	public PathNode() {

	}

	public PathNode(Position position) {
		this.position = position;
		this.weight = Integer.MAX_VALUE;
	}

	public PathNode(Position position, int weight) {
		this.position = position;
		this.weight = weight;
	}

	public PathNode(Position position, ClientMapNode node) {
		this.position = position;
		this.node = node;
		this.weight = Integer.MAX_VALUE;

	}

	@Override
	public int hashCode() {
		return Objects.hash(position);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PathNode other = (PathNode) obj;
		return Objects.equals(position, other.position);
	}

	public ClientMapNode getMapNode() {
		return this.node;
	}

	public Position getPosition() {
		return this.position;
	}

	public int getWeight() {
		return this.weight;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public PathNode getPrevious() {
		return this.previous;
	}

	@Override
	public int compareTo(PathNode pathNode) {
		return Integer.compare(this.weight, pathNode.weight);
	}

	@Override
	public String toString() {
		return "PathNode [position=" + position + "]" + "Weight [weight=" + weight;
	}

	public void setPrevious(PathNode current) {
		this.previous = current;

	}

}
