package necesse.entity.levelEvent;

import java.awt.geom.Point2D;
import java.util.Collection;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.AttackAnimMob;
import necesse.level.maps.regionSystem.RegionPosition;

public abstract class GlaiveShowAttackEvent extends LevelEvent {
   protected float lastAngleTick;
   protected float anglePerTick;
   protected int aimX;
   protected int aimY;
   protected int attackSeed;
   protected AttackAnimMob attackMob;

   public GlaiveShowAttackEvent(AttackAnimMob var1, int var2, int var3, int var4, float var5) {
      super(false);
      this.attackMob = var1;
      this.aimX = var2 - var1.getX();
      this.aimY = var3 - var1.getY();
      this.attackSeed = var4;
      this.anglePerTick = var5;
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
            float var3 = var2 * 360.0F;
            if (var3 >= this.lastAngleTick + this.anglePerTick) {
               for(float var4 = this.lastAngleTick + this.anglePerTick; var4 <= var3; var4 += this.anglePerTick) {
                  this.tick(var4);
                  this.lastAngleTick = var4;
               }
            }

         } else {
            this.over();
         }
      }
   }

   public abstract void tick(float var1);

   public Point2D.Float getAngleDir(float var1) {
      return this.aimX < 0 ? GameMath.getAngleDir(-var1 - 110.0F) : GameMath.getAngleDir(var1 + 110.0F);
   }

   public Collection<RegionPosition> getRegionPositions() {
      return this.attackMob != null ? this.attackMob.getRegionPositions() : super.getRegionPositions();
   }
}
