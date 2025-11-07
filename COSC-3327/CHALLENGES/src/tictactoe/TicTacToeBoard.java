package tictactoe;

public interface TicTacToeBoard {
    int ROW_COUNT = 3;
    int COLUMN_COUNT = 3;

    /** Returns X or O for a filled cell; null if empty. */
    Mark getMark(int row, int column);

    /** Place the current player's mark at (row, column). Preconditions: in-bounds, empty, not game-over. */
    void setMark(int row, int column);

    /** Whose turn is it? Returns X or O; returns null if the game is over. */
    Mark getTurn();

    /** Is the game over (win or tie)? */
    boolean isGameOver();

    /** Winner if any. Pre: isGameOver()==true. Returns X or O if someone won; null if tie. */
    Mark getWinner();

    /** Board pretty-print (X| |O etc.). */
    String toString();
}
