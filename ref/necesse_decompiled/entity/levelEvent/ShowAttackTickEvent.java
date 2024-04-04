package necesse.entity.levelEvent;

import necesse.entity.mobs.AttackAnimMob;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;

public abstract class ShowAttackTickEvent extends LevelEvent {
   protected int attackSeed;
   protected int totalTicksPerAttack;
   protected float lastTicksAttackProgress;
   protected AttackAnimMob attackMob;
   protected SwordToolItem sword;

   public ShowAttackTickEvent() {
   }

   public ShowAttackTickEvent(AttackAnimMob var1, int var2, int var3, SwordToolItem var4) {
      super(false);
      this.attackMob = var1;
      this.attackSeed = var2;
      this.totalTicksPerAttack = var3;
      this.sword = var4;
   }

   public void init() {
      super.init();
      if (this.attackMob == null) {
         this.over();
      }

   }

   public void tickMovement(float var1) {
      super.tickMovement(var1);
      if (!this.isOver()) {
         if (this.attackMob.isAttacking && this.attackMob.attackSeed == this.attackSeed) {
            float var2 = this.attackMob.getAttackAnimProgress();
            float var3 = 1.0F / (float)this.totalTicksPerAttack;

            while(true) {
               float var4 = var2 - this.lastTicksAttackProgress;
               if (!(var4 >= var3)) {
                  return;
               }

               this.tick(this.attackMob, this.lastTicksAttackProgress);
               this.lastTicksAttackProgress += var3;
            }
         } else {
            this.over();
         }
      }
   }

   public abstract void tick(AttackAnimMob var1, float var2);
}
