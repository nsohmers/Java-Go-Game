import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.BasicStroke;
import java.awt.RadialGradientPaint;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;

import java.awt.image.BufferedImage;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

import java.io.File;

import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class Screen extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    private enum GameState {
        INTRO,
        START,
        PLAY,
        END
    }

    private static Board board;

    private static GameState state = GameState.INTRO;

    private static final int gameWidth = 1000;
    private static final int gameHeight = 1000;

    private static int NUMROWS = 9;
    private static int NUMCOLS = 9;

    private static int leftMargin = 188;
    private static int rightMargin = 188;
    private static int topMargin = 188 + 20;
    private static int bottomMargin = 188 - 20;

    private static BasicStroke lineStroke = new BasicStroke(5.5f);

    private static final int rowGap = (gameWidth - leftMargin - rightMargin) / (NUMROWS - 1);
    private static final int colGap = (gameHeight - topMargin - bottomMargin) / (NUMCOLS - 1);

    private static final int extraPadding = 30;
    private static final Color boardColor = new Color(220, 179, 92);

    private static TerritoryType winner = TerritoryType.UNKNOWN;

    private static int time = 0;

    private static GameText GText, OText, clickStartText, blackTurn, whiteTurn;

    private static ArrayList<GameText> texts;
    private static ArrayList<AnimatedStone> animatedStones;

    private static final Color outerBoardColor = new Color(137, 87, 55);

    private static final Color blackStone = new Color(12, 14, 25);
    private static final Color whiteStone = new Color(238, 231, 231);

    public Screen() {
        board = new Board(NUMROWS, NUMCOLS);

        texts = new ArrayList<GameText>();
        animatedStones = new ArrayList<AnimatedStone>();

        Color black = AnimatedStone.blackStone;
        Color white = AnimatedStone.whiteStone;

        GText = new GameText("G", (gameHeight / 2), 400f, black, this, false);
        OText = new GameText("O", (gameWidth / 2), (gameHeight / 2), 400f, white, this);
        clickStartText = new GameText("click to start game", topMargin + 725, 100f, black, this, true);

        blackTurn = new GameText("black turn", 100, 90f, black, this, true);
        whiteTurn = new GameText("white turn", 100, 90f, white, this, true);

        texts.add(GText);
        texts.add(OText);
        texts.add(clickStartText);
        texts.add(blackTurn);
        texts.add(whiteTurn);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

	public Dimension getPreferredSize() {
		return new Dimension(gameWidth, gameHeight);
	}

    private void hideAll() {
        for (GameText text : texts) {
            text.hide();
        }
    }

    private void drawBoard(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        int width = gameWidth;
        int height = gameHeight;
        Color startColor = outerBoardColor;
        Color endColor = startColor.darker();
        Point2D center = new Point2D.Float(width / 2, height / 2);
        float radius = 2 * width / 3;
        RadialGradientPaint gradient = new RadialGradientPaint(center, radius, new float[]{0.7f, 1f}, new Color[]{startColor, endColor});

        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        g.setColor(boardColor);

        g.fillRect(leftMargin - (colGap / 2) - (extraPadding / 2), topMargin - (rowGap / 2) - (extraPadding / 2), (gameWidth - rightMargin - leftMargin) + colGap + extraPadding, (gameHeight - topMargin - bottomMargin) + rowGap + extraPadding);

        g.setColor(Color.BLACK);

        Graphics2D g2 = (Graphics2D) g;

        g2.setStroke(lineStroke);

        for (int i = 0; i < NUMROWS; i++) {
            int rowY = topMargin + (rowGap) * i;

            g2.drawLine(leftMargin, rowY, gameWidth - rightMargin, rowY);
        }

        for (int i = 0; i < NUMCOLS; i++) {
            int colX = leftMargin + (colGap) * i;

            g2.drawLine(colX, topMargin, colX, gameHeight - bottomMargin);
        }
    }

    private void updateStones(ArrayList<Point> points) {
        for (int i = 0; i < animatedStones.size(); i++) {
            if (!(points.contains(animatedStones.get(i).getPoint(board, leftMargin, topMargin)))) {
                animatedStones.remove(i);
                i--;
            }
        }
    }

    private void drawStones(Graphics g) {
        for (int i = 0; i < animatedStones.size(); i++) {
            try {
                animatedStones.get(i).draw(g);
            } catch (ConcurrentModificationException ex){
                System.out.println("Caught");
                i--;
                continue;
            }
        }
    }

    private void fallStones() {
        int width = 150;
        int x = (int) (Math.random() * (gameWidth - width));
        int y = 0 - width;

        if (time % 7 == 0) {
            animatedStones.add(new AnimatedStone(x, y, width, width));
        }

        for (int i = animatedStones.size() - 1; i >= 0; i--) {
            if (animatedStones.get(i).getY() > gameHeight + width) {
                animatedStones.remove(i);
            } else {
                animatedStones.get(i).fall();
            }
        }
    }

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

        switch (state) {
            case INTRO:
                Graphics2D g2d = (Graphics2D) g;
                int width = gameWidth;
                int height = gameHeight;

                Color startColor = outerBoardColor.brighter();
                Color endColor = boardColor;

                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);

                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
                drawStones(g);
                break;
            case START:
                g.setColor(boardColor);
                g.fillRect(0, 0, gameWidth, gameHeight);
                break;
            case PLAY:
                drawBoard(g);
                drawStones(g);
                break;
            case END:
                break;
        }

        for (GameText text : texts) {
            text.draw(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        switch (state) {
            case INTRO:
                state = GameState.START;
                return;

            case START:
                state = GameState.PLAY;
                return;

            case PLAY:
                break;

            case END:
                return;
        }

        int x = e.getX();
        int y = e.getY();

        int row = (((y - topMargin) + (rowGap / 2)) / rowGap);
        int col = (((x - leftMargin) + (colGap / 2)) / colGap);

        if (row < 0)
            return;
        else if (row > NUMROWS - 1)
            return;

        if (col < 0)
            return;
        else if (col > NUMCOLS - 1)
            return;

        if (board.addStone(row, col)) {
            animatedStones.add(new AnimatedStone(board.getLast(), leftMargin, topMargin, colGap, rowGap));
            if (board.updateStones()) {
                ArrayList<Point> keys = new ArrayList<Point>(board.getPointColor().keySet()); 
                updateStones(keys);
            }
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    public void animate() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch ( InterruptedException ex ) {
                Thread.currentThread().interrupt();
            }

            for (GameText text : texts) {
                if (!text.hidden())
                    text.update();
            }

            switch (state) {
            case INTRO:
                GText.show();
                OText.show();
                clickStartText.show();
                if (time >= 80)
                    fallStones();
                    repaint();
                break;
            case START:
                animatedStones.clear();
                hideAll();
                break;
            case PLAY:
                if (board.currentColor() == StoneColor.BLACK) {
                    whiteTurn.hide();
                    blackTurn.show();
                } else {
                    blackTurn.hide();
                    whiteTurn.show();
                }
                break;
            case END:
                break;
            }

            repaint();

            time++;
        }
    }
}