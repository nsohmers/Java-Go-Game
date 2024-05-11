import java.util.ArrayList;
import java.util.HashMap;

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

    public Board(int boardHeight, int boardWidth) {
        grid = new Point[boardHeight][boardWidth];

        stonePool = new HashMap<Point, Stone>();
        groups = new ArrayList<Group>();

        for (int r = 0; r <  grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                grid[r][c] = new Point(r, c);
                stonePool.put(grid[r][c], null);
            }
        }
    }

    public Point[][] getGrid() {
        return grid;
    }

    public StoneColor currentColor() {
        return ((currentTurn % 2 == 1) ? StoneColor.BLACK : StoneColor.WHITE);
    }

    public int currentTurn() {
        return currentTurn;
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

    public HashMap<Point, StoneColor> getPointColorHash() {
        HashMap<Point, StoneColor> result = new HashMap<Point, StoneColor>();

        for (HashMap.Entry<Point, Stone> entry : stonePool.entrySet()) {
            Point point = entry.getKey();
            Stone stone = entry.getValue();

            if (stone != null) {
                result.put(point, stone.getColor());
            }
        }

        return result;
    }

    public void addStone(int row, int col) {
        // check there are no stones already there
        if (stoneAtLocation(row, col))
            return;

        // reference to point the stone will be on
        Point point = grid[row][col];

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

        // if the current liberties are zero remove the current 
        // group and add back the ones we removed
        // TODO: if leberties are zero but it removes another
        // group then allow for it to be put down
        if (current.getLiberties().size() == 0) {
            groups.addAll(removedGroups);
            return;
        }

        // add the stone to the grid
        stonePool.remove(point, null);
        stonePool.put(point, stone);

        // add the new group to groups
        groups.add(current);

        // increment the current turn by one
        currentTurn++;
    }
}