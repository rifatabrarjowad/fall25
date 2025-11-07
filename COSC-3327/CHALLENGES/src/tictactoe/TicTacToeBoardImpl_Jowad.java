package tictactoe;

public class TicTacToeBoardImpl_Jowad implements TicTacToeBoard {

    // The ONLY instance variable for board state.
    private final int[] movesArray;

    // Internal sentinel
    private static final int NO_MOVE = -1;

    // ---------- ctor ----------
    public TicTacToeBoardImpl_Jowad() {
        movesArray = new int[TicTacToeBoard.ROW_COUNT * TicTacToeBoard.COLUMN_COUNT];
        for (int i = 0; i < movesArray.length; i++) movesArray[i] = NO_MOVE;
    }

    // ---------- public API ----------
    @Override
    public Mark getMark(int row, int column) {
        assert inBounds(row, column) : "row/col out of bounds";
        int mv = movesArray[toIndex(row, column)];
        return (mv == NO_MOVE) ? null : moveIndexToMark(mv);
    }

    @Override
    public void setMark(int row, int column) {
        assert inBounds(row, column) : "row/col out of bounds";
        assert getMark(row, column) == null : "cell must be empty";
        assert !isGameOver() : "game already over";
        movesArray[toIndex(row, column)] = countFilled();
    }

    @Override
    public Mark getTurn() {
        if (isGameOver()) return null;
        int nextMove = countFilled();
        return (nextMove % 2 == 0) ? Mark.X : Mark.O; // X starts
    }

    @Override
    public boolean isGameOver() {
        return hasWinner() || countFilled() == movesArray.length;
    }

    @Override
    public Mark getWinner() {
        assert isGameOver() : "pre: game must be over";
        // check all win lines for same parity (all even => X, all odd => O)
        for (int[] line : WIN_LINES) {
            int a = movesArray[line[0]], b = movesArray[line[1]], c = movesArray[line[2]];
            if (a != NO_MOVE && b != NO_MOVE && c != NO_MOVE) {
                if ((a & 1) == (b & 1) && (b & 1) == (c & 1)) {
                    return moveIndexToMark(a);
                }
            }
        }
        // tie
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < TicTacToeBoard.ROW_COUNT; r++) {
            for (int c = 0; c < TicTacToeBoard.COLUMN_COUNT; c++) {
                Mark m = getMark(r, c);
                sb.append(m == null ? ' ' : (m == Mark.X ? 'X' : 'O'));
                if (c < TicTacToeBoard.COLUMN_COUNT - 1) sb.append("|");
            }
            if (r < TicTacToeBoard.ROW_COUNT - 1) sb.append(System.lineSeparator()).append("_____").append(System.lineSeparator());
        }
        return sb.toString();
    }

    // ---------- helpers (private) ----------

    // As on the board: translate row/col -> 1D index
    private static int toIndex(int row, int col) { return row * TicTacToeBoard.COLUMN_COUNT + col; }

    private static boolean inBounds(int row, int col) {
        return 0 <= row && row < TicTacToeBoard.ROW_COUNT && 0 <= col && col < TicTacToeBoard.COLUMN_COUNT;
    }

    // count of filled squares = index of next move
    private int countFilled() {
        int cnt = 0;
        for (int v : movesArray) if (v != NO_MOVE) cnt++;
        return cnt;
    }

    private static Mark moveIndexToMark(int moveIndex) {
        return (moveIndex % 2 == 0) ? Mark.X : Mark.O;
    }

    private boolean hasWinner() {
        for (int[] line : WIN_LINES) {
            int a = movesArray[line[0]], b = movesArray[line[1]], c = movesArray[line[2]];
            if (a != NO_MOVE && b != NO_MOVE && c != NO_MOVE) {
                if ((a & 1) == (b & 1) && (b & 1) == (c & 1)) return true;
            }
        }
        return false;
    }

    // All 8 winning triples in row-major indices
    private static final int[][] WIN_LINES = new int[][]{
            {0,1,2},{3,4,5},{6,7,8}, // rows
            {0,3,6},{1,4,7},{2,5,8}, // cols
            {0,4,8},{2,4,6}          // diagonals
    };

    // Small utility mirrored from your “GUI needs” note:
    // post: rv == m*n
    public static int square(int m, int n) { return m * n; }
}
