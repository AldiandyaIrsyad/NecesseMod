package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;

public class ShownCooldownBuff extends Buff {
   protected int maxStacks;
   protected boolean showsFirstStackDurationText;

   public ShownCooldownBuff(int var1, boolean var2) {
      this.maxStacks = var1;
      this.showsFirstStackDurationText = var2;
      this.canCancel = false;
      this.isImportant = true;
   }

   public ShownCooldownBuff() {
      this(1, false);
   }

   public int getStackSize() {
      return this.maxStacks;
   }

   public boolean showsFirstStackDurationText() {
      return this.showsFirstStackDurationText;
   }

   public void init(ActiveBuff var1, BuffEventSubscriber var2) {
   }
}
