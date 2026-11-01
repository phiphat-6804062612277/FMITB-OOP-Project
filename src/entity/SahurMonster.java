package entity;

import system.BathroomManager;
import system.SoundManager;

/**
 * Tung Tung Tung Sahur:
 * Patrols through floors 1 and 2, eventually reaching the Bathroom Door.
 * Blocked only by keeping the bathroom door closed.
 */
public class SahurMonster extends Monster {

    public SahurMonster() {
        super("Tung Tung Tung Sahur", 4, 12.0f, 3.8f, 2.5f);
    }

    @Override
    public boolean updateAI(float dt, float gameProgress, BathroomManager bathroom) {
        if (hasKilledPlayer) {
            return true;
        }

        // Difficulty scaling: interval decreases as night progresses
        float currentInterval = Math.max(4.0f, baseMoveInterval * (1.0f - gameProgress * 0.55f));

        if (!isAtAttackPoint) {
            moveTimer -= dt;
            if (moveTimer <= 0.0f) {
                moveTimer = currentInterval * (0.8f + (float) Math.random() * 0.4f);
                // 70% chance to advance, 10% chance to retreat, 20% wait
                double roll = Math.random();
                if (roll < 0.70) {
                    stage++;
                    if (stage >= maxStage) {
                        stage = maxStage;
                        isAtAttackPoint = true;
                        defenseHoldTimer = 0.0f;
                        attackCountdown = baseAttackReactionTime;
                    }
                } else if (roll > 0.90 && stage > 0) {
                    stage--;
                }
            }
        } else {
            // Monster is standing at the door!
            if (bathroom.isDoorClosed()) {
                defenseHoldTimer += dt;

                // Held long enough: drive monster away!
                if (defenseHoldTimer >= requiredHoldTime) {
                    onBlocked(bathroom);
                    return false;
                }
            } else {
                // Door is OPEN!
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
        // Retreat back to Floor 1 (CAM 1 or CAM 2)
        if (Math.random() < 0.5) {
            stage = 1;
        } else {
            stage = 0;
        }
        moveTimer = baseMoveInterval * 0.8f;
        attackCountdown = baseAttackReactionTime;
    }
}
