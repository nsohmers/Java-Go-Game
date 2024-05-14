enum StoneColor {
    BLACK,
    WHITE
}

public class Stone {
    private Point point;
    private StoneColor color;

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

    // TODO: Move this to Point class
    // and make Stone extend Point
    // (therfore can use this function)
    public Point[] getNeighbors(Point[][] grid) {
        int row = point.getRow();
        int col = point.getCol();

        int numberOfRows = grid.length;
        int numberOfCols = grid[0].length;

        if ( row > 0 && row < numberOfRows - 1 && col > 0 && col < numberOfCols - 1 ) {
            Point[] neighbors = {grid[row][col - 1], grid[row][col + 1], grid[row + 1][col], grid[row - 1][col]};
            return neighbors;
        } else if ( row == 0 && col > 0 && col < numberOfCols - 1 ) {
            Point[] neighbors = {grid[row][col - 1], grid[row][col + 1], grid[row + 1][col]};
            return neighbors;
        } else if ( row == numberOfRows - 1 && col > 0 && col < numberOfCols - 1 ) {
            Point[] neighbors = {grid[row][col - 1], grid[row][col + 1], grid[row - 1][col]};
            return neighbors;
        } else if ( row > 0 && row < numberOfRows - 1 && col == 0) {
            Point[] neighbors = {grid[row][col + 1], grid[row - 1][col], grid[row + 1][col]};
            return neighbors;
        } else if ( row > 0 && row < numberOfRows - 1 && col == numberOfCols - 1 ) {
            Point[] neighbors = {grid[row][col - 1], grid[row - 1][col], grid[row + 1][col]};
            return neighbors;
        } else if (row == 0 && col == 0) {
            Point[] neighbors = {grid[row][col + 1], grid[row + 1][col]};
            return neighbors;
        } else if (row == 0 && col == numberOfCols - 1) {
            Point[] neighbors = {grid[row][col - 1], grid[row + 1][col]};
            return neighbors;
        } else if (row == numberOfRows - 1 && col == 0) {
            Point[] neighbors = {grid[row][col + 1], grid[row - 1][col]};
            return neighbors;
        } else if (row == numberOfRows - 1 && col == numberOfCols - 1) {
            Point[] neighbors = {grid[row][col - 1], grid[row - 1][col]};
            return neighbors;
        } else {
            System.out.println("Error calculating neighbors");
            return null;
        }
    }
}