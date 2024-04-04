package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class ManaPotionFatigueBuff extends Buff {
   public ManaPotionFatigueBuff() {
      this.canCancel = false;
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      Buff var3 = BuffRegistry.Debuffs.MANA_EXHAUSTION;
      if (var1.owner.buffManager.hasBuff(var3)) {
         var1.owner.isManaExhausted = false;
         var1.owner.buffManager.removeBuff(var3, false);
      }

   }
}
