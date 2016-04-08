package trackbackers.strategicgamework.gamemodules.server.tictactoe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TicTacToeView extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Dimension PREFERRED_SIZE = new Dimension(360, 360);

    private Cell[] cells = new Cell[9];
    private JLabel playerOneLabel;
    private JLabel playerTwoLabel;

    public TicTacToeView(String playerOne, String playerTwo) {
        super(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.fill = 1;

        JPanel grid = new JPanel(new GridLayout(3, 3, 5, 5));
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new Cell();
            grid.add(this.cells[i]);
        }
        c.gridx = 0;
        c.gridy = 0;
        add(grid, c);

        JPanel players = new JPanel();
        players.setLayout(new BoxLayout(players, 1));
        Font font = new Font("SansSerif", 1, 18);
        this.playerOneLabel = new JLabel(PlayerType.PLAYER_ONE.symbol + ": " + playerOne);

        this.playerOneLabel.setFont(font);
        this.playerOneLabel.setForeground(Color.RED);
        players.add(this.playerOneLabel);
        this.playerTwoLabel = new JLabel(PlayerType.PLAYER_TWO.symbol + ": " + playerTwo);

        this.playerTwoLabel.setFont(font);
        this.playerTwoLabel.setForeground(Color.BLUE);
        players.add(this.playerTwoLabel);
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.0D;
        c.fill = 0;
        add(players, c);

        repaint();
        setVisible(true);
    }

    public void update(int pos, PlayerType playerType) {
        this.cells[pos].setPlayerType(playerType);
        repaint();
    }

    public void setPlayerToMove(PlayerType playerToMove) {
        if (playerToMove == PlayerType.PLAYER_ONE) {
            this.playerOneLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, this.playerOneLabel.getForeground()));

            this.playerTwoLabel.setBorder(null);
        } else {
            this.playerTwoLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, this.playerTwoLabel.getForeground()));

            this.playerOneLabel.setBorder(null);
        }
    }

    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }

    private class Cell
            extends JPanel {
        private static final long serialVersionUID = 1L;
        private PlayerType playerType = PlayerType.NONE;


        public Cell() {
        }

        public void setPlayerType(PlayerType playerType) {
            this.playerType = playerType;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            setBackground(Color.WHITE);

            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(8.0F, 1, 1));


            switch (this.playerType) {
                case PLAYER_ONE:
                    g2d.setColor(Color.RED);
                    int maxX = getWidth() - 20;
                    int maxY = getHeight() - 20;
                    g2d.drawLine(16, 16, maxX, maxY);
                    g2d.drawLine(16, maxY, maxX, 16);
                    break;
                case PLAYER_TWO:
                    int width = getWidth() - 40;
                    int height = getHeight() - 40;
                    g2d.setColor(Color.BLUE);
                    g2d.drawOval(18, 18, width, height);
                    break;
            }
        }
    }
}