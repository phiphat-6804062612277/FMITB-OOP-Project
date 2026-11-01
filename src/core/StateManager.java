package core;

import system.*;

/**
 * StateManager controls transitions between different game screens/states
 * and coordinates data resets across subsystems.
 */
public class StateManager {
    private int currentState;
    private final GameTimer timer;
    private final StaminaManager stamina;
    private final BathroomManager bathroom;
    private final CameraManager camera;
    private final MonsterAI monsterAI;

    public StateManager(GameTimer timer, StaminaManager stamina, BathroomManager bathroom,
                        CameraManager camera, MonsterAI monsterAI) {
        this.timer = timer;
        this.stamina = stamina;
        this.bathroom = bathroom;
        this.camera = camera;
        this.monsterAI = monsterAI;
        this.currentState = GameState.MENU;
    }

    public void startNewGame() {
        timer.reset();
        timer.start();
        stamina.reset();
        bathroom.reset();
        camera.reset();
        monsterAI.reset();
        setState(GameState.BATHROOM);
        SoundManager.getInstance().playSound("tablet_flip");
        SoundManager.getInstance().playLoop("ambient");
    }

    public void setState(int newState) {
        this.currentState = newState;
        if (newState == GameState.GAME_OVER || newState == GameState.GAME_WIN
                || newState == GameState.MENU || newState == GameState.HOW_TO_PLAY) {
            timer.stop();
            SoundManager.getInstance().stopSound("ambient");
        }
    }

    public int getCurrentState() {
        return currentState;
    }

    public boolean isPlaying() {
        return currentState == GameState.BATHROOM || currentState == GameState.TABLET;
    }
}
