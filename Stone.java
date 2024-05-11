enum StoneColor {
    BLACK,
    WHITE
}

public class Stone {
    private Point point;
    private StoneColor color;
    public static int numberOfRows, numberOfCols;

    public Stone(Point point, StoneColor color) {
        this.point = point;
        this.color = color;
    }

    public Point getPoint() {
        return point;
    }

    public StoneColor getColor() {
        return color;
    }

    public Point[] getNeighbors(Point[][] grid) {
        int row = point.getRow();
        int col = point.getCol();

        Point left = grid[row - 1][col];
        Point right = grid[row + 1][ col];
        Point down = grid[row][col - 1];
        Point up = grid[row][col + 1];

        if ( row > 0 && row < numberOfRows - 1 && col > 0 && col < numberOfCols - 1 ) {
            Point[] neighbors = {left, right, down, up};
            return neighbors;
        } else if ( row == 0 && col > 0 && col < numberOfCols - 1 ) {
            Point[] neighbors = {left, right, down};
            return neighbors;
        } else if ( row == numberOfRows - 1 && col > 0 && col < numberOfCols - 1 ) {
            Point[] neighbors = {left, right, up};
            return neighbors;
        } else if ( row > 0 && row < numberOfRows - 1 && col == 0) {
            Point[] neighbors = {right, up, down};
            return neighbors;
        } else if ( row > 0 && row < numberOfRows - 1 && col == numberOfCols - 1 ) {
            Point[] neighbors = {left, up, down};
            return neighbors;
        } else if (row == 0 && col == 0) {
            Point[] neighbors = {right, down};
            return neighbors;
        } else if (row == 0 && col == numberOfCols - 1) {
            Point[] neighbors = {left, down};
            return neighbors;
        } else if (row == numberOfRows - 1 && col == 0) {
            Point[] neighbors = {right, up};
            return neighbors;
        } else if (row == numberOfRows - 1 && col == numberOfCols - 1) {
            Point[] neighbors = {left, up};
            return neighbors;
        } else {
            System.out.println("Error calculating neighbors");
            return null;
        }
    }
}