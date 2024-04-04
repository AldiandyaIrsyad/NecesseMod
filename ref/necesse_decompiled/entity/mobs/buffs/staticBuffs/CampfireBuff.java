package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class CampfireBuff extends VicinityBuff {
   public CampfireBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.MOB_SPAWN_RATE, -0.5F);
      var1.setModifier(BuffModifiers.HEALTH_REGEN_FLAT, 2.0F);
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add(Localization.translate("bufftooltip", "campfiretip1"));
      var3.add(Localization.translate("bufftooltip", "campfiretip2"));
      return var3;
   }
}
