import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.io.File;

public class AnimatedButton {
  private String text;
  private int x, y, time, width, height, borderRadius;
  private float size;
  private boolean animating, hidden;
  private Color fill, normal, selected, current;
  private Screen screen;

  public AnimatedButton(String text, int x, int y, int width, int height, int borderRadius, boolean animating,
      Screen screen) {
    this.text = text;
    this.x = x;
    this.y = y;
    this.size = 0f;
    this.width = width;
    this.height = height;
    this.borderRadius = borderRadius;
    this.animating = animating;
    this.hidden = true;
    this.screen = screen;

    normal = new Color(234, 224, 204);
    fill = new Color(139, 93, 51);
    selected = new Color(252, 245, 199);
    current = normal;
  }

  public void draw(Graphics g) {
    if (hidden)
      return;

    if (animating) {
      float scaleFactor = 1f;

      size += scaleFactor;

      if (size >= width) {
        size = width;
        animating = false;
      }
    }

    g.setColor(fill);
    g.fillRoundRect(x, y, width, height, borderRadius, borderRadius);

    g.setColor(current);
    g.drawRoundRect(x, y, width, height, borderRadius, borderRadius);

    float fontSize = Math.min(width, height) - 30;
    Font font = getFont(fontSize);

    int stringWidth = screen.getFontMetrics(font).stringWidth(text);
    int stringHeight = screen.getFontMetrics(font).getHeight();

    int textX = x + (width - stringWidth) / 2;
    int textY = y + (height + stringHeight) / 2 - screen.getFontMetrics(font).getDescent();

    g.setFont(font);
    g.drawString(text, textX, textY);
  }

  public boolean mouseTouching(int x, int y) {
    if (x >= this.x && x <= width + this.x &&
        y >= this.y && y <= this.y + height) {
      current = selected;
      return true;
    }

    current = normal;
    return false;
  }

  public void hide() {
    hidden = true;
  }

  public void show() {
    if (!hidden)
      return;

    hidden = false;
    time = 0;
    size = 0f;
    animating = true;
  }

  public Font getFont(float size) {
    try {
      return Font.createFont(Font.TRUETYPE_FONT, new File("fonts/gameFont.ttf")).deriveFont(size);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
