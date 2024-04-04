package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import necesse.entity.mobs.Mob;

public interface AttackStageSkipTo<T extends Mob> {
   boolean shouldSkipTo(T var1, boolean var2);
}
