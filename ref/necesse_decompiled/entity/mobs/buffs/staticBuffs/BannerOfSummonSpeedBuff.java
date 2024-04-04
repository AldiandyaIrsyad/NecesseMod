package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class BannerOfSummonSpeedBuff extends VicinityBuff {
   public BannerOfSummonSpeedBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.SUMMONS_SPEED, 0.75F);
   }

   public void updateLocalDisplayName() {
      this.displayName = new LocalMessage("item", this.getStringID());
   }
}
