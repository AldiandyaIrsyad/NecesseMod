package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class LifelineCooldownBuff extends Buff {
   public LifelineCooldownBuff() {
      this.canCancel = false;
      this.isImportant = true;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      return super.getTooltip(var1, var2);
   }
}
