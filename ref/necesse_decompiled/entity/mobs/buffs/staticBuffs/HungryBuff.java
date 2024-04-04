package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class HungryBuff extends Buff {
   public HungryBuff() {
      this.shouldSave = false;
      this.isPassive = true;
      this.canCancel = false;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setMaxModifier(BuffModifiers.HEALTH_REGEN, 0.0F, 10000);
      var1.setMaxModifier(BuffModifiers.COMBAT_HEALTH_REGEN, 0.0F, 10000);
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("bufftooltip", "hungrytip"));
      return var3;
   }
}
