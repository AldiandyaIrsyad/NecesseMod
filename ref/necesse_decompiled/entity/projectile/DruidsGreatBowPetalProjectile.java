package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class DruidsGreatBowPetalProjectile extends Projectile {
   public int splitsRemaining = 3;
   public int remainingDistance;

   public DruidsGreatBowPetalProjectile() {
   }

   public DruidsGreatBowPetalProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setTarget(var5, var6);
      this.speed = var7;
      this.distance = Math.min(var8, 100);
      this.remainingDistance = var8;
      this.setDamage(var9);
      this.knockback = var10;
   }

   public DruidsGreatBowPetalProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, int var9) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setAngle(var5);
      this.speed = var6;
      this.distance = Math.min(var7, 100);
      this.remainingDistance = var7;
      this.setDamage(var8);
      this.knockback = var9;
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.piercing = 0;
      this.setWidth(6.0F, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.remainingDistance);
      var1.putNextByteUnsigned(this.splitsRemaining);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.remainingDistance = var1.getNextInt();
      this.splitsRemaining = var1.getNextByteUnsigned();
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         if (this.splitsRemaining > 0) {
            --this.splitsRemaining;
            if (this.traveledDistance >= (float)this.distance || var1 != null) {
               int var5 = this.remainingDistance - this.distance;
               float var6 = this.getAngle();
               DruidsGreatBowPetalProjectile var7 = new DruidsGreatBowPetalProjectile(this.getLevel(), this.getOwner(), var3, var4, var6 - 10.0F, this.speed, this.distance, this.getDamage().modFinalMultiplier(0.66F), this.knockback);
               if (this.modifier != null) {
                  this.modifier.initChildProjectile(var7, 0.5F, 2);
               }

               var7.distance = this.splitsRemaining > 0 ? Math.min(var5, 200) : var5;
               var7.remainingDistance = var5;
               var7.splitsRemaining = this.splitsRemaining;
               if (var1 != null) {
                  var7.startHitCooldown(var1);
               }

               this.getLevel().entityManager.projectiles.add(var7);
               DruidsGreatBowPetalProjectile var8 = new DruidsGreatBowPetalProjectile(this.getLevel(), this.getOwner(), var3, var4, var6 + 10.0F, this.speed, this.distance, this.getDamage().modFinalMultiplier(0.66F), this.knockback);
               if (this.modifier != null) {
                  this.modifier.initChildProjectile(var8, 0.5F, 2);
               }

               var8.distance = this.splitsRemaining > 0 ? Math.min(var5, 200) : var5;
               var8.remainingDistance = var5;
               var8.splitsRemaining = this.splitsRemaining;
               if (var1 != null) {
                  var8.startHitCooldown(var1);
               }

               this.getLevel().entityManager.projectiles.add(var8);
            }
         }

      }
   }

   public Color getParticleColor() {
      return new Color(64, 101, 70);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(64, 101, 70), 12.0F, 200, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y);
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), 0);
      }
   }
}
