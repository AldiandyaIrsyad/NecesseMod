package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public abstract class ToggleActiveBuff extends TrinketBuff {
   public ToggleActiveBuff() {
   }

   public void serverTick(ActiveBuff var1) {
      this.updateActive(var1);
   }

   public void clientTick(ActiveBuff var1) {
      this.updateActive(var1);
   }

   public boolean isActive(ActiveBuff var1) {
      return var1 != null && var1.getGndData().getBoolean("active");
   }

   protected final void updateActive(ActiveBuff var1) {
      boolean var2 = var1.getGndData().getBoolean("active");
      boolean var3 = !this.isNextActive(var1);
      if (var2 != var3) {
         var1.resetDefaultModifiers();
         var1.getGndData().setBoolean("active", var3);
         this.updateActive(var1, var3);
         var1.forceManagerUpdate();
      }

   }

   protected abstract void updateActive(ActiveBuff var1, boolean var2);

   protected abstract boolean isNextActive(ActiveBuff var1);

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      this.updateActive(var1);
   }
}
