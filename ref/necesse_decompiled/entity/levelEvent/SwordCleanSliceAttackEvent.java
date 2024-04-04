package necesse.entity.levelEvent;

import necesse.entity.mobs.AttackAnimMob;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;

public abstract class SwordCleanSliceAttackEvent extends ShowAttackTickEvent {
   public SwordCleanSliceAttackEvent() {
   }

   public SwordCleanSliceAttackEvent(AttackAnimMob var1, int var2, int var3, SwordToolItem var4) {
      super(var1, var2, var3, var4);
   }

   public void tick(AttackAnimMob var1, float var2) {
      switch (this.attackMob.dir) {
         case 0:
            this.tick(-90.0F, var2);
            break;
         case 1:
            this.tick(0.0F, var2);
            break;
         case 2:
            this.tick(90.0F, var2);
            break;
         case 3:
            this.tick(180.0F, var2);
      }

   }

   public abstract void tick(float var1, float var2);
}
