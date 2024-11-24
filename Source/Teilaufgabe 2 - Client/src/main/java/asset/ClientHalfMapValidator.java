package asset;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import exceptions.ClientMapValidatorException;
import physics.Position;
import terrain.FortState;
import terrain.TerrainType;
import terrain.TreasureState;

public class ClientHalfMapValidator extends ClientHalfMap {
	private boolean isCorrect = false;
	private final int requiredMountains = 9;
	private final int requiredGrass = 24;
	private final int requiredWater = 7;
	private final int longBorderWater = 4;
	private final int shortBorderWater = 2;
	private int waterFields = 0;
	private int mountainFields = 0;
	private int grassFields = 0;
	private static Logger logger = LoggerFactory.getLogger(ClientHalfMapValidator.class);

	public ClientHalfMapValidator(Map<Position, ClientMapNode> nodes, Position fort) {
		super(nodes, fort);
		try {
			this.validate();
		} catch (ClientMapValidatorException e) {
			e.printStackTrace();
		}
	}

	private boolean validate() throws ClientMapValidatorException {
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

			if (!numberOfTerrainFulfilled()) {
				isCorrect = false;
				repairMapNumberOfTerrain();
			}

			if (!checkFortPosition(super.getFort())) {
				isCorrect = false;
				relocateFortPosition(super.getFort());
			}

			isCorrect = checkMapBorderWaterFields();

		}
		return isCorrect;

	}

	private void floodFill(Position coordinate, TerrainType replace) throws ClientMapValidatorException {
		if (coordinate == null) {
			throw new ClientMapValidatorException("Coordinate cannot be null");
		}
		if (coordinate.getCoordinateX() >= super.getWidth() || coordinate.getCoordinateY() >= super.getHeight()) {
			throw new ClientMapValidatorException("Coordinate is invalid");
		}
		if (replace == null) {
			throw new ClientMapValidatorException("Replaced Terrain cannot be null");
		}

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
			for (int i = 0; i < directionX.length; ++i) {
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
				|| super.clientMap.get(newP).getTerrain() == replace
				|| super.clientMap.get(newP).getTerrain() != oldTerrain) {
			return false;
		}

		return true;
	}

	private void repairMapNumberOfTerrain() throws ClientMapValidatorException {
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

			if (!grass.isEmpty()) {
				grasspos = random.nextInt(grass.size());
			}
			if (!water.isEmpty()) {
				waterpos = random.nextInt(water.size());
			}
			if (!mountains.isEmpty()) {
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
				try {
					floodFill(pos.getKey(), TerrainType.GRASS);
				} catch (ClientMapValidatorException e) {
					e.printStackTrace();
				}

			}
			check = numberOfTerrainFulfilled();
		} while (check == false);

	}

	private boolean numberOfTerrainFulfilled() {
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

	private boolean checkMapBorderWaterFields() throws ClientMapValidatorException {
		/*
		 * Position(0,0)-------------------------------------Position(9,0)
		 * Position(0,1)-------------------------------------Position(9,1)
		 * Position(0,2)-------------------------------------Position(9,2)
		 * Position(0,3)-------------------------------------Position(9,3)
		 * Position(0,4)-------------------------------------Position(9,4)
		 */
		boolean checked = true;
		List<Position> shortWaterBorderFieldsLeft = new ArrayList<>();
		List<Position> shortWaterBorderFieldsRight = new ArrayList<>();
		List<Position> longWaterBorderFieldsTop = new ArrayList<>();
		List<Position> longWaterBorderFieldsBottom = new ArrayList<>();

		for (int x = 0; x < super.getWidth(); ++x) {
			Position borderTop = new Position(x, 0);
			if (super.clientMap.containsKey(borderTop) && super.clientMap.get(borderTop) != null) {
				if (super.clientMap.get(borderTop).getTerrain() == TerrainType.WATER) {
					longWaterBorderFieldsTop.add(borderTop);
				}
			}

			Position borderBot = new Position(x, super.getHeight() - 1);
			if (super.clientMap.containsKey(borderBot) && super.clientMap.get(borderBot) != null) {
				if (super.clientMap.get(borderBot).getTerrain() == TerrainType.WATER) {
					longWaterBorderFieldsBottom.add(borderBot);
				}
			}
		}

		for (int y = 0; y < super.getHeight(); ++y) {
			Position borderLeft = new Position(0, y);
			if (super.clientMap.containsKey(borderLeft) && super.clientMap.get(borderLeft) != null) {
				if (super.clientMap.get(borderLeft).getTerrain() == TerrainType.WATER) {
					shortWaterBorderFieldsLeft.add(borderLeft);
				}
			}

			Position borderRight = new Position(super.getWidth() - 1, y);
			if (super.clientMap.containsKey(borderRight) && super.clientMap.get(borderRight) != null) {
				if (super.clientMap.get(borderRight).getTerrain() == TerrainType.WATER) {
					shortWaterBorderFieldsRight.add(borderRight);
				}
			}
		}

		if (shortWaterBorderFieldsLeft.size() > shortBorderWater) {
			checked = repairShortBorderWater(shortWaterBorderFieldsLeft);
		}

		if (shortWaterBorderFieldsRight.size() > shortBorderWater) {
			checked = repairShortBorderWater(shortWaterBorderFieldsRight);
		}

		if (longWaterBorderFieldsBottom.size() > longBorderWater) {
			checked = repairLongBorderWater(longWaterBorderFieldsBottom);

		}

		if (longWaterBorderFieldsTop.size() > longBorderWater) {
			checked = repairLongBorderWater(longWaterBorderFieldsTop);

		}

		return checked;
	}

	private boolean repairShortBorderWater(List<Position> positions) {
		Random random = new Random();
		int pos = random.nextInt(positions.size());
		super.clientMap.get(positions.get(pos)).setTerrain(TerrainType.MOUNTAIN);

		boolean check = numberOfTerrainFulfilled();
		return check;
	}

	private boolean repairLongBorderWater(List<Position> positions) {
		Random random = new Random();
		int pos = random.nextInt(positions.size());
		super.clientMap.get(positions.get(pos)).setTerrain(TerrainType.GRASS);

		boolean check = numberOfTerrainFulfilled();
		return check;
	}

	private boolean checkFortPosition(Position position) throws ClientMapValidatorException {
		boolean check = true;
		if (position == null) {
			throw new ClientMapValidatorException("The position has to be defined");
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

	private void relocateFortPosition(Position position) throws ClientMapValidatorException {
		if (position == null) {
			throw new IllegalArgumentException("position hast to be defined");
		} else {
			List<Entry<Position, ClientMapNode>> grass = super.clientMap.entrySet().stream().filter((entry) -> {
				return ((ClientMapNode) entry.getValue()).getTerrain() == TerrainType.GRASS && entry.getValue() != null;
			}).collect(Collectors.toList());
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
