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

    // Used to ensure Ko rule
    private static HashMap<Point, StoneColor> prev_white_board;
    private static HashMap<Point, StoneColor> prev_black_board;

    public Board(int boardHeight, int boardWidth) {
        grid = new Point[boardHeight][boardWidth];

        stonePool = new HashMap<Point, Stone>();

        groups = new ArrayList<Group>();

        prev_white_board = new HashMap<Point, StoneColor>();
        prev_black_board = new HashMap<Point, StoneColor>();

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

    // Returns all current Stones
    public ArrayList<Stone> getStones() {
        ArrayList<Stone> result = new ArrayList<Stone>(stonePool.values());
        result.removeIf(Objects::isNull);
        return result;
    }

    // used to ensure Ko rule
    // returns a hashmap with points as keys
    // and stone colors as values
    private HashMap<Point, StoneColor> getPointColor() {
        HashMap<Point, StoneColor> result = new HashMap<Point, StoneColor>();

        for (Stone stone : getStones()) {
            result.put(stone.getPoint(grid), stone.getColor());
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

    // Checks that the board did not repeat itself
    // if it does it will return true
    private boolean checkKoRule() {
        HashMap<Point, StoneColor> current = getPointColor();
        HashMap<Point, StoneColor> prev = (currentColor() == StoneColor.BLACK)
                                          ? prev_black_board : prev_white_board;


        if (prev.equals(current)) {
            return true;
        }

        if (currentColor() == StoneColor.BLACK) {
            prev_black_board = current;
        } else {
            prev_white_board = current;
        }

        return false;
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
            boolean touching = false;

            // if the point is one of these surrounding the
            // group set "touching" to true
            for (Point liberty : liberties) {
                if (liberty.equals(point)) {
                    touching = true;
                }
            }

            // if the stone is touching the group merge the groups
            // and delete the old group
            if (touching) {
                removedGroups.add(groups.get(i));
                current.merge(groups.remove(i));
                i--;
            }

            // the idea is all groups touching the 
            // new stone will be merged into one group
        }

        // Add the stone to the grid
        // (this is needed to check if other groups lose liberties)
        stonePool.put(point, stone);

        // if the current liberties are zero and any of the other groups
        // don't lose all of their liberties remove the current 
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

        return true;
    }

    public void updateStones() {
        // Loop through every group
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);

            // Check if the group has zero liberties
            if (group.getNumLiberties() == 0) {
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
    }
}