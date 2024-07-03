import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Map.Entry;
import java.awt.geom.Point2D;
import java.io.File;

public class Screen extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

  private enum GameState {
    INTRO, PLAY, INSTRUCTIONS, END
  }

  private static Board board;
  private static GameState state = GameState.INTRO;

  private static final int gameWidth = 1000;
  private static final int gameHeight = 1000;

  private static final int NUMROWS = 9;
  private static final int NUMCOLS = 9;

  private static final int leftMargin = 188;
  private static final int rightMargin = 188;
  private static final int topMargin = 188;
  private static final int bottomMargin = 188;

  private static final BasicStroke lineStroke = new BasicStroke(5.5f);

  private static final int rowGap = (gameWidth - leftMargin - rightMargin) / (NUMROWS - 1);
  private static final int colGap = (gameHeight - topMargin - bottomMargin) / (NUMCOLS - 1);

  private static final int extraPadding = 30;

  private static final Color boardColor = new Color(220, 179, 92);
  private static final Color outerBoardColor = new Color(137, 87, 55);

  private static TerritoryType winner = TerritoryType.UNKNOWN;
  private static int time = 0;
  private static boolean passed = false, resigned = false;

  private static GameText GText, OText, clickStartText, blackTurn, whiteTurn, blackPassed, whitePassed, blackWon,
      whiteWon, tie, whiteScore, blackScore;
  private static AnimatedButton passButton, resignButton, playAgainButton, instructionButton;
  private static ArrayList<GameText> texts;
  private static ArrayList<AnimatedButton> buttons;
  private static ArrayList<AnimatedStone> animatedStones;

  private static Audioplayer audioplayer;

  private boolean fading = false;
  private int transparency = 0;
  private Font instructionFont;

  public Screen() {
    board = new Board(NUMROWS, NUMCOLS);
    texts = new ArrayList<>();
    buttons = new ArrayList<>();
    animatedStones = new ArrayList<>();

    Color black = AnimatedStone.blackStone;
    Color white = AnimatedStone.whiteStone;
    Color tieColor = new Color(203, 192, 173);

    GText = new GameText("G", (gameHeight / 2), 400f, black, this, false, false);
    OText = new GameText("O", (gameWidth / 2), (gameHeight / 2), 400f, white, this);
    clickStartText = new GameText("click to start game", topMargin + 725, 100f, black, this, true, false);
    blackTurn = new GameText("black turn", 100, 90f, black, this, true, true);
    whiteTurn = new GameText("white turn", 100, 90f, white, this, true, true);
    blackPassed = new GameText("black passed", 100, 90f, white, this, true, true);
    whitePassed = new GameText("white passed", 100, 90f, black, this, true, true);
    blackWon = new GameText("black won", 100, 90f, black, this, true, true);
    whiteWon = new GameText("white won", 100, 90f, white, this, true, true);
    tie = new GameText("tie", 100, 90f, tieColor, this, true, true);
    blackScore = new GameText("black score: " + 0, leftMargin - 2 * extraPadding,
        gameHeight - bottomMargin + 2 * extraPadding + 50, 60f, AnimatedStone.blackStone, this);
    whiteScore = new GameText("white score: " + 0, leftMargin - 2 * extraPadding,
        gameHeight - bottomMargin + 2 * extraPadding + blackScore.getMaxTextHeight() + 45, 60f,
        AnimatedStone.whiteStone, this);

    texts.addAll(Arrays.asList(GText, OText, clickStartText, blackTurn, whiteTurn, blackPassed, whitePassed, blackWon,
        whiteWon, tie, blackScore, whiteScore));

    int center = gameWidth / 2;
    passButton = new AnimatedButton("pass", (center - 200 - 20), 895, 200, 80, 15, true, this);
    resignButton = new AnimatedButton("resign", (center + 20), 895, 200, 80, 15, true, this);
    playAgainButton = new AnimatedButton("play again", gameWidth - rightMargin - 300 + extraPadding * 2 + 30, 895, 280,
        80, 15, true, this);
    instructionButton = new AnimatedButton("instructions", gameWidth - 200 - 10, gameHeight - 60 - 10, 200, 60, 15,
        true, this);

    buttons.addAll(Arrays.asList(passButton, resignButton, playAgainButton, instructionButton));

    audioplayer = new Audioplayer();
    audioplayer.playGameMusic();

    try {
      instructionFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/gameFont.ttf")).deriveFont(20f);
    } catch (Exception e) {
      e.printStackTrace();
    }

    this.addMouseListener(this);
    this.addMouseMotionListener(this);

  }

  public Dimension getPreferredSize() {
    return new Dimension(gameWidth, gameHeight);
  }

  private void hideAll() {
    texts.forEach(GameText::hide);
    buttons.forEach(AnimatedButton::hide);
  }

  private void drawBoard(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    int width = gameWidth;
    int height = gameHeight;
    Color startColor = outerBoardColor;
    Color endColor = startColor.darker();
    Point2D center = new Point2D.Float(width / 2, height / 2);
    float radius = 2 * width / 3;
    RadialGradientPaint gradient = new RadialGradientPaint(center, radius, new float[] { 0.7f, 1f },
        new Color[] { startColor, endColor });

    g2d.setPaint(gradient);
    g2d.fillRect(0, 0, width, height);

    g.setColor(boardColor);
    g.fillRect(leftMargin - (colGap / 2) - (extraPadding / 2), topMargin - (rowGap / 2) - (extraPadding / 2),
        (gameWidth - rightMargin - leftMargin) + colGap + extraPadding,
        (gameHeight - topMargin - bottomMargin) + rowGap + extraPadding);

    g.setColor(Color.BLACK);
    g2d.setStroke(lineStroke);

    for (int i = 0; i < NUMROWS; i++) {
      int rowY = topMargin + (rowGap) * i;
      g2d.drawLine(leftMargin, rowY, gameWidth - rightMargin, rowY);
    }

    for (int i = 0; i < NUMCOLS; i++) {
      int colX = leftMargin + (colGap) * i;
      g2d.drawLine(colX, topMargin, colX, gameHeight - bottomMargin);
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
      animatedStones.get(i).draw(g);
    }
  }

  private void drawTerritories(Graphics g) {
    Color black = AnimatedStone.blackStone;
    Color white = AnimatedStone.whiteStone;

    for (Entry<Point, StoneColor> entry : board.getTerritoriesPointColor().entrySet()) {
      Point point = entry.getKey();
      g.setColor((entry.getValue() == StoneColor.BLACK) ? black : white);
      int width = colGap / 2 - 10;
      int height = rowGap / 2 - 10;
      int x = (leftMargin + colGap * point.getCol()) - width / 2;
      int y = (topMargin + rowGap * point.getRow()) - height / 2;
      g.fillRect(x, y, width, height);
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

  private void updateWinner() {
    if (resigned) {
      winner = (board.currentColor() == StoneColor.BLACK) ? TerritoryType.WHITE : TerritoryType.BLACK;
    } else {
      winner = board.calculateWinner();
    }
  }

  private void endGameAction() {
    state = GameState.END;
    updateWinner();
    hideAll();
    blackScore.setText("black score: " + board.getTeamScore(StoneColor.BLACK));
    whiteScore.setText("white score: " + board.getTeamScore(StoneColor.WHITE));
    blackScore.show();
    whiteScore.show();
  }

  private void fade() {
    int decreasing = 4;
    if (fading) {
      if ((transparency - decreasing) >= 0) {
        transparency -= decreasing;
      } else {
        fading = false;
      }
    } else if ((transparency + decreasing) <= 255) {
      transparency += decreasing;
    } else {
      transparency = 255; // Reset transparency to 255 when it reaches above 255
    }
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    int width, height;
    Color startColor, endColor;
    GradientPaint gradient;
    Graphics2D g2d;
    switch (state) {
      case INTRO:
        g2d = (Graphics2D) g;
        width = gameWidth;
        height = gameHeight;
        startColor = outerBoardColor.brighter();
        endColor = boardColor;
        gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        drawStones(g);
        break;
      case PLAY:
        drawBoard(g);
        drawStones(g);
        break;
      case INSTRUCTIONS:
        g2d = (Graphics2D) g;
        width = gameWidth;
        height = gameHeight;
        startColor = outerBoardColor.brighter();
        endColor = boardColor;
        gradient = new GradientPaint(0, 0, startColor, 0, height, endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        g.setColor(AnimatedStone.blackStone);
        g.setFont(instructionFont);

        String[] instructions = {
            "--Tromp-Taylor Rules for Go--",
            "",
            "Gameplay:",
            "Two players: one with black stones, one with white stones.",
            "",
            "Turns:",
            "Players alternate turns, starting with black.",
            "On a player's turn, they may place one of their stones on an empty intersection.",
            "",
            "Groups and Liberties:",
            "A group of stones is a connected set of stones of the same color.",
            "A stone or group of stones has liberties, which are the empty intersections",
            "directly adjacent to them.",
            "",
            "Capture:",
            "If a stone or group of stones has no liberties after",
            "an opponent's move, it is captured and removed from the board.",
            "",
            "Ko Rule:",
            "A player may not make a move that returns the game to the previous board position.",
            "",
            "Suicide:",
            "Placing a stone such that it or a group of stones of the same color",
            "would have no liberties is illegal unless it captures opposing stones in the process.",
            "",
            "End of Game:",
            "The game ends when both players pass consecutively.",
            "",
            "Scoring:",
            "",
            "Territory: Empty intersections surrounded by a player's stones are",
            "counted as their territory.",
            "",
            "Active Stones:",
            "Stones that remain on the board at the end of the game",
            "are counted as active stones.",
            "",
            "The player with the most combined territory and active stones wins.",
            "",
            "Click to Continue",
        };

        int y = 50;
        for (String line : instructions) {
          int x = 10;
          g2d.drawString(line, x, y);
          y += this.getFontMetrics(instructionFont).getHeight();
        }
        break;
      case END:
        drawBoard(g);
        drawStones(g);
        drawTerritories(g);
        break;
    }

    texts.forEach(text -> text.draw(g));
    buttons.forEach(button -> button.draw(g));

    new Thread(() -> fade()).start();
    g.setColor(new Color(0, 0, 0, transparency));
    if (fading)
      g.fillRect(0, 0, gameWidth, gameHeight);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    switch (state) {
      case INTRO:
        audioplayer.playWhoosh();
        audioplayer.stopGameMusic();
        transparency = 255;
        fading = true;
        state = GameState.PLAY;
        animatedStones.clear();
        hideAll();
        return;
      case PLAY:
        break;
      case INSTRUCTIONS:
        audioplayer.playWhoosh();
        transparency = 255;
        fading = true;
        state = GameState.PLAY;
        return;
      case END:
        if (playAgainButton.mouseTouching(x, y)) {
          passed = false;
          resigned = false;
          board = new Board(NUMROWS, NUMCOLS);
          state = GameState.INTRO;
          audioplayer.playWhoosh();
          audioplayer.playGameMusic();
          transparency = 255;
          fading = true;
          hideAll();
        }
        return;
    }

    if (instructionButton.mouseTouching(x, y)) {
      state = GameState.INSTRUCTIONS;
      audioplayer.playWhoosh();
      transparency = 255;
      fading = true;
      hideAll();
    }

    if (passButton.mouseTouching(x, y)) {
      if (!passed) {
        passed = true;
        board.pass();
      } else {
        endGameAction();
        return;
      }
    } else {
      passed = false;
    }

    if (!resigned && resignButton.mouseTouching(x, y)) {
      resigned = true;
      endGameAction();
      return;
    }

    int row = (((y - topMargin) + (rowGap / 2)) / rowGap);
    int col = (((x - leftMargin) + (colGap / 2)) / colGap);

    if (row < 0 || row > NUMROWS - 1 || col < 0 || col > NUMCOLS - 1)
      return;

    if (board.addStone(row, col)) {
      audioplayer.playPlop();
      animatedStones.add(new AnimatedStone(board.getLast(), leftMargin, topMargin, colGap, rowGap));
      if (board.updateStones()) {
        audioplayer.playRemove();
        ArrayList<Point> keys = new ArrayList<>(board.getPointColor().keySet());
        updateStones(keys);
      }
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    passButton.mouseTouching(x, y);
    resignButton.mouseTouching(x, y);
    playAgainButton.mouseTouching(x, y);
    instructionButton.mouseTouching(x, y);
  }

  public void animate() {
    while (true) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }

      switch (state) {
        case INTRO:
          GText.show();
          OText.show();
          clickStartText.show();
          if (time >= 80)
            fallStones();
          break;
        case PLAY:
          passButton.show();
          resignButton.show();
          instructionButton.show();
          if (!passed) {
            blackPassed.hide();
            whitePassed.hide();
            if (board.currentColor() == StoneColor.BLACK) {
              whiteTurn.hide();
              blackTurn.show();
            } else {
              blackTurn.hide();
              whiteTurn.show();
            }
          } else {
            if (board.currentColor() == StoneColor.BLACK) {
              whiteTurn.hide();
              blackTurn.hide();
              whitePassed.show();
            } else {
              blackTurn.hide();
              whiteTurn.hide();
              blackPassed.show();
            }
          }
          break;
        case INSTRUCTIONS:
          break;
        case END:
          playAgainButton.show();
          blackScore.show();
          whiteScore.show();

          switch (winner) {
            case BLACK:
              blackWon.show();
              break;
            case WHITE:
              whiteWon.show();
              break;
            case NEUTRAL:
              tie.show();
              break;
            case UNKNOWN:
              break;
          }
          break;
      }

      repaint();
      time++;
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }
}
