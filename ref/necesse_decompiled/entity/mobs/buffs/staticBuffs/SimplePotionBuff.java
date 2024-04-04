package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;

public class SimplePotionBuff extends SimpleModifierBuff {
   public SimplePotionBuff(boolean var1, ModifierValue<?>... var2) {
      super(true, true, true, false, var2);
      this.isImportant = var1;
   }

   public SimplePotionBuff(ModifierValue<?>... var1) {
      this(false, var1);
   }

   public void updateLocalDisplayName() {
      this.displayName = new LocalMessage("item", this.getStringID());
   }

   public boolean isPotionBuff() {
      return true;
   }
}
