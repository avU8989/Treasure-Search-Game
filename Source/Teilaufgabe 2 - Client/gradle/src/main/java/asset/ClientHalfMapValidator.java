package asset;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import physics.Position;
import terrain.FortState;
import terrain.TerrainType;
import terrain.TreasureState;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement()
public class ClientHalfMapValidator extends ClientHalfMap {
	private boolean isCorrect = false;
	private final int requiredMountains = 5;
	private final int requiredGrass = 24;
	private final int requiredWater = 7;
	private final int longBorderWater = 4;
	private final int shortBorderWater = 2;
	private int waterFields = 0;
	private int mountainFields = 0;
	private int grassFields = 0;

	public ClientHalfMapValidator(HashMap<Position, ClientMapNode> nodes, Position fort) {
		super(nodes, fort);
	}

	public boolean validate() {
		/*
		 * s goes through a loop and checks all function if everything is clear
		 * isCorrect is true
		 */

		while (!isCorrect) {

			List<Entry<Position, ClientMapNode>> positionsWater = super.clientMap.entrySet().stream()
					.filter(c -> c.getValue().getTerrain() == TerrainType.WATER).collect(Collectors.toList());

			for (var pos : positionsWater) {
				floodFill(pos.getKey(), TerrainType.GRASS);

			}

			if (numberOfTerrainFulfilled()) {
				isCorrect = true;
			} else {
				isCorrect = false;
				repairMapNumberOfTerrain();
			}

			if (!this.checkMapBorderWaterFields()) {
				isCorrect = true;
			} else {
				isCorrect = false;
			}

			if (checkFortPosition(super.getFort())) {
				isCorrect = true;
			} else {
				isCorrect = false;
				relocateFortPosition(super.getFort());
			}

		}
		return isCorrect;

	}

	public void floodFill(Position coordinate, TerrainType replace) {
		if (!super.clientMap.containsKey(coordinate) || super.clientMap.get(coordinate) == null) {
			return;
		}

		if (super.clientMap.get(coordinate).getTerrain() == replace) {
			return;
		}

		int[] directionX = { 1, 1, 1, 0, 0, -1, -1, -1 };
		int[] directionY = { 1, 0, -1, 1, -1, 0, 1, 1 };
		Queue<Position> adjacentNodes = new LinkedList<Position>();
		adjacentNodes.offer(coordinate);
		TerrainType oldTerrain = super.clientMap.get(coordinate).getTerrain();

		// using BFS search
		while (!adjacentNodes.isEmpty()) {
			Position head = adjacentNodes.poll();// retrieves the head of the queue
			int newX = 0;
			int newY = 0;
			for (int i = 0; i < 8; ++i) {
				newX = head.getCoordinateX() + directionX[i];
				newY = head.getCoordinateY() + directionY[i];
				Position newP = new Position(newX, newY);
				if (validPosition(newX, newY, newP, oldTerrain, replace)) {
					super.clientMap.get(newP).setTerrain(replace);
					adjacentNodes.offer(newP);
				}

			}
		}
	}

	private boolean validPosition(int newX, int newY, Position newP, TerrainType oldTerrain, TerrainType replace) {
		if (newX < 0 || newY < 0 || newY >= super.getHeight() || newX >= super.getWidth()
				|| super.clientMap.get(newP).getTerrain() != oldTerrain
				|| super.clientMap.get(newP).getTerrain() == replace) {
			return false;
		}

		return true;
	}

	public void repairMapNumberOfTerrain() {
		System.out.println("vamonos");
		List<Entry<Position, ClientMapNode>> grass;
		List<Entry<Position, ClientMapNode>> water;
		List<Entry<Position, ClientMapNode>> mountains;
		Random random = new Random();
		int grasspos = 0;
		int waterpos = 0;
		int mountpos = 0;

		boolean check = false;
		do {
			grass = super.clientMap.entrySet().stream()
					.filter(entry -> entry.getValue().getTerrain() == TerrainType.GRASS && entry.getValue() != null)
					.collect(Collectors.toList());

			water = super.clientMap.entrySet().stream()
					.filter(entry -> entry.getValue().getTerrain() == TerrainType.WATER && entry.getValue() != null)
					.collect(Collectors.toList());
			mountains = super.clientMap.entrySet().stream()
					.filter(entry -> entry.getValue().getTerrain() == TerrainType.MOUNTAIN && entry.getValue() != null)
					.collect(Collectors.toList());

			if (grass.size() > 0) {
				grasspos = random.nextInt(grass.size());
			}
			if (water.size() > 0) {
				waterpos = random.nextInt(water.size());
			}
			if (mountains.size() > 0) {
				mountpos = random.nextInt(mountains.size());
			}

			if (grassFields > requiredGrass) {
				if (waterFields < requiredWater) {
					super.clientMap.get(grass.get(grasspos).getKey()).setTerrain(TerrainType.WATER);
					this.waterFields++;
					this.grassFields--;
				} else if (mountainFields < requiredMountains) {
					super.clientMap.get(grass.get(grasspos).getKey()).setTerrain(TerrainType.MOUNTAIN);
					this.mountainFields++;
					this.grassFields--;
				}
			}

			if (mountainFields > requiredMountains) {
				if (waterFields < requiredWater) {
					super.clientMap.get(mountains.get(mountpos).getKey()).setTerrain(TerrainType.WATER);
					this.waterFields++;
					this.mountainFields--;
				} else if (grassFields < requiredGrass) {
					super.clientMap.get(mountains.get(mountpos).getKey()).setTerrain(TerrainType.GRASS);
					this.grassFields++;
					this.mountainFields--;
				}
			}

			if (waterFields > requiredWater) {
				if (mountainFields < requiredMountains) {
					super.clientMap.get(water.get(waterpos).getKey()).setTerrain(TerrainType.MOUNTAIN);
					this.mountainFields++;
					this.waterFields--;
				} else if (grassFields < requiredGrass) {
					super.clientMap.get(water.get(waterpos).getKey()).setTerrain(TerrainType.GRASS);
					this.grassFields++;
					this.waterFields--;

				}
			}

			List<Entry<Position, ClientMapNode>> positionsWater = super.clientMap.entrySet().stream()
					.filter(c -> c.getValue().getTerrain() == TerrainType.WATER).collect(Collectors.toList());

			for (var pos : positionsWater) {
				floodFill(pos.getKey(), TerrainType.GRASS);

			}

			check = numberOfTerrainFulfilled();
		} while (check == false);

	}

