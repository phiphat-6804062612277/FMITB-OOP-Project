package ui;

import core.GameState;
import system.AssetManager;
import java.awt.*;

/**
 * EndingRenderer renders Game Over and Game Win screens.
 * Uses standard java.awt.Graphics and java.awt.Image taught in Lecture 10.
 */
public class EndingRenderer implements Renderer {
    private final int state;
    private Point mousePos = new Point(-1, -1);

    // Button bounds aligned with 1280x720 artwork
    public static final Rectangle RETRY_BTN_BOUNDS = new Rectangle(440, 581, 167, 67);
    public static final Rectangle MENU_BTN_BOUNDS = new Rectangle(667, 581, 173, 67);

    public EndingRenderer(int state) {
        this.state = state;
    }

    public void setMousePos(Point p) {
        this.mousePos = p;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        String imageName = (state == GameState.GAME_OVER) ? "game_over.png" : "game_win.png";
        Image img = AssetManager.getInstance().getImage(imageName);
        if (img != null) {
            g.drawImage(img, 0, 0, width, height, null);
        }
        drawHoverEffect(g);
    }

    private void drawHoverEffect(Graphics g) {
        if (mousePos == null) {
            return;
        }
        if (RETRY_BTN_BOUNDS.contains(mousePos)) {
            g.setColor(new Color(255, 255, 255, 60));
            g.fillRoundRect(RETRY_BTN_BOUNDS.x, RETRY_BTN_BOUNDS.y, RETRY_BTN_BOUNDS.width, RETRY_BTN_BOUNDS.height, 16, 16);
            g.setColor(new Color(255, 255, 255, 180));
            g.drawRoundRect(RETRY_BTN_BOUNDS.x, RETRY_BTN_BOUNDS.y, RETRY_BTN_BOUNDS.width, RETRY_BTN_BOUNDS.height, 16, 16);
            g.drawRoundRect(RETRY_BTN_BOUNDS.x + 1, RETRY_BTN_BOUNDS.y + 1, RETRY_BTN_BOUNDS.width - 2, RETRY_BTN_BOUNDS.height - 2, 16, 16);
        } else if (MENU_BTN_BOUNDS.contains(mousePos)) {
            g.setColor(new Color(255, 255, 255, 60));
            g.fillRoundRect(MENU_BTN_BOUNDS.x, MENU_BTN_BOUNDS.y, MENU_BTN_BOUNDS.width, MENU_BTN_BOUNDS.height, 16, 16);
            g.setColor(new Color(255, 255, 255, 180));
            g.drawRoundRect(MENU_BTN_BOUNDS.x, MENU_BTN_BOUNDS.y, MENU_BTN_BOUNDS.width, MENU_BTN_BOUNDS.height, 16, 16);
            g.drawRoundRect(MENU_BTN_BOUNDS.x + 1, MENU_BTN_BOUNDS.y + 1, MENU_BTN_BOUNDS.width - 2, MENU_BTN_BOUNDS.height - 2, 16, 16);
        }
    }
}
