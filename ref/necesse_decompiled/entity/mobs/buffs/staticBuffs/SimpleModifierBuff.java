package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class SimpleModifierBuff extends Buff {
   protected ModifierValue<?>[] modifiers;

   public SimpleModifierBuff(boolean var1, boolean var2, boolean var3, boolean var4, ModifierValue<?>... var5) {
      this.isVisible = var1;
      this.shouldSave = var2;
      this.canCancel = var3;
      this.isImportant = var4;
      this.modifiers = var5;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
      ModifierValue[] var3 = this.modifiers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ModifierValue var6 = var3[var5];
         var6.apply(var1);
      }

   }
}
