package system;

/**
 * BathroomManager handles the state of the bathroom defenses:
 * - Door (blocks Tung Tung Tung Sahur)
 * - Vent (blocks Tralalero Tralala)
 * - Stamina integration and exhaustion penalties.
 */
public class BathroomManager {
    private boolean doorClosed;
    private boolean ventClosed;
    private final StaminaManager staminaManager;

    public BathroomManager(StaminaManager staminaManager) {
        this.staminaManager = staminaManager;
        reset();
    }

    public void reset() {
        this.doorClosed = false;
        this.ventClosed = false;
    }

    public void update(float deltaSeconds, boolean isTabletOpen) {
        // While looking at tablet, player cannot close or hold door/vent
        if (isTabletOpen) {
            doorClosed = false;
            ventClosed = false;
        }

        int activeClosures = 0;
        if (doorClosed) {
            activeClosures++;
        }
        if (ventClosed) {
            activeClosures++;
        }

        boolean allowClosure = !isTabletOpen;
        boolean justExhausted = staminaManager.update(deltaSeconds, activeClosures, allowClosure);

        // If player just exhausted, force open door and vent
        if (justExhausted) {
            doorClosed = false;
            ventClosed = false;
        }
    }

    public boolean toggleDoor() {
        if (doorClosed) {
            doorClosed = false;
            return false;
        } else {
            if (staminaManager.canClose()) {
                doorClosed = true;
                return true;
            }
            return false;
        }
    }

    public boolean toggleVent() {
        if (ventClosed) {
            ventClosed = false;
            return false;
        } else {
            if (staminaManager.canClose()) {
                ventClosed = true;
                return true;
            }
            return false;
        }
    }

    public boolean isDoorClosed() {
        return doorClosed;
    }

    public boolean isVentClosed() {
        return ventClosed;
    }

    public StaminaManager getStaminaManager() {
        return staminaManager;
    }
}
