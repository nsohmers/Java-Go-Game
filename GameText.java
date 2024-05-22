import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.io.File;

public class GameText {
    private String text;

    private int x, y, time, index = 0;

    private float[] sizes;
    private float maxSize;

    private boolean hidden = true, centered = false, offcenter = false;

    private Color color;

    private static Screen screen;

    // points for bezzier curve
    private static final float p0 = 1f;
    private static final float p1 = 80f;
    private static final float p2 = 40f;
    private static final float p3 = 0f;

    public GameText(String text, int y, float maxSize, Color color, Screen screen, boolean centered) {
        this.text = text;
        this.y = y;
        this.maxSize = maxSize;
        this.color = color;
        this.screen = screen;

        this.centered = centered;
        offcenter = !centered;

        sizes = new float[text.length()];

        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = 0f;
        }
    }

    public GameText(String text, int x, int y, float maxSize, Color color, Screen screen) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.maxSize = maxSize;
        this.color = color;
        this.screen = screen;

        centered = false;

        sizes = new float[text.length()];

        for (int i = 0; i < sizes.length; i++) {
            sizes[i] = 0;
        }
    }

    public void draw(Graphics g) {
        if (hidden)
            return;

        if (centered) {
            this.x = (screen.getWidth() / 2) - (getTextWidth() / 2);
        } else if (offcenter) {
            this.x = (screen.getWidth() / 2) - getTextWidth();
        }

        int maxIndex = Math.min(index + 1, sizes.length);

        for (int i = 0; i < maxIndex; i++) {
            double t = sizes[i] / maxSize; 
            double scaleFactor = bezierCurve(t, p0, p1, p2, p3);

            sizes[i] = (float) (sizes[i] + scaleFactor);

            if (sizes[i] > maxSize) {
                sizes[i] = maxSize;
                index++;
            }
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

    private double bezierCurve(double t, float p0, float p1, float p2, float p3) {
        double u = 1 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;
        return (uuu * p0) + (3 * uu * t * p1) + (3 * u * tt * p2) + (ttt * p3);
    }

    public void update() {
        time++;
    }
     
    public void hide() {
        hidden = true;
    }

    public void show() {
        if (!hidden)
            return;

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

    public int getTextWidth() {
        int total = 0;

        for (int i = 0; i < sizes.length; i++) {
            total += screen.getFontMetrics(getFont(sizes[i])).stringWidth(text.substring(i, i + 1));
        }

        return total;
    }

    public Font getFont(float size) {
        Font font;

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/gameFont.ttf")).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return font;
    }

    public boolean hidden() {
        return hidden;
    }
}