	public boolean numberOfTerrainFulfilled() {
		boolean check = true;

		this.mountainFields = (int) super.clientMap.values().stream()
				.filter(c -> c.getTerrain() == TerrainType.MOUNTAIN).count();
		this.waterFields = (int) super.clientMap.values().stream().filter(c -> c.getTerrain() == TerrainType.WATER)
				.count();
		this.grassFields = (int) super.clientMap.values().stream().filter(c -> c.getTerrain() == TerrainType.GRASS)
				.count();

		if (grassFields < requiredGrass) {
			check = false;
		}
		if (waterFields < requiredWater) {
			check = false;

		}
		if (mountainFields < requiredMountains) {
			check = false;
		}

		return check;
	}

	public boolean checkMapBorderWaterFields() {
		/*
		 * Position(0,0)-------------------------------------Position(9,0)
		 * Position(0,1)-------------------------------------Position(9,1)
		 * Position(0,2)-------------------------------------Position(9,2)
		 * Position(0,3)-------------------------------------Position(9,3)
		 * Position(0,4)-------------------------------------Position(9,4)
		 */
		int waterLeftBorder = 0;
		int waterRightBorder = 0;
		int waterTopBorder = 0;
		int waterBottomBorder = 0;
		Position leftBorder = new Position();
		Position rightBorder = new Position();
		Position topBorder = new Position();
		Position bottomBorder = new Position();
		boolean checked = true;

		for (int x = 0; x < super.getWidth(); ++x) {
			topBorder.setCoordinates(x, 0);
			bottomBorder.setCoordinates(x, 4);
			if (super.clientMap.containsKey(topBorder) && super.clientMap.get(topBorder) != null) {
				if (super.clientMap.get(topBorder).getTerrain() == TerrainType.WATER) {
					++waterTopBorder;
				}
			}

			if (super.clientMap.containsKey(bottomBorder) && super.clientMap.get(bottomBorder) != null) {
				if (super.clientMap.get(bottomBorder).getTerrain() == TerrainType.WATER) {
					++waterBottomBorder;
				}
			}
		}

		for (int y = 0; y < super.getHeight(); ++y) {
			leftBorder.setCoordinates(0, y);
			rightBorder.setCoordinates(super.getWidth() - 1, y);
			if (super.clientMap.containsKey(leftBorder) && super.clientMap.get(leftBorder) != null) {
				if (super.clientMap.get(leftBorder).getTerrain() == TerrainType.WATER) {
					++waterLeftBorder;
				}
			}

			if (super.clientMap.containsKey(rightBorder) && super.clientMap.get(rightBorder) != null) {
				if (super.clientMap.get(rightBorder).getTerrain() == TerrainType.WATER) {
					++waterRightBorder;
				}
			}
		}

		if (waterLeftBorder > shortBorderWater || waterRightBorder > shortBorderWater) {
			checked = false;
		}

		if (waterBottomBorder > longBorderWater || waterTopBorder > longBorderWater) {
			checked = false;
		}

		return checked;
	}

	private boolean checkFortPosition(Position position) {
		boolean check = true;
		if (position == null) {
			throw new IllegalArgumentException("position has to be defined");
		} else {
			if (super.clientMap.containsKey(position)
					&& super.clientMap.get(position).getTerrain() != TerrainType.GRASS) {
				if (super.clientMap.get(position).getFortState() == FortState.MyFortPresent
						&& super.clientMap.get(position).getTreasureState() == TreasureState.MyTreasurePresent) {
					check = false;
				}
				check = false;
			}
		}

		return check;
	}

	private void relocateFortPosition(Position position) {
		if (position == null) {
			throw new IllegalArgumentException("position hast to be defined");
		} else {
			List<Entry<Position, ClientMapNode>> grass = super.clientMap.entrySet().stream()
					.filter(entry -> entry.getValue().getTerrain() == TerrainType.GRASS && entry.getValue() != null)
					.collect(Collectors.toList());

			Random random = new Random();
			int pos = random.nextInt(grass.size());

			super.setFort(grass.get(pos).getKey());
			super.clientMap.get(grass.get(pos).getKey()).setFortState(FortState.MyFortPresent);
			super.clientMap.get(position).setFortState(FortState.NoOrUnknownFortState);
		}
	}

	public boolean isCorrect() {
		return this.isCorrect;
	}
}
