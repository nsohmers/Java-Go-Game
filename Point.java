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
}