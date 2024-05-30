import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
public class GameText {
    private String text;
    private int x, y, time, index = 0;
    private float[] sizes;
    private float maxSize;
    private boolean hidden = true, centered = false, offcenter = false, allTextanimated = false;
    private Color color;
    private static Screen screen;

    // Bezier curve points
    private static final float[] bezierPoints = {1f, 80f, 40f, 0f};
    private static final float[] altBezierPoints = {30f, -5f, 10f, 0f};

    public GameText(String text, int y, float maxSize, Color color, Screen screen, boolean centered, boolean allTextanimated) {
        this(text, 0, y, maxSize, color, screen);
        this.centered = centered;
        this.allTextanimated = allTextanimated;
        this.offcenter = !centered;
    }

    public GameText(String text, int x, int y, float maxSize, Color color, Screen screen) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.maxSize = maxSize;
        this.color = color;
        this.screen = screen;
        this.sizes = new float[text.length()];
    }

    public void draw(Graphics g) {
        if (hidden) return;

        int maxIndex = Math.min(index + 1, sizes.length);
        for (int i = 0; i < (allTextanimated ? sizes.length : maxIndex); i++) {
            float t = sizes[i] / maxSize;
            float scaleFactor = bezierCurve(t, allTextanimated ? altBezierPoints : bezierPoints);
            sizes[i] += scaleFactor;
            if (sizes[i] > maxSize) {
                sizes[i] = maxSize;
                index++;
            }
        }

        if (centered) {
            this.x = (screen.getWidth() / 2) - (getTextWidth() / 2);
        } else if (offcenter) {
            this.x = (screen.getWidth() / 2) - getTextWidth();
        }

        g.setColor(color);

        int currentX = x;
        for (int i = 0; i < sizes.length; i++) {
            String current = text.substring(i, i + 1);
            Font font = getFont(sizes[i]);
            g.setFont(font);
            g.drawString(current, currentX, y);
            currentX += screen.getFontMetrics(font).stringWidth(current);
        }
    }

    private float bezierCurve(float t, float[] points) {
        float u = 1 - t;
        float tt = t * t;
        float uu = u * u;
        float uuu = uu * u;
        float ttt = tt * t;
        return (uuu * points[0]) + (3 * uu * t * points[1]) + (3 * u * tt * points[2]) + (ttt * points[3]);
    }

    public void update() {
        time++;
    }

    public void hide() {
        hidden = true;
    }

    public void show() {
        if (!hidden) return;

        hidden = false;
        time = 0;
        index = 0;
        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = 0f;
        }
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setText(String text) {
        this.text = text;
        this.sizes = new float[text.length()];
    }

    public int getTextWidth() {
        int total = 0;
        for (int i = 0; i < sizes.length; i++) {
            total += screen.getFontMetrics(getFont(sizes[i])).stringWidth(text.substring(i, i + 1));
        }
        return total;
    }

    public int getMaxTextHeight() {
        return screen.getFontMetrics(getFont(maxSize)).getHeight();
    }

    public Font getFont(float size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File("fonts/gameFont.ttf")).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString() {
        return text + "\tX:" + this.x + "\tY:" + this.y + "\t" + hidden;
    }

    public boolean hidden() {
        return hidden;
    }
}