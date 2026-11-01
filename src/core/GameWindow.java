package core;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * GameWindow extends JFrame to represent the main application window.
 * Sets up 1280x720 resolution, centering, title, and graceful shutdown.
 */
public class GameWindow extends JFrame {
    private final GamePanel gamePanel;

    public GameWindow() {
        super("Five Minutes In The Bathroom");
        this.gamePanel = new GamePanel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gamePanel.stopGameLoop();
            }
        });
    }

    public void showWindow() {
        setVisible(true);
        gamePanel.startGameLoop();
        gamePanel.requestFocusInWindow();
    }
}
