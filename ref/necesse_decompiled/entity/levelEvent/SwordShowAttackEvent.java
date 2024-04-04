package necesse.entity.levelEvent;

import necesse.engine.util.GameMath;
import necesse.entity.mobs.AttackAnimMob;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;

public abstract class SwordShowAttackEvent extends LevelEvent {
   protected float lastAngleTick;
   protected float anglePerTick;
   protected boolean firstSetLastAngleTick;
   protected int aimX;
   protected int aimY;
   protected int attackSeed;
   protected AttackAnimMob attackMob;
   protected SwordToolItem sword;

   public SwordShowAttackEvent() {
   }

   public SwordShowAttackEvent(AttackAnimMob var1, int var2, int var3, int var4, float var5, SwordToolItem var6) {
      super(false);
      this.attackMob = var1;
      this.aimX = var2 - var1.getX();
      this.aimY = var3 - var1.getY();
      this.attackSeed = var4;
      this.anglePerTick = var5;
      this.sword = var6;
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
            if (!this.firstSetLastAngleTick) {
               this.lastAngleTick = GameMath.fixAngle((Float)this.sword.getSwingDirection(this.attackMob).apply(0.0F));
               this.firstSetLastAngleTick = true;
            }

            float var3 = GameMath.fixAngle((Float)this.sword.getSwingDirection(this.attackMob).apply(var2));
            float var4 = GameMath.getAngleDifference(this.lastAngleTick, var3);
            float var5 = Math.signum(var4);
            float var6 = Math.abs(var4);
            if (var6 >= this.anglePerTick) {
               for(float var7 = 0.0F; var7 <= var6; var7 += this.anglePerTick) {
                  this.tick(this.lastAngleTick + var7 * var5);
               }

               this.lastAngleTick = var3;
            }

         } else {
            this.over();
         }
      }
   }

   public abstract void tick(float var1);
}
