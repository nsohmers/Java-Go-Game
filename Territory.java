import java.util.ArrayList;

// UNKOWN means that the territory has not been assigned a color yet
// NEUTRAL means the territory has both black and white pieces touching it
// BLACK means the territory is soley surrounded by black stones
// WHITE means the territory is soley surrounded by white stones
enum TerritoryType {
  UNKNOWN,
  NEUTRAL,
  BLACK,
  WHITE
}

public class Territory {
  // Represents all points within territory
  private ArrayList<Point> occupiedPoints;
  // represents territory type, explained in enum above
  private TerritoryType type = TerritoryType.UNKNOWN;

  // Initialize Territory object,
  // find all points in the territory
  public Territory(Point initial, Board board) {
    occupiedPoints = new ArrayList<Point>();
    getTerritory(initial, board);
  }

  // Uses DFS algo to find all empty spaces within stones
  private void getTerritory(Point initial, Board board) {
    // If the point is already explored, exit the method
    if (hasPoint(initial)) {
      return;
    }

    // get the territory at the point
    TerritoryType territory = board.territoryAt(initial);

    // if the point is empty, add it to our list of points
    // and search its neighbors
    if (territory == TerritoryType.UNKNOWN) {
      occupiedPoints.add(initial);

      for (Point point : initial.getNeighbors(board.getGrid())) {
        getTerritory(point, board);
      }
    } else {
      // if not, check if our territory is UNKNOWN
      // if it is, make that our territory type
      if (type == TerritoryType.UNKNOWN)
        type = territory;

      // if we already have a color,
      // set our territory to neutral
      // meaning it touches both
      // white and black stones
      else if (type != territory)
        type = TerritoryType.NEUTRAL;
    }
  }

  public TerritoryType getType() {
    return type;
  }

  public boolean hasPoint(Point point) {
    return occupiedPoints.contains(point);
  }

  public int getNumPoints() {
    return occupiedPoints.size();
  }

  public ArrayList<Point> getPoints() {
    return occupiedPoints;
  }
}
