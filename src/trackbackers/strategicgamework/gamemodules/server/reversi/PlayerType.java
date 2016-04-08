package trackbackers.strategicgamework.gamemodules.server.reversi;

public enum PlayerType {
    NONE, PLAYER_ONE, PLAYER_TWO;

    public PlayerType getOpponent() {
        return this == PLAYER_TWO ? PLAYER_ONE : this == PLAYER_ONE ? PLAYER_TWO : NONE;
    }
}