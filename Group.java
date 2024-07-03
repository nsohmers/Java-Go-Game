import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Group {
  private Board board;
  private ArrayList<Stone> stones;
  private StoneColor groupColor;

  // every group starts off with one piece but
  // will merge when touching other groups
  public Group(Stone initial, Board board) {
    this.board = board;
    this.stones = new ArrayList<Stone>();
    this.groupColor = initial.getColor();

    stones.add(initial);
  }

  public ArrayList<Stone> getStones() {
    return stones;
  }

  public int getNumLiberties() {
    return getLiberties().size();
  }

  public StoneColor getGroupColor() {
    return groupColor;
  }

  // function used to merge with another group
  public void merge(Group other) {
    if (other.getGroupColor() == groupColor) {
      stones.removeAll(other.getStones());
      stones.addAll(other.getStones());
    } else
      System.out.println("Error, two groups were merged with different colors");
  }

  // returns all liberties of a group;
  // linked hash set is used to remove all
  // repeating references to the same point
  public ArrayList<Point> getLiberties() {
    LinkedHashSet<Point> libertiesSet = new LinkedHashSet<Point>();

    for (Stone stone : stones) {
      Point[] neighbors = stone.getNeighbors(board.getGrid());
      for (Point neighbor : neighbors) {
        if (!board.stoneAtLocation(neighbor))
          libertiesSet.add(neighbor);
      }
    }

    return new ArrayList<Point>(libertiesSet);
  }
}
