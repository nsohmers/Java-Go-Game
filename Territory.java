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
    private ArrayList<Point> occupiedPoints;
    private TerritoryType type = TerritoryType.UNKNOWN;


    public Territory(Point initial, Board board) {
        occupiedPoints = new ArrayList<Point>();
        getTerritory(initial, board);
    }

    private void getTerritory(Point initial, Board board) {
        // If the point is already explored, exit the method
        if (hasPoint(initial)) {
            return;
        }

        TerritoryType territory = board.territoryAt(initial);

        if (territory == TerritoryType.UNKNOWN) {
            occupiedPoints.add(initial);

            for (Point point : initial.getNeighbors(board.getGrid())) {
                getTerritory(point, board);
            }
        } else {
            if (type == TerritoryType.UNKNOWN)
                type = territory;

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
}
