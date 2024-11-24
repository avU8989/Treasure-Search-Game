package physics;

import java.util.List;
import java.util.Set;

public interface MovementStrategy {

	public List<Direction> sendDirection();

	public void setVisitedFields(Set<Position> visitedFields);

	public Set<Position> getVisitedFields();

}
