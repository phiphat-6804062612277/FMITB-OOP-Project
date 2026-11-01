package entity;

import system.BathroomManager;
import system.SoundManager;

/**
 * Tralalero Tralala:
 * The second monster that climbs through the ventilation ducts
 * and attacks via the Bathroom Air Vent.
 * Blocked only by keeping the air vent closed.
 */
public class TralalaMonster extends Monster {

    public TralalaMonster() {
        super("Tralalero Tralala", 1, 14.0f, 3.8f, 2.5f);
    }

    @Override
    public boolean updateAI(float dt, float gameProgress, BathroomManager bathroom) {
        if (hasKilledPlayer) {
            return true;
        }

        float currentInterval = Math.max(4.5f, baseMoveInterval * (1.0f - gameProgress * 0.50f));

        if (!isAtAttackPoint) {
            moveTimer -= dt;
            if (moveTimer <= 0.0f) {
                moveTimer = currentInterval * (0.8f + (float) Math.random() * 0.4f);
                double roll = Math.random();
                if (roll < 0.65) {
                    stage = 1;
                    isAtAttackPoint = true;
                    defenseHoldTimer = 0.0f;
                    attackCountdown = baseAttackReactionTime;
                    SoundManager.getInstance().playSound("door_creak");
                }
            }
        } else {
            // Monster is at the air vent!
            if (bathroom.isVentClosed()) {
                defenseHoldTimer += dt;

                // Held long enough: drive monster away!
                if (defenseHoldTimer >= requiredHoldTime) {
                    onBlocked(bathroom);
                    return false;
                }
            } else {
                // Vent is OPEN!
                defenseHoldTimer = 0.0f;
                attackCountdown -= dt;
                if (attackCountdown <= 0.0f) {
                    hasKilledPlayer = true;
                    SoundManager.getInstance().playSound("jumpscare");
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onBlocked(BathroomManager bathroom) {
        SoundManager.getInstance().playSound("monster_banging");
        isAtAttackPoint = false;
        defenseHoldTimer = 0.0f;
        // Retreat back to CAM 1
        stage = 0;
        moveTimer = baseMoveInterval * 0.9f;
        attackCountdown = baseAttackReactionTime;
    }
}
