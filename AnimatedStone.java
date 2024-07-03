import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;

public class AnimatedStone {
  private int x, y, width, height;
  private float size;
  private boolean animating;
  private Color color;
  private double velocity = 0.0, gravity = 0.3;

  public static final Color blackStone = new Color(12, 14, 25);
  public static final Color whiteStone = new Color(228, 221, 221);

  private static BezierCurve bezierCurve = new BezierCurve(15f, -10f, 30f, 0f);

  public AnimatedStone(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.animating = false;
    this.size = width;
    this.color = (((int) (Math.random() * 2)) == 0) ? blackStone : whiteStone;
  }

  public AnimatedStone(Stone stone, int leftMargin, int topMargin, int colGap, int rowGap) {
    this.x = (leftMargin + colGap * stone.getCol()) - colGap / 2;
    this.y = (topMargin + rowGap * stone.getRow()) - rowGap / 2;
    this.width = colGap;
    this.height = rowGap;
    this.size = 0;
    this.animating = true;
    this.color = (stone.getColor() == StoneColor.BLACK) ? blackStone : whiteStone;
  }

  public void draw(Graphics g) {
    Point2D center = new Point2D.Float(x + width / 2, y + height / 2);

    if (animating) {
      float t = size / width;

      size += bezierCurve.getCurve(t);

      if (size >= width) {
        size = width;
        animating = false;
      }
    }

    Graphics2D g2d = (Graphics2D) g;
    float radius = size / 2;
    float[] dist = { 0.1f, 0.4f, 1f };
    Color[] colors = { color.brighter().brighter(), color.brighter(), color };
    RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);

    g2d.setPaint(p);
    g2d.fill(new Ellipse2D.Float((float) center.getX() - radius, (float) center.getY() - radius, size, size));
  }

  public Point getPoint(Board board, int leftMargin, int topMargin) {
    int row = (((y - topMargin) + (height / 2)) / height);
    int col = (((x - leftMargin) + (width / 2)) / width);
    return board.getGrid()[row][col];
  }

  public void fall() {
    velocity += gravity;
    y += (int) velocity;
  }

  public boolean isAnimating() {
    return animating;
  }

  public int getY() {
    return y;
  }
}
