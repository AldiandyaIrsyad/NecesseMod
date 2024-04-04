package necesse.entity.mobs.ai.behaviourTree.util;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;

public class SummonTargetFinderDistance<T extends Mob> extends TargetFinderDistance<T> {
   public SummonTargetFinderDistance(int var1) {
      super(var1);
   }

   public float getSearchDistanceMod(T var1, Mob var2) {
      float var3 = (Float)var1.buffManager.getModifier(BuffModifiers.CHASER_RANGE);
      float var4 = var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.TARGET_RANGE);
      PlayerMob var5 = var1.getFollowingPlayer();
      float var6 = var5 == null ? 1.0F : (Float)var5.buffManager.getModifier(BuffModifiers.SUMMONS_TARGET_RANGE);
      return var4 * var6 * var3;
   }
}
