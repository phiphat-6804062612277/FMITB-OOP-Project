package core;

import system.*;
import ui.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;

/**
 * GamePanel provides the 60 FPS Game Loop, Double Buffering,
 * Input dispatching, and delegates rendering to Renderer components.
 */
public class GamePanel extends JPanel implements Runnable, MouseListener, MouseMotionListener, KeyListener {
    public static final int PANEL_WIDTH = 1280;
    public static final int PANEL_HEIGHT = 720;
    public static final int TARGET_FPS = 60;
    public static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

    // Subsystems (Composition)
    private final GameTimer gameTimer;
    private final StaminaManager staminaManager;
    private final BathroomManager bathroomManager;
    private final CameraManager cameraManager;
    private final MonsterAI monsterAI;
    private final StateManager stateManager;

    // Renderers
    private final MenuRenderer menuRenderer;
    private final HowToPlayRenderer howToPlayRenderer;
    private final BathroomRenderer bathroomRenderer;
    private final TabletRenderer tabletRenderer;
    private final EndingRenderer gameOverRenderer;
    private final EndingRenderer gameWinRenderer;

    // Thread & Game Loop
    private Thread gameThread;
    private boolean isRunning;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        // 1. Initialize Subsystems
        this.gameTimer = new GameTimer(300.0f); // 5 minutes
        this.staminaManager = new StaminaManager();
        this.bathroomManager = new BathroomManager(staminaManager);
        this.cameraManager = new CameraManager();
        this.monsterAI = new MonsterAI();
        this.stateManager = new StateManager(gameTimer, staminaManager, bathroomManager, cameraManager, monsterAI);

        // 2. Initialize Renderers
        this.menuRenderer = new MenuRenderer();
        this.howToPlayRenderer = new HowToPlayRenderer();
        this.bathroomRenderer = new BathroomRenderer(bathroomManager, gameTimer, monsterAI);
        this.tabletRenderer = new TabletRenderer(cameraManager, gameTimer, monsterAI);
        this.gameOverRenderer = new EndingRenderer(GameState.GAME_OVER);
        this.gameWinRenderer = new EndingRenderer(GameState.GAME_WIN);

