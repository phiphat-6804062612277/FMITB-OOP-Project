package system;

import entity.Monster;
import entity.SahurMonster;
import entity.TralalaMonster;

/**
 * MonsterAI orchestrates all monster instances using a polymorphic Monster[] array.
 */
public class MonsterAI {
    private final Monster[] monsters;
    private final SahurMonster sahur;
    private final TralalaMonster tralala;

    public MonsterAI() {
        this.sahur = new SahurMonster();
        this.tralala = new TralalaMonster();
        this.monsters = new Monster[2];
        this.monsters[0] = sahur;
        this.monsters[1] = tralala;
    }

    public void reset() {
        for (int i = 0; i < monsters.length; i++) {
            monsters[i].reset();
        }
    }

    /**
     * Updates all monsters polymorphically.
     */
    public boolean update(float dt, float gameProgress, BathroomManager bathroom) {
        for (int i = 0; i < monsters.length; i++) {
            boolean killed = monsters[i].updateAI(dt, gameProgress, bathroom);
            if (killed) {
                return true;
            }
        }
        return false;
    }

    public SahurMonster getSahur() {
        return sahur;
    }

    public TralalaMonster getTralala() {
        return tralala;
    }
}
