import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Board {
    // This is the grid representing the board in Go
    // each point represent a row and col on the grid
    private static Point[][] grid;

    // this is used to access stones at a given point
    private static HashMap<Point, Stone> stonePool;

    // The current turn is the current turn the game is on
    // when turn = 1, the game has just started
    // this will increment every move.
    private static int currentTurn = 1;

    // This will represent every group of stones
    // a lone stone is still considered a group
    // adjacent stones are in the same group
    private static ArrayList<Group> groups;

    // Hash codes used to ensure Super Ko rule
    // Meaning that the board can never repeat itself
    private static ArrayList<Integer> previousBoards;

    private static Stone last;

    public Board(int boardHeight, int boardWidth) {
        grid = new Point[boardHeight][boardWidth];

        stonePool = new HashMap<Point, Stone>();

        groups = new ArrayList<Group>();

        previousBoards = new ArrayList<Integer>();

        for (int r = 0; r <  grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                grid[r][c] = new Point(r, c);
                stonePool.put(grid[r][c], null);
            }
        }
    }

    public int currentTurn() {
        return currentTurn;
    }

    public void pass() {
        currentTurn++;
    }

    public Point[][] getGrid() {
        return grid;
    }

    public StoneColor currentColor() {
        return ((currentTurn % 2 == 1) ? StoneColor.BLACK : StoneColor.WHITE);
    }

    public boolean stoneAtLocation(int row, int col) {
        if (stonePool.get(grid[row][col]) != null) {
            return true;
        }

        return false;
    }

    public boolean stoneAtLocation(Point point) {
        if (stonePool.get(point) != null) {
            return true;
        }

        return false;
    }
    public Stone getLast() {
        return last;
    }

    // Returns all current Stones
    public ArrayList<Stone> getStones() {
        ArrayList<Stone> result = new ArrayList<Stone>(stonePool.values());
        result.removeIf(Objects::isNull);
        return result;
    }

    // returns a hashmap with points as keys
    // and stone colors as values
    public HashMap<Point, StoneColor> getPointColor() {
        HashMap<Point, StoneColor> result = new HashMap<Point, StoneColor>();

        for (Stone stone : getStones()) {
            result.put(stone.getPoint(grid), stone.getColor());
        }

        return result;
    }

    public HashMap<Point, StoneColor> getTerritoriesPointColor() {
        HashMap<Point, StoneColor> result = new HashMap<Point, StoneColor>();

        for (Territory territory : getTerritories()) {
            for (Point point : territory.getPoints()) {
                if (territory.getType() == TerritoryType.BLACK) {
                    result.put(point, StoneColor.BLACK);
                } else if (territory.getType() == TerritoryType.WHITE) {
                    result.put(point, StoneColor.WHITE);
                }
            }
        }

        return result;
    }

    // check to see if any groups of opposite
    // color lose all their liberties
    private boolean otherGroupLosesLiberties() {
        StoneColor other = (currentColor() != StoneColor.BLACK) ? StoneColor.BLACK : StoneColor.WHITE;

        for (Group group : groups) {
            if (group.getGroupColor() == other && group.getNumLiberties() == 0) {
                return true;
            }
        }

        return false;
    }

    // Checks that the board never repeats itself
    // if it does it will return true
    private boolean checkKoRule() {
        int current = getPointColor().hashCode();

        if (previousBoards.contains(current)) {
            return true;
        }

        previousBoards.add(current);

        return false;
    }

    // returns what type of territory is at a specified point
    public TerritoryType territoryAt(Point point) {
        if (stonePool.get(point) == null) {
            return TerritoryType.UNKNOWN;
        }

        if (stonePool.get(point).getColor() == StoneColor.BLACK) {
            return TerritoryType.BLACK;
        }

        return TerritoryType.WHITE;
    }

    // returns true if stone is placed, otherwise returns false
    public boolean addStone(int row, int col) {
        // check there are no stones already there
        if (stoneAtLocation(row, col))
            return false;

        // reference to point the stone will be on
        Point point = grid[row][col];

        // create Stone object
        Stone stone = new Stone(point, currentColor());

        // create a new group for the stone
        Group current = new Group(stone, this);

        // this is to store any new groups we remove
        ArrayList<Group> removedGroups = new ArrayList<Group>();

        // loop through all groups
        for (int i = 0; i < groups.size(); i++) {
            // skip the ones that don't match the color of the stone
            if (groups.get(i).getGroupColor() != currentColor())
                continue;

            // this is the list of points that surrounds the group
            ArrayList<Point> liberties = groups.get(i).getLiberties();

            // if the stone is one of the groups liberties
            // (touching the group) merge the groups
            // and delete the old group
            if (liberties.contains(point)) {
                removedGroups.add(groups.get(i));
                current.merge(groups.remove(i));
                i--;
            }

            // the idea is all groups touching the 
            // new stone will be merged into one group
        }

        // Add the stone to the grid
        // (this is needed to check if other groups
        // lose liberties and for the ko rule)
        stonePool.put(point, stone);

        // if the board repeats itself, or if the current liberties 
        // are zero and any of the other groups
        // don't lose all of their liberties, remove the current 
        // group and add back the ones we removed
        if (checkKoRule() || current.getNumLiberties() == 0 && !otherGroupLosesLiberties()) {
            stonePool.put(point, null);
            groups.addAll(removedGroups);
            return false;
        }

        // add the new group to groups
        groups.add(current);

        // increment the current turn by one
        currentTurn++;

        last = stone;

        return true;
    }

    public boolean updateStones() {
        boolean removed = false;

        // Loop through every group
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);

            // Check if the group has zero liberties
            if (group.getNumLiberties() == 0) {
                removed = true;
                // if it does then loop through its stones
                // and set their corresponding points to null
                for (Stone stone : group.getStones()) {
                    stonePool.put(stone.getPoint(grid), null);
                }

                // then remove the group
                groups.remove(i);
                i--;
            }
        }

        return removed;
    }

    // used to create an array list of territories
    // territories are groups of points with no stones
    // that are adjacent to each other
    // a territory can be "black" or "white"
    // depending on if it is solely surronded by one color
    public ArrayList<Territory> getTerritories() {
        // make the array list
        ArrayList<Territory> territories = new ArrayList<Territory>();

        // loop through every point in the 2D array
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid.length; c++) {
                Point point = grid[r][c];

                // if there is a stone there skip it
                if (stoneAtLocation(point))
                    continue;

                boolean already_found = false;

                // find out if the point is already part of a territory
                for (Territory territory : territories) {
                    if (territory.hasPoint(point)) {
                        already_found = true;
                        break;
                    }
                }

                // if it is skip it
                if (already_found)
                    continue;

                // create a new territory that starts at the point
                // and add it to territories
                territories.add(new Territory(point, this));
            }
        }

        // return the array list
        return territories;
    }

    // Used to calculate a team's score
    // May be deleted/opimized later
    public int getTeamScore(StoneColor color) {
        TerritoryType type = (color == StoneColor.BLACK)
                            ? TerritoryType.BLACK : TerritoryType.WHITE;
        int counter = 0;

        for (Territory territory : getTerritories()) {
            if (territory.getType() == type) {
                counter += territory.getNumPoints();
            }
        }

        for (StoneColor otherColor : getPointColor().values()) {
            if (color == otherColor) {
                counter++;
            }
        }

        return counter;
    }

    public TerritoryType calculateWinner() {
        int black = getTeamScore(StoneColor.BLACK);
        int white = getTeamScore(StoneColor.WHITE);

        if (black == white) {
            return TerritoryType.NEUTRAL;
        }

        if (black > white) {
            return TerritoryType.BLACK;
        }

        return TerritoryType.WHITE;
    }
}