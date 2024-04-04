package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class WebPotionBuff extends Buff {
   public WebPotionBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public void onHasAttacked(ActiveBuff var1, MobWasHitEvent var2) {
      super.onHasAttacked(var1, var2);
      if (!var2.wasPrevented) {
         var2.target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.WEB_POTION_SLOW, var2.target, 5.0F, var2.attacker), var2.target.isServer());
      }

   }

   public void updateLocalDisplayName() {
      this.displayName = new LocalMessage("item", this.getStringID());
   }
}
