package trackbackers.strategicgamework.gamemodules.server.reversi;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import nl.hanze.t23i.gamemodule.extern.AbstractGameModule;

public class ReversiGame extends AbstractGameModule {
    public static final String GAME_TYPE = "Reversi";
    private String moveDetails;
    private String matchResult = "";
    private Map<String, Integer> playerResults;
    private Map<String, Integer> playerScores;
    private int[][] grid;
    private ReversiView view;
    private String playerToMove;

    public ReversiGame(String playerOne, String playerTwo) {
        super(playerOne, playerTwo);

        this.moveDetails = null;
        this.playerResults = new HashMap();
        this.playerScores = new HashMap();
        this.grid = new int[8][8];
        this.view = new ReversiView(playerOne, playerTwo);
        this.playerToMove = playerOne;
    }

    public void start() throws IllegalStateException {
        super.start();
        this.grid[3][3] = PlayerType.PLAYER_TWO.ordinal();
        this.grid[3][4] = PlayerType.PLAYER_ONE.ordinal();
        this.grid[4][3] = PlayerType.PLAYER_ONE.ordinal();
        this.grid[4][4] = PlayerType.PLAYER_TWO.ordinal();
        this.view.update(this.grid);

        this.view.setPlayerToMove(PlayerType.PLAYER_ONE);
    }

    public Component getView() {
        return this.view;
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

        if ((pos < 0) || (pos >= 64) || (this.grid[(pos / 8)][(pos % 8)] != 0) || (!isMoveValid(pos, getPlayerType(player)))) {
            illegalPlayerMove(player);
            return;
        }

        this.grid[(pos / 8)][(pos % 8)] = getPlayerType(player).ordinal();
        this.moveDetails = "";
        updateSurroundings(pos, getPlayerType(player));
        this.view.update(this.grid);

        if (isGridFull()) {
            endMatch(player);
            return;
        }

        if (moveAvailable(getPlayerType(this.playerToMove).getOpponent())) {
            nextPlayer();
        } else if (!moveAvailable(getPlayerType(this.playerToMove))) {
            endMatch(player);
        }
    }

    private void endMatch(String player) {
        this.matchStatus = 1;
        int[] tileCounts = getTileCounts();

        if (tileCounts[0] > tileCounts[1]) {
            this.playerResults.put(player, Integer.valueOf(1));
            this.playerResults.put(otherPlayer(player), Integer.valueOf(-1));
        } else if (tileCounts[1] > tileCounts[0]) {
            this.playerResults.put(player, Integer.valueOf(-1));
            this.playerResults.put(otherPlayer(player), Integer.valueOf(1));
        } else {
            this.playerResults.put(player, Integer.valueOf(0));
            this.playerResults.put(otherPlayer(player), Integer.valueOf(0));
        }

        this.playerScores.put(player, Integer.valueOf(tileCounts[0]));
        this.playerScores.put(otherPlayer(player), Integer.valueOf(tileCounts[1]));
    }

    private boolean moveAvailable(PlayerType opponent) {
        for (int y = 0; y < this.grid.length; y++) {
            for (int x = 0; x < this.grid[y].length; x++) {
                if ((getTile(x, y) == PlayerType.NONE.ordinal()) && (willFlip(opponent, x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isMoveValid(int pos, PlayerType type) {
        int checkX = pos % 8;
        int checkY = pos / 8;

        return willFlip(type, checkX, checkY);
    }

    private boolean willFlip(PlayerType type, int checkX, int checkY) {
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            if (getTileLength(type, direction, checkX, checkY) > 0) {
                return true;
            }
        }
        return false;
    }

    private void updateSurroundings(int pos, PlayerType type) {
        int checkX = pos % 8;
        int checkY = pos / 8;

        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            int length = getTileLength(type, direction, checkX, checkY);
            if (length > 0) {
                flipTiles(type, direction, checkX, checkY, length);
            }
        }
    }

    private void flipTiles(PlayerType type, Direction direction, int x, int y, int length) {
        for (int i = 0; i < length; i++) {
            x += direction.offsetX;
            y += direction.offsetY;

            if (!tileInBounds(x, y)) {
                return;
            }

            setTile(x, y, type.ordinal());
        }
    }

    private boolean tileInBounds(int checkX, int checkY) {
        return (checkX >= 0) && (checkX < 8) && (checkY >= 0) && (checkY < 8);
    }


    private int getTileLength(PlayerType type, Direction direction, int checkX, int checkY) {
        int tileLength = 0;

        checkX += direction.offsetX;
        checkY += direction.offsetY;

        if (!tileInBounds(checkX, checkY)) {
            return 0;
        }

        while ((tileInBounds(checkX, checkY)) && (getTile(checkX, checkY) == type.getOpponent().ordinal())) {
            tileLength++;
            checkX += direction.offsetX;
            checkY += direction.offsetY;
        }

        return tileInBounds(checkX, checkY) ? 0 : getTile(checkX, checkY) == type.ordinal() ? tileLength : 0;
    }

    private int getTile(int checkX, int checkY) {
        return this.grid[checkY][checkX];
    }

    private void setTile(int x, int y, int tile) {
        this.grid[y][x] = tile;
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
        super.getPlayerScore(player);
        return ((Integer) this.playerScores.get(player)).intValue();
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
        endMatch(player);
        this.matchResult = (this.moveDetails = "Illegal move");
        this.playerResults.put(player, Integer.valueOf(-1));
        this.playerResults.put(otherPlayer(player), Integer.valueOf(1));
    }


    public int[] getTileCounts() {
        int playerOne = getPlayerType(this.playerToMove).ordinal();
        int playerTwo = getPlayerType(otherPlayer(this.playerToMove)).ordinal();

        int[] counts = {0, 0};

        for (int[] i : this.grid) {
            for (int j : i) {
                if (j == playerOne) {
                    counts[0] += 1;
                } else if (j == playerTwo) {
                    counts[1] += 1;
                }
            }
        }

        return counts;
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

    private enum Direction {
        UP(0, -1), LEFT_UP(-1, -1), LEFT(-1, 0), LEFT_DOWN(-1, 1), DOWN(0, 1), RIGHT_DOWN(1, 1),
        RIGHT(1, 0), RIGHT_UP(1, -1);

        public final int offsetX;
        public final int offsetY;

        Direction(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }
}