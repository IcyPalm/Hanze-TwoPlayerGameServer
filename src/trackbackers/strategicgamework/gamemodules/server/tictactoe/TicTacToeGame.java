package trackbackers.strategicgamework.gamemodules.server.tictactoe;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import nl.hanze.t23i.gamemodule.extern.AbstractGameModule;

public class TicTacToeGame extends AbstractGameModule {
    public static final String GAME_TYPE = "Tic-tac-toe";
    private String moveDetails;
    private String matchResult = "";
    private Map<String, Integer> playerResults;
    private int[][] grid;
    private TicTacToeView view;
    private String playerToMove;

    public TicTacToeGame(String playerOne, String playerTwo) {
        super(playerOne, playerTwo);

        this.moveDetails = null;
        this.playerResults = new HashMap();
        this.grid = new int[3][3];
        this.view = new TicTacToeView(playerOne, playerTwo);
        this.playerToMove = playerOne;
    }

    public Component getView() {
        return this.view;
    }

    public void start() throws IllegalStateException {
        super.start();
        this.view.setPlayerToMove(PlayerType.PLAYER_ONE);
    }

    public void doPlayerMove(String player, String move)
            throws IllegalStateException {
        super.doPlayerMove(player, move);

        if (!this.playerToMove.equals(player)) {
            throw new IllegalStateException("It is not " + player + "'s turn");
        }
        int pos;
        try {
            pos = Integer.parseInt(move);
        } catch (NumberFormatException e) {
            illegalPlayerMove(player);
            return;
        }

        if ((pos < 0) || (pos >= 9) || (this.grid[(pos / 3)][(pos % 3)] != 0)) {
            illegalPlayerMove(player);
            return;
        }

        this.grid[(pos / 3)][(pos % 3)] = getPlayerType(player).ordinal();
        this.view.update(pos, getPlayerType(player));

        this.moveDetails = "";
        if (hasWon(player)) {
            this.matchStatus = 1;
            this.playerResults.put(player, Integer.valueOf(1));
            this.playerResults.put(otherPlayer(player), Integer.valueOf(-1));
            return;
        }
        if (isGridFull()) {
            this.matchStatus = 1;
            this.playerResults.put(player, Integer.valueOf(0));
            this.playerResults.put(otherPlayer(player), Integer.valueOf(0));
        }
        nextPlayer();
    }

    public String getMatchResultComment() throws IllegalStateException {
        super.getMatchResultComment();

        return this.matchResult;
    }

    public String getMoveDetails() throws IllegalStateException {
        super.getMoveDetails();

        if (this.moveDetails == null) {
            throw new IllegalStateException("No moves have been done yet");
        }
        return this.moveDetails;
    }

    public String getPlayerToMove() throws IllegalStateException {
        super.getPlayerToMove();
        return this.playerToMove;
    }

    public int getPlayerResult(String player) throws IllegalStateException {
        super.getPlayerResult(player);
        return ((Integer) this.playerResults.get(player)).intValue();
    }

    public int getPlayerScore(String player) throws IllegalStateException {
        super.getPlayerResult(player);
        return ((Integer) this.playerResults.get(player)).intValue();
    }

    public String getTurnMessage() throws IllegalStateException {
        super.getTurnMessage();
        return "";
    }

    private void nextPlayer() {
        this.playerToMove = otherPlayer(this.playerToMove);
        this.view.setPlayerToMove(getPlayerType(this.playerToMove));
    }

    private String otherPlayer(String player) {
        return player.equals(this.playerOne) ? this.playerTwo : this.playerOne;
    }

    private PlayerType getPlayerType(String player) {
        return player.equals(this.playerOne) ? PlayerType.PLAYER_ONE : PlayerType.PLAYER_TWO;
    }

    private void illegalPlayerMove(String player) {
        this.matchStatus = 1;
        this.matchResult = (this.moveDetails = "Illegal move");
        this.playerResults.put(player, Integer.valueOf(-1));
        this.playerResults.put(otherPlayer(player), Integer.valueOf(1));
    }

    public boolean hasWon(String player) {
        int number = getPlayerType(player).ordinal();


        if ((this.grid[0][0] == number) && (this.grid[0][1] == number) && (this.grid[0][2] == number)) {
            return true;
        }


        if ((this.grid[1][0] == number) && (this.grid[1][1] == number) && (this.grid[1][2] == number)) {
            return true;
        }


        if ((this.grid[2][0] == number) && (this.grid[2][1] == number) && (this.grid[2][2] == number)) {
            return true;
        }


        if ((this.grid[0][0] == number) && (this.grid[1][0] == number) && (this.grid[2][0] == number)) {
            return true;
        }


        if ((this.grid[0][1] == number) && (this.grid[1][1] == number) && (this.grid[2][1] == number)) {
            return true;
        }


        if ((this.grid[0][2] == number) && (this.grid[1][2] == number) && (this.grid[2][2] == number)) {
            return true;
        }


        if ((this.grid[0][0] == number) && (this.grid[1][1] == number) && (this.grid[2][2] == number)) {
            return true;
        }


        if ((this.grid[2][0] == number) && (this.grid[1][1] == number) && (this.grid[0][2] == number)) {
            return true;
        }

        return false;
    }

    private boolean isGridFull() {
        for (int[] i : this.grid) {
            for (int j : i) {
                if (j == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
