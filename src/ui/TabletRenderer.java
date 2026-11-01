package ui;

import system.*;
import java.awt.*;

/**
 * TabletRenderer draws the CCTV Tablet monitoring system.
 * Uses standard java.awt.Graphics and java.awt.Image taught in Lecture 10.
 */
public class TabletRenderer implements Renderer {
    private final CameraManager camera;
    private final GameTimer timer;
    private final MonsterAI monsterAI;

    // Interactive button hitboxes aligned with the minimap on 1280x720 canvas
    public static final Rectangle CAM_1_BTN = new Rectangle(920, 505, 65, 55);
    public static final Rectangle CAM_2_BTN = new Rectangle(980, 545, 65, 60);
    public static final Rectangle CAM_3_BTN = new Rectangle(980, 545, 65, 60);
    public static final Rectangle CAM_4_BTN = new Rectangle(980, 430, 65, 55);
    public static final Rectangle FLOOR_TOGGLE_BTN_BOUNDS = new Rectangle(1060, 630, 165, 55);
    public static final Rectangle RETURN_BTN_BOUNDS = new Rectangle(400, 660, 480, 60);

    public TabletRenderer(CameraManager camera, GameTimer timer, MonsterAI monsterAI) {
        this.camera = camera;
        this.timer = timer;
        this.monsterAI = monsterAI;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        // 1. Resolve which of the 10 camera images to display
        Image scene = getActiveCameraScene();
        if (scene != null) {
            g.drawImage(scene, 0, 0, width, height, null);
        }

        // 2. Subtle CCTV recording overlay (REC dot & Clock)
        drawCCTVOverlay(g, width);
    }

    /**
     * Resolves the exact pre-rendered image among the 10 camera assets:
     * - CAM 1: cam1_tralala_sahur, cam1_sahur, cam1_tralala, or cam1_empty
     * - CAM 2: cam2_sahur or cam2_empty
     * - CAM 3: cam3_sahur or cam3_empty
     * - CAM 4: cam4_sahur or cam4_empty
     */
    private Image getActiveCameraScene() {
        CameraId cam = camera.getActiveCamera();
        AssetManager am = AssetManager.getInstance();

        int sahurStage = monsterAI.getSahur().getStage();
        boolean sahurAtDoor = monsterAI.getSahur().isAtAttackPoint();

        int tralalaStage = monsterAI.getTralala().getStage();
        boolean tralalaAtVent = monsterAI.getTralala().isAtAttackPoint();

        if (cam == CameraId.C1) {
            boolean sahurHere = (!sahurAtDoor && sahurStage == 0);
            boolean tralalaHere = (!tralalaAtVent && tralalaStage == 0);

            if (sahurHere && tralalaHere) {
                return am.getImage("cam1_tralala_sahur.png");
            } else if (sahurHere) {
                return am.getImage("cam1_sahur.png");
            } else if (tralalaHere) {
                return am.getImage("cam1_tralala.png");
            } else {
                return am.getImage("cam1_empty.png");
            }
        } else if (cam == CameraId.C2) {
            boolean sahurHere = (!sahurAtDoor && sahurStage == 1);
            if (sahurHere) {
                return am.getImage("cam2_sahur.png");
            } else {
                return am.getImage("cam2_empty.png");
            }
        } else if (cam == CameraId.C3) {
            boolean sahurHere = (!sahurAtDoor && sahurStage == 2);
            if (sahurHere) {
                return am.getImage("cam3_sahur.png");
            } else {
                return am.getImage("cam3_empty.png");
            }
        } else if (cam == CameraId.C4) {
            boolean sahurHere = (!sahurAtDoor && sahurStage == 3);
            if (sahurHere) {
                return am.getImage("cam4_sahur.png");
            } else {
                return am.getImage("cam4_empty.png");
            }
        }

        return am.getImage("cam1_empty.png");
    }

    private void drawCCTVOverlay(Graphics g, int width) {
        // Blinking REC Indicator
        if (System.currentTimeMillis() % 1200 < 700) {
            g.setColor(new Color(230, 40, 40));
            g.fillOval(50, 42, 14, 14);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(Color.WHITE);
            g.drawString("REC", 72, 54);
        }

        // Countdown Timer in top right
        String timeStr = timer.getFormattedRemainingTime();
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(timeStr);
        int tx = width - tw - 60;
        int ty = 52;

        g.setColor(new Color(0, 0, 0, 160));
        g.drawString(timeStr, tx + 2, ty + 2);

        g.setColor(new Color(255, 255, 255, 230));
        g.drawString(timeStr, tx, ty);
    }
}
