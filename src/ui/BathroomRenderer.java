package ui;

import system.*;
import java.awt.*;

/**
 * BathroomRenderer draws the first-person view in the bathroom.
 * Uses standard java.awt.Graphics methods taught in Lecture 10.
 */
public class BathroomRenderer implements Renderer {
    private final BathroomManager bathroom;
    private final GameTimer timer;
    private final MonsterAI monsterAI;

    // Clickable hitboxes and rendering bounds
    public static final Rectangle DOOR_BOUNDS = new Rectangle(614, 72, 260, 522);
    public static final Rectangle VENT_BOUNDS = new Rectangle(42, 2, 298, 330);
    public static final Rectangle TABLET_BTN_BOUNDS = new Rectangle(440, 652, 400, 52);

    public BathroomRenderer(BathroomManager bathroom, GameTimer timer, MonsterAI monsterAI) {
        this.bathroom = bathroom;
        this.timer = timer;
        this.monsterAI = monsterAI;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        // 1. Draw Background
        Image bg = AssetManager.getInstance().getImage("bathroom_bg.png");
        if (bg != null) {
            g.drawImage(bg, 0, 0, width, height, null);
        }

        // 2. Draw Door (Closed, Open, or with Monster sprite)
        drawDoor(g);

        // 3. Draw Vent (Closed, Open, or with Monster sprite)
        drawVent(g);

        // 4. Draw HUD: Clock, Stamina Bar, Control Button
        drawHUD(g, width);
    }

    private void drawDoor(Graphics g) {
        Image doorImg;
        if (bathroom.isDoorClosed()) {
            doorImg = AssetManager.getInstance().getImage("door_closed.png");
        } else if (monsterAI.getSahur().isAtAttackPoint()) {
            doorImg = AssetManager.getInstance().getImage("sahur_at_door.png");
        } else {
            doorImg = AssetManager.getInstance().getImage("door_open.png");
        }

        if (doorImg != null) {
            g.drawImage(doorImg, DOOR_BOUNDS.x, DOOR_BOUNDS.y, DOOR_BOUNDS.width, DOOR_BOUNDS.height, null);
        }
    }

    private void drawVent(Graphics g) {
        Image ventImg;
        if (bathroom.isVentClosed()) {
            ventImg = AssetManager.getInstance().getImage("vent_closed.png");
        } else if (monsterAI.getTralala().isAtAttackPoint()) {
            ventImg = AssetManager.getInstance().getImage("tralala_at_vent.png");
        } else {
            ventImg = AssetManager.getInstance().getImage("vent_open.png");
        }

        if (ventImg != null) {
            g.drawImage(ventImg, VENT_BOUNDS.x, VENT_BOUNDS.y, VENT_BOUNDS.width, VENT_BOUNDS.height, null);
        }
    }

    private void drawHUD(Graphics g, int w) {
        // 1. 5-minute countdown timer in top right
        int boxW = 160;
        int boxH = 50;
        int boxX = w - boxW - 30;
        int boxY = 20;

        g.setColor(Color.WHITE);
        g.fillRoundRect(boxX, boxY, boxW, boxH, 16, 16);
        g.setColor(new Color(220, 40, 40));
        g.drawRoundRect(boxX, boxY, boxW, boxH, 16, 16);
        g.drawRoundRect(boxX + 1, boxY + 1, boxW - 2, boxH - 2, 16, 16);

        String timeStr = timer.getFormattedRemainingTime();
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        int strX = boxX + (boxW - fm.stringWidth(timeStr)) / 2;
        int strY = boxY + (boxH + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(timeStr, strX, strY);

        // 2. Stamina Bar (Bottom-Left)
        StaminaManager stamina = bathroom.getStaminaManager();
        int stX = 40;
        int stY = 665;
        int stW = 260;
        int stH = 24;

        // Dark track background
        g.setColor(new Color(20, 25, 30, 210));
        g.fillRoundRect(stX, stY, stW, stH, 10, 10);

        // Fill stamina bar (green normally, flashing red if exhausted)
        float pct = stamina.getStaminaPercentage();
        int fillW = (int) (stW * pct);
        if (fillW > 0) {
            Color barColor = new Color(45, 205, 80);
            if (stamina.isExhausted()) {
                boolean flash = (System.currentTimeMillis() % 400 < 200);
                if (flash) {
                    barColor = new Color(230, 45, 45);
                } else {
                    barColor = new Color(90, 25, 25);
                }
            }
            g.setColor(barColor);
            g.fillRoundRect(stX, stY, fillW, stH, 10, 10);
        }

        // White border
        g.setColor(new Color(255, 255, 255, 200));
        g.drawRoundRect(stX, stY, stW, stH, 10, 10);
        g.drawRoundRect(stX + 1, stY + 1, stW - 2, stH - 2, 10, 10);

        // 3. Tablet Button at bottom
        drawButton(g, TABLET_BTN_BOUNDS, "▲ OPEN CCTV MONITOR [SPACE] ▲", new Color(30, 110, 220));
    }

    private void drawButton(Graphics g, Rectangle bounds, String text, Color bg) {
        g.setColor(bg);
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
        g.setColor(Color.WHITE);
        g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
        g.drawRoundRect(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2, 12, 12);

        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        int strW = g.getFontMetrics().stringWidth(text);
        int strH = g.getFontMetrics().getAscent();
        g.drawString(text, bounds.x + (bounds.width - strW) / 2, bounds.y + (bounds.height + strH) / 2 - 4);
    }
}
