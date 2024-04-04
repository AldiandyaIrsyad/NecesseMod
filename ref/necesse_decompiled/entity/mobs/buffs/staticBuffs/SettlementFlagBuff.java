package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class SettlementFlagBuff extends VicinityBuff {
   public SettlementFlagBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setMaxModifier(BuffModifiers.MOB_SPAWN_RATE, 0.2F);
      var1.setMaxModifier(BuffModifiers.MOB_SPAWN_CAP, 0.5F);
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("bufftooltip", "settlementflagtip"));
      return var3;
   }
}
