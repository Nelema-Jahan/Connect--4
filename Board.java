import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Board {
    private static final int ROWS = 6;
    private static final int COLS = 7;

    private DiscPanel[][] boardPanels;
    private int[][] board;

    private Connect4Game game;
    private Player player1;
    private Player player2;
    private boolean isPvP;

    private AI ai;
    private JPanel container;
    private JLabel statusLabel;

    private static final Color BOARD_COLOR = new Color(52, 73, 94);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color ACCENT_COLOR = new Color(230, 126, 34);

    public Board(Connect4Game game, Player player1, Player player2, boolean isPvP) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        this.isPvP = isPvP;

        board = new int[ROWS][COLS];
        boardPanels = new DiscPanel[ROWS][COLS];

        initializeBoard();

        if (!isPvP) {
            ai = new AI(player2.getId());
        }
    }

    private void initializeBoard() {
        JPanel gridPanel = new JPanel(new GridLayout(ROWS, COLS, 3, 3));
        gridPanel.setBackground(BOARD_COLOR);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                boardPanels[r][c] = new DiscPanel();
                boardPanels[r][c].setBackground(new Color(52, 152, 219));
                boardPanels[r][c].setOpaque(true);
                boardPanels[r][c].setBorder(BorderFactory.createLineBorder(BOARD_COLOR, 1));

                int col = c;
                int row = r;

                boardPanels[r][c].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        onColumnClick(col);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        boardPanels[row][col].setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                });

                gridPanel.add(boardPanels[r][c]);
            }
        }

        // Create status label
        statusLabel = new JLabel("🔴 Player 1's Turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBackground(ACCENT_COLOR);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top panel for status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(statusLabel, BorderLayout.CENTER);

        // Attach panel reference
        container = new JPanel(new BorderLayout());
        container.setBackground(BACKGROUND_COLOR);
        container.add(topPanel, BorderLayout.NORTH);
        container.add(gridPanel, BorderLayout.CENTER);
    }

    public JPanel getBoardPanel() {
        return container;
    }

    public void updateStatus() {
        Player current = game.getCurrentPlayer();
        String emoji = (current == player1) ? "🔴" : "🟡";
        statusLabel.setText(emoji + " " + current.getName() + "'s Turn");
    }

    private void onColumnClick(int col) {
        Player current = game.getCurrentPlayer();

        if (current == player1 || (isPvP && current == player2)) {
            if (makeMove(col, current)) {
                String emoji = (current == player1) ? "🔴" : "🟡";
                statusLabel.setText(emoji + " " + current.getName() + " WINS! 🎉");
                statusLabel.setBackground(new Color(46, 204, 113));
                disableAllColumns();
                showWinNotification(current, emoji);
            } else {
                game.switchTurn();
                updateStatus();

                if (!isPvP && game.getCurrentPlayer() == player2) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        computerMove();
                    });
                }
            }
        }
    }

    private boolean makeMove(int col, Player player) {
        for (int r = ROWS - 1; r >= 0; r--) {
            if (board[r][col] == 0) {
                board[r][col] = player.getId();

                boardPanels[r][col].setDiscColor(player.getColor());
                boardPanels[r][col].repaint();

                return checkWinner(r, col, player.getId());
            }
        }
        return false;
    }

    private boolean checkWinner(int row, int col, int playerId) {
        return checkDirection(row, col, playerId, 1, 0) ||   // vertical
               checkDirection(row, col, playerId, 0, 1) ||   // horizontal
               checkDirection(row, col, playerId, 1, 1) ||   // diagonal \
               checkDirection(row, col, playerId, 1, -1);    // diagonal /
    }

    private boolean checkDirection(int row, int col, int playerId, int dr, int dc) {
        int count = 1;

        // forward
        for (int i = 1; i < 4; i++) {
            int r = row + i * dr;
            int c = col + i * dc;

            if (r < 0 || r >= ROWS || c < 0 || c >= COLS || board[r][c] != playerId)
                break;

            count++;
        }

        // backward
        for (int i = 1; i < 4; i++) {
            int r = row - i * dr;
            int c = col - i * dc;

            if (r < 0 || r >= ROWS || c < 0 || c >= COLS || board[r][c] != playerId)
                break;

            count++;
        }

        return count >= 4;
    }

    public void computerMove() {
        int[] result = ai.minimax(board, 6, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        int col = result[0];

        if (col != -1) {
            if (makeMove(col, player2)) {
                statusLabel.setText("🟡 Computer WINS! 🎉");
                statusLabel.setBackground(new Color(46, 204, 113));
                disableAllColumns();
                showWinNotification(player2, "🟡");
            } else {
                game.switchTurn();
                updateStatus();
            }
        }
    }

    private void disableAllColumns() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                boardPanels[r][c].removeMouseListener(
                    boardPanels[r][c].getMouseListeners()[0]
                );
            }
        }
    }

    private void showWinNotification(Player winner, String emoji) {
        JDialog dialog = new JDialog((JFrame) null, "🎉 WINNER! 🎉", false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw rounded rectangle background
                g2d.setColor(new Color(46, 204, 113));
                int arc = 25;
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                
                // Draw border
                g2d.setColor(new Color(39, 174, 96));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setOpaque(false);

        // Winner emoji and name
        JLabel winnerLabel = new JLabel(emoji + " " + winner.getName());
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        winnerLabel.setForeground(Color.WHITE);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(winnerLabel);

        panel.add(Box.createVerticalStrut(8));

        // "WINS!" text
        JLabel winsLabel = new JLabel("WINS! 🏆");
        winsLabel.setFont(new Font("Arial", Font.BOLD, 36));
        winsLabel.setForeground(Color.WHITE);
        winsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(winsLabel);

        panel.add(Box.createVerticalStrut(15));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Play Again button
        JButton playAgainBtn = new JButton("Play Again");
        playAgainBtn.setFont(new Font("Arial", Font.BOLD, 14));
        playAgainBtn.setForeground(new Color(46, 204, 113));
        playAgainBtn.setBackground(Color.WHITE);
        playAgainBtn.setFocusPainted(false);
        playAgainBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        playAgainBtn.setPreferredSize(new Dimension(130, 40));
        playAgainBtn.setMaximumSize(new Dimension(130, 40));
        playAgainBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        playAgainBtn.addActionListener(e -> {
            dialog.dispose();
            game.resetGame(isPvP);
        });

        playAgainBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                playAgainBtn.setBackground(new Color(189, 195, 199));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                playAgainBtn.setBackground(Color.WHITE);
            }
        });

        // Back button
        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));
        backBtn.setForeground(new Color(230, 126, 34));
        backBtn.setBackground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        backBtn.setPreferredSize(new Dimension(130, 40));
        backBtn.setMaximumSize(new Dimension(130, 40));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backBtn.addActionListener(e -> {
            dialog.dispose();
            game.showStartMenu();
        });

        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backBtn.setBackground(new Color(189, 195, 199));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                backBtn.setBackground(Color.WHITE);
            }
        });

        buttonPanel.add(playAgainBtn);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(backBtn);

        panel.add(buttonPanel);

        dialog.setContentPane(panel);
        dialog.setSize(380, 280);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}