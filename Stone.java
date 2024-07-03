enum StoneColor {
  BLACK,
  WHITE
}

public class Stone extends Point {
  private StoneColor color;

  public Stone(int row, int col, StoneColor color) {
    super(row, col);
    this.color = color;
  }

  public Stone(Point point, StoneColor color) {
    super(point.getRow(), point.getCol());
    this.color = color;
  }

  public StoneColor getColor() {
    return color;
  }
}
