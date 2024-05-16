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

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Screen extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    private Board board;

    private static final int gameWidth = 800;
    private static final int gameHeight = 800;

    private static int NUMROWS = 9;
    private static int NUMCOLS = 9;

    private static int leftMargin = 60;
    private static int rightMargin = 60;
    private static int topMargin = 60;
    private static int bottomMargin = 60;

    private static final int rowGap = (gameWidth - leftMargin - rightMargin) / (NUMROWS - 1);
    private static final int colGap = (gameHeight - topMargin - bottomMargin) / (NUMCOLS - 1);

    private static final Color boardColor = new Color(220, 179, 92);
    private static final Color blackStone = new Color(2, 4, 15);
    private static final Color whiteStone = new Color(238, 231, 231);

    public Screen() {
        board = new Board(NUMROWS, NUMCOLS);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

	public Dimension getPreferredSize() {
		return new Dimension(gameWidth, gameHeight);
	}

    private void drawBoard(Graphics g) {
        g.setColor(boardColor);

        g.fillRect(0, 0, gameWidth, gameHeight);

        g.setColor(Color.BLACK);

        Graphics2D g2 = (Graphics2D) g;

        g2.setStroke(new BasicStroke(5.2f));

        for (int i = 0; i < NUMROWS; i++) {
            int rowY = topMargin + (rowGap) * i;

            g2.drawLine(leftMargin, rowY, gameWidth - rightMargin, rowY);
        }

        for (int i = 0; i < NUMCOLS; i++) {
            int colX = leftMargin + (colGap) * i;

            g2.drawLine(colX, topMargin, colX, gameHeight - bottomMargin);
        }
    }

    private void drawStone(Stone stone, Graphics g) {
        StoneColor stoneColor = stone.getColor();
        Color color = (stoneColor == StoneColor.BLACK) ? blackStone : whiteStone;

        int stoneX = (leftMargin + colGap * stone.getCol()) - colGap / 2;
        int stoneY = (topMargin + rowGap * stone.getRow()) - rowGap / 2;

        g.setColor(color);

        g.fillOval(stoneX, stoneY, colGap, rowGap);
    }

    private void drawStones(Graphics g) {
        for (Stone stone : board.getStones()) {
            drawStone(stone, g);
        }
    }


	public void paintComponent(Graphics g) {
		super.paintComponent(g);

        drawBoard(g);
        drawStones(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int row = (((y - topMargin) + (rowGap / 2)) / rowGap);
        int col = (((x - leftMargin) + (rowGap / 2)) / colGap);

        if (row < 0)
            return;
        else if (row > NUMROWS - 1)
            return;

        if (col < 0)
            return;
        else if (col > NUMCOLS - 1)
            return;

        if (board.addStone(row, col)) {
            board.updateStones();
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
}