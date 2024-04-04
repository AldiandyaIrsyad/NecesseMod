package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class ShieldActiveBuff extends Buff {
   public ShieldActiveBuff() {
      this.shouldSave = false;
      this.isVisible = false;
   }

   public boolean shouldNetworkSync() {
      return false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      float var3 = var1.getGndData().getFloat("minSlow", -1.0F);
      if (var3 != -1.0F) {
         var1.setMinModifier(BuffModifiers.SLOW, var3, 1000000);
      }

   }
}
