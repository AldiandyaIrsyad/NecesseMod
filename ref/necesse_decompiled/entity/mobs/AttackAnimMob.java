package necesse.entity.mobs;

import java.awt.geom.Point2D;
import necesse.engine.network.packet.PacketMobAttack;
import necesse.engine.util.GameMath;

public class AttackAnimMob extends Mob {
   public int attackAnimTime = 200;
   public Point2D.Float attackDir;
   public int attackSeed;

   public AttackAnimMob(int var1) {
      super(var1);
   }

   public void clientTick() {
      super.clientTick();
      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.isAttacking) {
         this.getAttackAnimProgress();
      }

   }

   public float getAttackAnimProgress() {
      float var1 = (float)(this.getWorldEntity().getTime() - this.attackTime) / (float)this.attackAnimTime;
      if (var1 >= 1.0F) {
         this.isAttacking = false;
      }

      return Math.min(1.0F, var1);
   }

   public void attack(int var1, int var2, boolean var3) {
      super.attack(var1, var2, var3);
      this.attackDir = GameMath.normalize((float)(var1 - this.getX()), (float)(var2 - this.getY()));
      this.isAttacking = true;
      if (this.isServer()) {
         this.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobAttack(this, var1, var2), this);
      }

   }

   public final void showAttack(int var1, int var2, boolean var3) {
      this.showAttack(var1, var2, 0, var3);
   }

   public void showAttack(int var1, int var2, int var3, boolean var4) {
      this.attackDir = GameMath.normalize((float)(var1 - this.getX()), (float)(var2 - this.getY()));
      if (var4) {
         this.setFacingDir((float)(var1 - this.getX()), (float)(var2 - this.getY()));
      } else if (var1 > this.getX()) {
         this.dir = 1;
      } else {
         this.dir = 3;
      }

      this.isAttacking = true;
      this.attackSeed = var3;
      this.attackTime = this.getWorldEntity().getTime();
   }

   public int getStartAttackHeight() {
      return 14;
   }

   public int getCurrentAttackHeight() {
      int var1 = this.getStartAttackHeight();
      Mob var2 = this.getMount();
      if (var2 != null) {
         var1 -= var2.getBobbing(var2.getX(), var2.getY());
         if (var2.getLevel() != null) {
            var1 -= var2.getLevel().getTile(var2.getX() / 32, var2.getY() / 32).getMobSinkingAmount(var2);
         }
      } else {
         var1 -= this.getBobbing(this.getX(), this.getY());
         if (this.getLevel() != null) {
            var1 -= this.getLevel().getTile(this.getX() / 32, this.getY() / 32).getMobSinkingAmount(this);
         }
      }

      return var1;
   }

   public int getCurrentAttackDrawYOffset() {
      Mob var1 = this.getMount();
      return var1 != null ? -this.getCurrentAttackHeight() + var1.getRiderDrawYOffset() : -this.getCurrentAttackHeight();
   }

   public int getCurrentAttackDrawXOffset() {
      Mob var1 = this.getMount();
      return var1 != null ? var1.getRiderDrawXOffset() : this.getRiderDrawXOffset();
   }
}
