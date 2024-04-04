package necesse.entity.mobs;

import java.awt.Rectangle;
import java.util.HashSet;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public class ProjectileHitboxMob extends Mob {
   public Projectile projectile;

   public ProjectileHitboxMob() {
      super(1);
      this.setArmor(0);
      this.setSpeed(0.0F);
      this.setFriction(1000.0F);
      this.setKnockbackModifier(0.0F);
      this.shouldSave = false;
      this.isStatic = true;
      this.selectBox = new Rectangle();
      this.collision = new Rectangle();
   }

   public boolean shouldSendSpawnPacket() {
      return false;
   }

   public void tickMovement(float var1) {
      if (this.projectile != null) {
         this.x = this.projectile.x;
         this.y = this.projectile.y;
      }

      super.tickMovement(var1);
   }

   protected void checkCollision() {
   }

   protected void calcAcceleration(float var1, float var2, float var3, float var4, float var5) {
   }

   protected void tickCollisionMovement(float var1, Mob var2) {
   }

   public boolean canHitThroughCollision() {
      return true;
   }

   public CollisionFilter getLevelCollisionFilter() {
      return null;
   }

   public void clientTick() {
      super.clientTick();
      if (this.projectile == null || this.projectile.removed()) {
         this.remove();
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.projectile == null || this.projectile.removed()) {
         this.remove();
      }

   }

   public Rectangle getHitBox(int var1, int var2) {
      return this.projectile != null ? this.projectile.getHitbox() : new Rectangle();
   }

   public boolean canBeTargeted(Mob var1, NetworkClient var2) {
      return super.canBeTargeted(var1, var2);
   }

   public boolean canBeHit(Attacker var1) {
      if (this.projectile != null) {
         if (var1 == this.projectile) {
            return false;
         }

         Mob var2 = this.projectile.getOwner();
         if (var2 != null) {
            return var2.canBeHit(var1);
         }
      }

      return super.canBeHit(var1);
   }

   public boolean canBePushed(Mob var1) {
      return false;
   }

   public boolean canPushMob(Mob var1) {
      return false;
   }

   public boolean isHealthBarVisible() {
      return false;
   }

   public boolean canTakeDamage() {
      return true;
   }

   public boolean countDamageDealt() {
      return false;
   }

   protected void playHitSound() {
   }

   protected void playDeathSound() {
   }

   public void spawnDamageText(int var1, int var2, boolean var3) {
   }

   protected void onDeath(Attacker var1, HashSet<Attacker> var2) {
      super.onDeath(var1, var2);
      if (this.projectile != null && !this.projectile.removed()) {
         this.projectile.remove();
         if (var1 instanceof Projectile) {
            Projectile var3 = (Projectile)var1;
            if (var3.hasHitbox) {
               Mob var4 = var3.getHitboxMob();
               if (var4 != null && !var4.removed() && this.projectile.canHit(var4)) {
                  this.projectile.onHit(var4, (LevelObjectHit)null, var3.x, var3.y, false, (ServerClient)null);
               }
            }
         }
      }

   }
}
