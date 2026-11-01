package system;

/**
 * StaminaManager handles player stamina (0 - 100).
 * Closing doors or vents drains 10 units/sec each.
 * Reaching 0 stamina causes exhaustion (cooldown: 3.5 seconds).
 * Regenerates when idle or while viewing the tablet.
 */
public class StaminaManager {
    public static final float MAX_STAMINA = 100.0f;
    public static final float DRAIN_RATE_PER_CLOSURE = 10.0f;
    public static final float REGEN_RATE = 15.0f;
    public static final float EXHAUSTION_COOLDOWN = 3.5f;

    private float stamina;
    private boolean isExhausted;
    private float exhaustionTimer;

    public StaminaManager() {
        reset();
    }

    public void reset() {
        this.stamina = MAX_STAMINA;
        this.isExhausted = false;
        this.exhaustionTimer = 0.0f;
    }

    public boolean update(float deltaSeconds, int activeClosures, boolean allowClosure) {
        boolean justExhausted = false;

        if (isExhausted) {
            exhaustionTimer -= deltaSeconds;
            if (exhaustionTimer <= 0.0f) {
                isExhausted = false;
                exhaustionTimer = 0.0f;
            }
            stamina = Math.min(MAX_STAMINA, stamina + (REGEN_RATE * 0.75f) * deltaSeconds);
            return false;
        }

        if (allowClosure && activeClosures > 0) {
            float totalDrain = activeClosures * DRAIN_RATE_PER_CLOSURE * deltaSeconds;
            stamina -= totalDrain;

            if (stamina <= 0.0f) {
                stamina = 0.0f;
                isExhausted = true;
                exhaustionTimer = EXHAUSTION_COOLDOWN;
                justExhausted = true;
            }
        } else {
            stamina = Math.min(MAX_STAMINA, stamina + REGEN_RATE * deltaSeconds);
        }

        return justExhausted;
    }

    public boolean canClose() {
        return !isExhausted && stamina > 0.0f;
    }

    public float getStaminaPercentage() {
        return stamina / MAX_STAMINA;
    }

    public boolean isExhausted() {
        return isExhausted;
    }
}
