package ui;

import system.AssetManager;
import java.awt.*;

/**
 * MenuRenderer draws the Main Menu screen:
 * - Background image (menu_bg.jpg)
 * - Stacked monospaced title on the left
 * - Text-based menu options (Start, How To Play, Exit) with >> hover indicators
 */
public class MenuRenderer implements Renderer {
    public static final Rectangle START_BTN_BOUNDS = new Rectangle(90, 420, 260, 50);
    public static final Rectangle HOW_TO_PLAY_BTN_BOUNDS = new Rectangle(90, 490, 280, 50);
    public static final Rectangle EXIT_BTN_BOUNDS = new Rectangle(90, 560, 200, 50);

    private Point mousePos = new Point(-1, -1);

    public void setMousePos(Point p) {
        this.mousePos = p;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        // 1. Background image
        Image bg = AssetManager.getInstance().getImage("menu_bg.jpg");
        if (bg != null) {
            g.drawImage(bg, 0, 0, width, height, null);
        }

        // 2. Subtle VHS / CRT Scanlines across the screen
        g.setColor(new Color(0, 0, 0, 28));
        for (int row = 0; row < height; row += 4) {
            g.fillRect(0, row, width, 1);
        }

        // 3. Stacked Monospaced Title
        drawFNAFTitle(g, 90, 105);

        // 4. Text Menu Buttons (Start, How To Play, Exit)
        drawFNAFButton(g, START_BTN_BOUNDS, "Start");
        drawFNAFButton(g, HOW_TO_PLAY_BTN_BOUNDS, "How To Play");
        drawFNAFButton(g, EXIT_BTN_BOUNDS, "Exit");
    }

    private void drawFNAFTitle(Graphics g, int x, int y) {
        Font titleFont = new Font("Monospaced", Font.BOLD, 52);
        g.setFont(titleFont);

        String[] lines = {
            "Five",
            "Minutes",
            "In The",
            "Bathroom"
        };

        int lineSpacing = 60;
        for (int i = 0; i < lines.length; i++) {
            String text = lines[i];
            int lineY = y + (i * lineSpacing);

            // Dark drop shadow
            g.setColor(new Color(0, 0, 0, 210));
            g.drawString(text, x + 3, lineY + 3);
            g.drawString(text, x + 2, lineY + 2);

            // Crisp monospaced white text
            g.setColor(new Color(240, 240, 245));
            g.drawString(text, x, lineY);
        }
    }

    private void drawFNAFButton(Graphics g, Rectangle b, String text) {
        boolean isHovered = (mousePos != null && b.contains(mousePos));

        g.setFont(new Font("Monospaced", Font.BOLD, 34));

        int drawX = b.x;
        int drawY = b.y + 36;

        String displayText;
        if (isHovered) {
            displayText = ">> " + text;
        } else {
            displayText = "   " + text;
        }

        // Shadow
        g.setColor(new Color(0, 0, 0, 220));
        g.drawString(displayText, drawX + 3, drawY + 3);

        // Hover effect
        if (isHovered) {
            g.setColor(Color.WHITE);
            g.drawString(displayText, drawX, drawY);
        } else {
            g.setColor(new Color(190, 195, 200));
            g.drawString(displayText, drawX, drawY);
        }
    }
}
