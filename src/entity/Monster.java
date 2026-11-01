package entity;

import system.BathroomManager;

/**
 * Abstract Monster class demonstrating OOP principles:
 * - Abstraction: Abstract updateAI and onBlocked methods
 * - Encapsulation: Protected state fields with public getter methods
 * - Polymorphism: Subclasses implement unique movement and attack logic
 */
public abstract class Monster {
    protected final String name;

    protected int stage;              // 0: Far, 1: Medium, 2: Close, 3: Very Close, 4: At Attack Point
    protected int maxStage;
    protected float moveTimer;
    protected float baseMoveInterval; // Base seconds between move attempts
    protected float attackCountdown;  // Seconds player has to react when monster is at attack point
    protected float baseAttackReactionTime;
    protected float defenseHoldTimer; // Seconds player has held defense while monster is at attack point
    protected float requiredHoldTime; // Seconds player must hold defense to drive monster away

    protected boolean isAtAttackPoint;
    protected boolean hasKilledPlayer;

    public Monster(String name, int maxStage, float baseMoveInterval, float baseAttackReactionTime, float requiredHoldTime) {
        this.name = name;
        this.maxStage = maxStage;
        this.baseMoveInterval = baseMoveInterval;
        this.baseAttackReactionTime = baseAttackReactionTime;
        this.requiredHoldTime = requiredHoldTime;
        reset();
    }

    public void reset() {
        this.stage = 0;
        this.moveTimer = baseMoveInterval * (0.8f + (float) Math.random() * 0.4f);
        this.isAtAttackPoint = false;
        this.hasKilledPlayer = false;
        this.attackCountdown = baseAttackReactionTime;
        this.defenseHoldTimer = 0.0f;
    }

    /**
     * Updates monster behavior and difficulty progression.
     */
    public abstract boolean updateAI(float dt, float gameProgress, BathroomManager bathroom);

    /**
     * Called when the monster is blocked by player's defense.
     */
    public abstract void onBlocked(BathroomManager bathroom);

    public String getName() {
        return name;
    }

    public int getStage() {
        return stage;
    }

    public boolean isAtAttackPoint() {
        return isAtAttackPoint;
    }
}
