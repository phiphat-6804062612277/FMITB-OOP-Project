package ui;

import system.AssetManager;
import java.awt.*;

/**
 * HowToPlayRenderer renders the instructions and guide screen.
 */
public class HowToPlayRenderer implements Renderer {
    public static final Rectangle BACK_BTN_BOUNDS = new Rectangle(80, 630, 280, 50);

    private Point mousePos = new Point(-1, -1);

    public void setMousePos(Point p) {
        this.mousePos = p;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        // 1. Dimmed Background image
        Image bg = AssetManager.getInstance().getImage("menu_bg.jpg");
        if (bg != null) {
            g.drawImage(bg, 0, 0, width, height, null);
        }
        g.setColor(new Color(10, 10, 15, 220));
        g.fillRect(0, 0, width, height);

        // 2. Title
        g.setFont(new Font("Monospaced", Font.BOLD, 42));
        g.setColor(Color.WHITE);
        g.drawString("HOW TO PLAY", 80, 75);

        g.setFont(new Font("Monospaced", Font.PLAIN, 15));
        g.setColor(new Color(100, 220, 160));
        g.drawString("SURVIVAL GUIDE // NIGHT PROTOCOL", 80, 105);

        g.setColor(new Color(60, 80, 90));
        g.drawLine(80, 118, width - 80, 118);
        g.drawLine(80, 119, width - 80, 119);

        // 3. Sections
        int startY = 155;
        int lineGap = 26;

        // Section 1: Objective
        drawSectionHeader(g, "1. OBJECTIVE", 80, startY);
        startY += 28;
        drawBodyText(g, "Survive 5 minutes in the bathroom stall (countdown to 00:00).", 100, startY);
        startY += 40;

        // Section 2: How to Survive
        drawSectionHeader(g, "2. HOW TO SURVIVE", 80, startY);
        startY += 28;
        drawHighlightText(g, "• 1. WATCH  :", new Color(100, 220, 255), 100, startY);
        drawBodyText(g, "Check CCTV cameras to monitor monster positions across floors.", 235, startY);
        startY += lineGap + 4;

        drawHighlightText(g, "• 2. DEFEND :", new Color(255, 120, 120), 100, startY);
        drawBodyText(g, "Block the Door [E] when Sahur arrives.", 235, startY);
        startY += lineGap;
        drawBodyText(g, "– Block the Vent [Q] when Tralala peeks in.", 235, startY);
        startY += lineGap;
        drawBodyText(g, "– Keep defenses closed until they leave!", 235, startY);
        startY += lineGap + 4;

        drawHighlightText(g, "• 3. STAMINA:", new Color(100, 255, 160), 100, startY);
        drawBodyText(g, "Do not keep doors closed for too long. Watch your green stamina bar!", 235, startY);
        startY += 45;

        // Section 3: Controls
        drawSectionHeader(g, "3. CONTROLS", 80, startY);
        startY += 28;
        drawKeyHint(g, "[ E ]", "Toggle Main Door", 100, startY);
        drawKeyHint(g, "[ Q ]", "Toggle Air Vent", 400, startY);
        drawKeyHint(g, "[ SPACE ]", "Flip CCTV Tablet", 680, startY);
        startY += lineGap + 2;
        drawKeyHint(g, "[ MOUSE ]", "Select Cam / Switch Floor", 100, startY);
        drawKeyHint(g, "[ ESC ]", "Back to Menu", 680, startY);

        // 4. Back Button
        boolean isHovered = (mousePos != null && BACK_BTN_BOUNDS.contains(mousePos));
        g.setFont(new Font("Monospaced", Font.BOLD, 26));
        if (isHovered) {
            g.setColor(new Color(100, 255, 160));
            g.drawString(">> BACK TO MENU [ESC]", BACK_BTN_BOUNDS.x, BACK_BTN_BOUNDS.y + 35);
        } else {
            g.setColor(new Color(180, 185, 190));
            g.drawString("   BACK TO MENU [ESC]", BACK_BTN_BOUNDS.x, BACK_BTN_BOUNDS.y + 35);
        }

        // 5. Subtle scanlines
        g.setColor(new Color(0, 0, 0, 30));
        for (int row = 0; row < height; row += 4) {
            g.fillRect(0, row, width, 1);
        }
    }

    private void drawSectionHeader(Graphics g, String title, int x, int y) {
        g.setFont(new Font("Monospaced", Font.BOLD, 17));
        g.setColor(new Color(255, 215, 100));
        g.drawString(title, x, y);
    }

    private void drawBodyText(Graphics g, String text, int x, int y) {
        g.setFont(new Font("Monospaced", Font.PLAIN, 15));
        g.setColor(new Color(220, 220, 225));
        g.drawString(text, x, y);
    }

    private void drawHighlightText(Graphics g, String text, Color c, int x, int y) {
        g.setFont(new Font("Monospaced", Font.BOLD, 15));
        g.setColor(c);
        g.drawString(text, x, y);
    }

    private void drawKeyHint(Graphics g, String key, String desc, int x, int y) {
        g.setFont(new Font("Monospaced", Font.BOLD, 15));
        g.setColor(new Color(100, 220, 160));
        g.drawString(key, x, y);
        int kw = g.getFontMetrics().stringWidth(key);
        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        g.setColor(new Color(210, 210, 215));
        g.drawString(": " + desc, x + Math.max(85, kw + 8), y);
    }
}
