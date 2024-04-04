package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.gameTooltips.ListGameTooltips;

public class DebugInvisibilityBuff extends Buff {
   public DebugInvisibilityBuff() {
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      var1.setModifier(BuffModifiers.INVISIBILITY, true);
      var1.setModifier(BuffModifiers.TARGET_RANGE, -1.0F);
   }

   public int getStackSize() {
      return 1;
   }

   public boolean overridesStackDuration() {
      return false;
   }

   public ListGameTooltips getTooltip(ActiveBuff var1, GameBlackboard var2) {
      ListGameTooltips var3 = super.getTooltip(var1, var2);
      var3.add("Used for testing purposes");
      return var3;
   }

   public void updateLocalDisplayName() {
      this.displayName = new StaticMessage("Debug invisibility");
   }
}
