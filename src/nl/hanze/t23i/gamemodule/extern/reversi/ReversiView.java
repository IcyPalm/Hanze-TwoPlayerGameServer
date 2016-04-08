package nl.hanze.t23i.gamemodule.extern.reversi;

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

public class ReversiView
        extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Dimension PREFERRED_SIZE = new Dimension(360, 360);

    private Cell[] cells = new Cell[64];
    private JLabel playerOneLabel;
    private JLabel playerTwoLabel;

    public ReversiView(String playerOne, String playerTwo) {
        super(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.fill = 1;

        JPanel grid = new JPanel(new GridLayout(8, 8, 1, 1));
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
        this.playerOneLabel = new JLabel("Black: " + playerOne);
        this.playerOneLabel.setFont(font);
        players.add(this.playerOneLabel);
        this.playerTwoLabel = new JLabel("White: " + playerTwo);
        this.playerTwoLabel.setFont(font);
        players.add(this.playerTwoLabel);
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.0D;
        c.fill = 0;
        add(players, c);

        repaint();
        setVisible(true);
    }

    public void update(int[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                this.cells[(row * 8 + col)].setPlayerType(PlayerType.values()[board[row][col]]);
            }
        }

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

            setBackground(new Color(0, 225, 0));

            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(8.0F, 1, 1));


            int maxX = getWidth() - 2;
            int maxY = getHeight() - 2;

            switch (this.playerType) {
                case PLAYER_ONE: {
                    g2d.setColor(Color.BLACK);
                    g2d.fillOval(1, 1, maxX, maxY);
                    break;
                }
                case PLAYER_TWO: {
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(1, 1, maxX, maxY);
                    break;
                }
            }
        }
    }
}