        // 3. Register Listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    public void startGameLoop() {
        if (gameThread == null) {
            isRunning = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void stopGameLoop() {
        isRunning = false;
        if (gameThread != null) {
            try {
                gameThread.join(500);
            } catch (InterruptedException e) {
            }
            gameThread = null;
        }
    }

    @Override
    public void run() {
        long lastLoopTime = System.nanoTime();

        while (isRunning) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            float deltaSeconds = updateLength / 1000000000.0f;

            // Cap delta time to prevent spiraling after pauses
            if (deltaSeconds > 0.05f) {
                deltaSeconds = 0.05f;
            }

            // Update logic
            updateGame(deltaSeconds);

            // Repaint screen
            repaint();

            // Frame rate pacing (60 FPS)
            long frameTime = System.nanoTime() - now;
            long sleepTime = (OPTIMAL_TIME - frameTime) / 1000000;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    private void updateGame(float dt) {
        if (!stateManager.isPlaying()) {
            return;
        }

        boolean isTablet = (stateManager.getCurrentState() == GameState.TABLET);

        // 1. Update Countdown Timer
        gameTimer.update(dt);
        if (gameTimer.isTimeUp()) {
            stateManager.setState(GameState.GAME_WIN);
            SoundManager.getInstance().playSound("win");
            return;
        }

        // 2. Update Bathroom & Stamina
        bathroomManager.update(dt, isTablet);

        // 3. Update Monster AI
        float progress = gameTimer.getProgress();
        boolean killed = monsterAI.update(dt, progress, bathroomManager);
        if (killed) {
            stateManager.setState(GameState.GAME_OVER);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int current = stateManager.getCurrentState();
        switch (current) {
            case GameState.MENU:
                menuRenderer.render(g, getWidth(), getHeight());
                break;
            case GameState.HOW_TO_PLAY:
                howToPlayRenderer.render(g, getWidth(), getHeight());
                break;
            case GameState.BATHROOM:
                bathroomRenderer.render(g, getWidth(), getHeight());
                break;
            case GameState.TABLET:
                tabletRenderer.render(g, getWidth(), getHeight());
                break;
            case GameState.GAME_OVER:
                gameOverRenderer.render(g, getWidth(), getHeight());
                break;
            case GameState.GAME_WIN:
                gameWinRenderer.render(g, getWidth(), getHeight());
                break;
        }
    }

    // --- MOUSE INPUT HANDLING ---
    @Override
    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        int current = stateManager.getCurrentState();

        if (current == GameState.MENU) {
            if (MenuRenderer.START_BTN_BOUNDS.contains(p)) {
                stateManager.startNewGame();
            } else if (MenuRenderer.HOW_TO_PLAY_BTN_BOUNDS.contains(p)) {
                stateManager.setState(GameState.HOW_TO_PLAY);
                SoundManager.getInstance().playSound("cam_switch");
            } else if (MenuRenderer.EXIT_BTN_BOUNDS.contains(p)) {
                System.exit(0);
            }
        } else if (current == GameState.HOW_TO_PLAY) {
            if (HowToPlayRenderer.BACK_BTN_BOUNDS.contains(p)) {
                stateManager.setState(GameState.MENU);
                SoundManager.getInstance().playSound("cam_switch");
            }
        } else if (current == GameState.BATHROOM) {
            // Door click
            if (BathroomRenderer.DOOR_BOUNDS.contains(p)) {
                boolean closed = bathroomManager.toggleDoor();
                if (closed) {
                    SoundManager.getInstance().playSound("door_close");
                } else {
                    SoundManager.getInstance().playSound("door_open");
                }
            }
            // Vent click
            else if (BathroomRenderer.VENT_BOUNDS.contains(p)) {
                boolean closed = bathroomManager.toggleVent();
                if (closed) {
                    SoundManager.getInstance().playSound("vent_close");
                } else {
                    SoundManager.getInstance().playSound("vent_open");
                }
            }
            // Switch to Tablet
            else if (BathroomRenderer.TABLET_BTN_BOUNDS.contains(p)) {
                stateManager.setState(GameState.TABLET);
                SoundManager.getInstance().playSound("tablet_flip");
            }
        } else if (current == GameState.TABLET) {
            // Return to Bathroom
            if (TabletRenderer.RETURN_BTN_BOUNDS.contains(p)) {
                stateManager.setState(GameState.BATHROOM);
                SoundManager.getInstance().playSound("tablet_flip");
            }
            // Toggle Floor
            else if (TabletRenderer.FLOOR_TOGGLE_BTN_BOUNDS.contains(p)) {
                cameraManager.toggleFloor();
                SoundManager.getInstance().playSound("cam_switch");
            }
            // Camera Buttons - conditioned by active floor
            else if (cameraManager.getActiveFloor() == 1) {
                if (TabletRenderer.CAM_1_BTN.contains(p)) {
                    cameraManager.selectCamera(CameraId.C1);
                    SoundManager.getInstance().playSound("cam_switch");
                } else if (TabletRenderer.CAM_2_BTN.contains(p)) {
                    cameraManager.selectCamera(CameraId.C2);
                    SoundManager.getInstance().playSound("cam_switch");
                }
            } else if (cameraManager.getActiveFloor() == 2) {
                if (TabletRenderer.CAM_3_BTN.contains(p)) {
                    cameraManager.selectCamera(CameraId.C3);
                    SoundManager.getInstance().playSound("cam_switch");
                } else if (TabletRenderer.CAM_4_BTN.contains(p)) {
                    cameraManager.selectCamera(CameraId.C4);
                    SoundManager.getInstance().playSound("cam_switch");
                }
            }
        } else if (current == GameState.GAME_OVER || current == GameState.GAME_WIN) {
            if (EndingRenderer.RETRY_BTN_BOUNDS.contains(p)) {
                stateManager.startNewGame();
                SoundManager.getInstance().playSound("cam_switch");
            } else if (EndingRenderer.MENU_BTN_BOUNDS.contains(p)) {
                stateManager.setState(GameState.MENU);
                SoundManager.getInstance().playSound("cam_switch");
            }
        }
    }

    // --- KEYBOARD INPUT HANDLING ---
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        int current = stateManager.getCurrentState();

        if (key == KeyEvent.VK_ESCAPE) {
            if (current == GameState.HOW_TO_PLAY) {
                stateManager.setState(GameState.MENU);
                SoundManager.getInstance().playSound("cam_switch");
            } else if (current == GameState.TABLET) {
                stateManager.setState(GameState.BATHROOM);
            } else if (current == GameState.BATHROOM) {
                stateManager.setState(GameState.MENU);
            }
            return;
        }

        if (current == GameState.BATHROOM) {
            if (key == KeyEvent.VK_E) {
                boolean closed = bathroomManager.toggleDoor();
                if (closed) {
                    SoundManager.getInstance().playSound("door_close");
                } else {
                    SoundManager.getInstance().playSound("door_open");
                }
            } else if (key == KeyEvent.VK_Q) {
                boolean closed = bathroomManager.toggleVent();
                if (closed) {
                    SoundManager.getInstance().playSound("vent_close");
                } else {
                    SoundManager.getInstance().playSound("vent_open");
                }
            } else if (key == KeyEvent.VK_SPACE) {
                stateManager.setState(GameState.TABLET);
                SoundManager.getInstance().playSound("tablet_flip");
            }
        } else if (current == GameState.TABLET) {
            if (key == KeyEvent.VK_SPACE) {
                stateManager.setState(GameState.BATHROOM);
                SoundManager.getInstance().playSound("tablet_flip");
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        menuRenderer.setMousePos(e.getPoint());
        howToPlayRenderer.setMousePos(e.getPoint());
        gameOverRenderer.setMousePos(e.getPoint());
        gameWinRenderer.setMousePos(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        menuRenderer.setMousePos(e.getPoint());
        howToPlayRenderer.setMousePos(e.getPoint());
        gameOverRenderer.setMousePos(e.getPoint());
        gameWinRenderer.setMousePos(e.getPoint());
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
