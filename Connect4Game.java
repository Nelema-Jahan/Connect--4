import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class Connect4Game {
    private JFrame frame;
    private Board board;
    private Player player1;
    private Player player2;
    private boolean isPvP;
    private boolean isPlayer1Turn = true;

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(230, 126, 34);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);

    public Connect4Game() {
        initializeGame();
    }

    private void initializeGame() {
        frame = new JFrame("Connect 4 Game");
        frame.setSize(700, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setBackground(BACKGROUND_COLOR);
        showStartScreen();
    }

    private void showStartScreen() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        // Title
        JLabel title = new JLabel("CONNECT 4");
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);

        // Subtitle
        JLabel subtitle = new JLabel("Strategic Disc-Dropping Game");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitle.setForeground(new Color(189, 195, 199));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitle);

        mainPanel.add(Box.createVerticalStrut(50));

        // Description
        JLabel description = new JLabel("Choose Your Game Mode:");
        description.setFont(new Font("Arial", Font.BOLD, 16));
        description.setForeground(Color.WHITE);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(description);

        mainPanel.add(Box.createVerticalStrut(30));

        // PvP Button
        JButton pvpBtn = createStyledButton("👥 Player vs Player", new Color(46, 204, 113));
        pvpBtn.addActionListener(e -> {
            isPvP = true;
            setupPlayers();
        });
        mainPanel.add(pvpBtn);

        mainPanel.add(Box.createVerticalStrut(20));

        // PvC Button
        JButton pvcBtn = createStyledButton("🤖 Player vs Computer", new Color(52, 152, 219));
        pvcBtn.addActionListener(e -> {
            isPvP = false;
            setupPlayers();
        });
        mainPanel.add(pvcBtn);

        mainPanel.add(Box.createVerticalStrut(30));

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(300, 60));
        btn.setPreferredSize(new Dimension(300, 60));
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    private void setupPlayers() {
        player1 = new Player("Player 1", Color.RED, 1);
        player2 = new Player(isPvP ? "Player 2" : "Computer", Color.YELLOW, 2);
        initGameBoard();
    }

    private void initGameBoard() {
        board = new Board(this, player1, player2, isPvP);
        frame.getContentPane().removeAll();
        frame.add(board.getBoardPanel());
        frame.revalidate();
        frame.repaint();
        board.updateStatus();
    }

    public void switchTurn() {
        isPlayer1Turn = !isPlayer1Turn;
    }

    public Player getCurrentPlayer() {
        return isPlayer1Turn ? player1 : player2;
    }

    public void resetGame(boolean pvp) {
        isPvP = pvp;
        isPlayer1Turn = true;
        setupPlayers();
    }

    public void showStartMenu() {
        frame.getContentPane().removeAll();
        showStartScreen();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Connect4Game());
    }
}