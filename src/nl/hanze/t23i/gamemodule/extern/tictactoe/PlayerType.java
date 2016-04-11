package nl.hanze.t23i.gamemodule.extern.tictactoe;

public enum PlayerType {
    NONE(' '), PLAYER_ONE('X'), PLAYER_TWO('O');

    public final char symbol;

    PlayerType(char symbol) {
        this.symbol = symbol;
    }
}