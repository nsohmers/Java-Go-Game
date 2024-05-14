public class Point {
    private int row, col;

    public Point(int[] point) {
        this.row = point[0];
        this.col = point[1];
    }

    public Point(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean equals(Point other) {
        if (row == other.getRow() && col == other.getCol()) {
            return true;
        }
        return false;
    }

    public Point getPoint(Point[][] grid) {
        return grid[row][col];
    }

    public Point getLeft(Point[][] grid) {
        if (col == 0)
            return null;

        return grid[row][col - 1];
    }

    public Point getRight(Point[][] grid) {
        if (col == grid[0].length - 1)
            return null;

        return grid[row][col - 1];
    }

    public Point getUp(Point[][] grid) {
        if (row == grid.length - 1)
            return null;

        return grid[row - 1][col];
    }

    public Point getDown(Point[][] grid) {
        if (row == 0)
            return null;

        return grid[row + 1][col];
    }

    public Point[] getNeighbors(Point[][] grid) {
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

    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}