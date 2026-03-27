public class AI {
    private final int AI_PLAYER;
    private final int HUMAN_PLAYER;
    private final int ROWS = 6;
    private final int COLS = 7;

    public AI(int aiPlayer) {
        this.AI_PLAYER = aiPlayer;
        this.HUMAN_PLAYER = (aiPlayer == 1) ? 2 : 1;
    }

    public int[] minimax(int[][] board, int depth, int alpha, int beta, boolean maximizing) {
        int bestCol = -1;
        int bestScore = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        if (depth == 0 || isGameOver(board)) {
            return new int[]{bestCol, evaluateBoard(board)};
        }

        for (int col = 0; col < COLS; col++) {
            int row = getAvailableRow(board, col);
            if (row != -1) {
                board[row][col] = maximizing ? AI_PLAYER : HUMAN_PLAYER;

                int score = minimax(board, depth - 1, alpha, beta, !maximizing)[1];

                board[row][col] = 0;

                if (maximizing) {
                    if (score > bestScore) {
                        bestScore = score;
                        bestCol = col;
                    }
                    alpha = Math.max(alpha, bestScore);
                } else {
                    if (score < bestScore) {
                        bestScore = score;
                        bestCol = col;
                    }
                    beta = Math.min(beta, bestScore);
                }

                if (beta <= alpha) break;
            }
        }

        return new int[]{bestCol, bestScore};
    }

    private boolean isGameOver(int[][] board) {
        return checkForWin(board, AI_PLAYER) ||
               checkForWin(board, HUMAN_PLAYER) ||
               isBoardFull(board);
    }

    private int getAvailableRow(int[][] board, int col) {
        for (int r = ROWS - 1; r >= 0; r--) {
            if (board[r][col] == 0) return r;
        }
        return -1;
    }

    private int evaluateBoard(int[][] board) {
        int score = evaluateCenter(board);
        score += evaluateLines(board, AI_PLAYER);
        score -= evaluateLines(board, HUMAN_PLAYER);
        return score;
    }

    private int evaluateCenter(int[][] board) {
        int score = 0;
        int center = COLS / 2;

        for (int r = 0; r < ROWS; r++) {
            if (board[r][center] == AI_PLAYER) score += 3;
        }
        return score;
    }

    private int evaluateLines(int[][] board, int player) {
        int score = 0;

        // Horizontal
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS - 3; c++)
                score += evaluateWindow(new int[]{
                        board[r][c], board[r][c+1], board[r][c+2], board[r][c+3]
                }, player);

        // Vertical
        for (int c = 0; c < COLS; c++)
            for (int r = 0; r < ROWS - 3; r++)
                score += evaluateWindow(new int[]{
                        board[r][c], board[r+1][c], board[r+2][c], board[r+3][c]
                }, player);

        // Diagonal \
        for (int r = 0; r < ROWS - 3; r++)
            for (int c = 0; c < COLS - 3; c++)
                score += evaluateWindow(new int[]{
                        board[r][c], board[r+1][c+1], board[r+2][c+2], board[r+3][c+3]
                }, player);

        // Diagonal /
        for (int r = 0; r < ROWS - 3; r++)
            for (int c = 3; c < COLS; c++)
                score += evaluateWindow(new int[]{
                        board[r][c], board[r+1][c-1], board[r+2][c-2], board[r+3][c-3]
                }, player);

        return score;
    }

    private int evaluateWindow(int[] window, int player) {
        int score = 0;
        int opponent = (player == AI_PLAYER) ? HUMAN_PLAYER : AI_PLAYER;

        int playerCount = 0, empty = 0, oppCount = 0;

        for (int x : window) {
            if (x == player) playerCount++;
            else if (x == opponent) oppCount++;
            else empty++;
        }

        if (playerCount == 4) score += 100000;
        else if (playerCount == 3 && empty == 1) score += 100;
        else if (playerCount == 2 && empty == 2) score += 10;

        if (oppCount == 3 && empty == 1) score -= 80;

        return score;
    }

    private boolean checkForWin(int[][] board, int player) {
        for (int r = 0; r < ROWS; r++)
            for (int c = 0; c < COLS; c++)
                if (board[r][c] == player &&
                        (checkDir(board, r, c, player, 1, 0) ||
                         checkDir(board, r, c, player, 0, 1) ||
                         checkDir(board, r, c, player, 1, 1) ||
                         checkDir(board, r, c, player, 1, -1)))
                    return true;
        return false;
    }

    private boolean checkDir(int[][] b, int r, int c, int p, int dr, int dc) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            int nr = r + i * dr;
            int nc = c + i * dc;
            if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS || b[nr][nc] != p)
                return false;
            count++;
        }
        return count == 4;
    }

    private boolean isBoardFull(int[][] board) {
        for (int c = 0; c < COLS; c++)
            if (board[0][c] == 0) return false;
        return true;
    }
}
