package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidWizardCloneProjectile extends Projectile {
   protected long spawnTime;
   protected boolean moving;

   public VoidWizardCloneProjectile() {
   }

   public VoidWizardCloneProjectile(Level var1, float var2, float var3, Mob var4, GameDamage var5) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.setDir((float)(GameRandom.globalRandom.nextGaussian() * 10.0), (float)(GameRandom.globalRandom.nextGaussian() * 10.0));
      this.speed = 0.0F;
      this.setDamage(var5);
      this.knockback = 0;
      this.setDistance(100000);
      this.setOwner(var4);
      this.setAngle(this.getAngle() + (GameRandom.globalRandom.nextFloat() - 0.5F) * 40.0F);
      this.spawnTime = this.getWorldEntity().getTime();
      this.moving = false;
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextBoolean(this.moving);
      var1.putNextLong(this.spawnTime);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.moving = var1.getNextBoolean();
      this.spawnTime = var1.getNextLong();
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.setWidth(10.0F);
      this.bouncing = 1000;
      this.piercing = 1;
      this.givesLight = true;
      this.trailOffset = 0.0F;
      if (this.moving) {
         this.speed = 100.0F;
      } else {
         this.speed = 0.0F;
      }

   }

   protected CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      });
   }

   public Color getParticleColor() {
      return VoidWizard.getWizardProjectileColor(this.getOwner());
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), (Color)null, 16.0F, 300, this.getHeight()) {
         public Color getColor() {
            return VoidWizard.getWizardProjectileColor(VoidWizardCloneProjectile.this.getOwner());
         }
      };
   }

   public void clientTick() {
      super.clientTick();
      if (!this.moving) {
         long var1 = this.getWorldEntity().getTime() - this.spawnTime;
         if (var1 >= 2000L) {
            this.speed = 100.0F;
            this.moving = true;
         }
      }

   }

   public void serverTick() {
      super.serverTick();
      if (!this.moving) {
         long var1 = this.getWorldEntity().getTime() - this.spawnTime;
         if (var1 >= 2000L) {
            this.speed = 100.0F;
            this.moving = true;
         }
      }

      Mob var3 = this.getOwner();
      if (var3 != null && var3.removed()) {
         this.remove();
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         float var10 = (float)(this.getWorldEntity().getTime() - this.spawnTime);
         float var11 = var10 / 10.0F;
         float var12 = this.getHeight();
         int var13;
         if (!this.moving) {
            double var14 = GameMath.limit((double)var10 / 2000.0, 0.0, 1.0);
            var13 = 16 + (int)(var14 * 16.0);
            var12 = (float)((double)var12 * var14);
         } else {
            float var17 = var10 / 5.0F % 2.0F;
            if (var17 > 1.0F) {
               var17 = Math.abs(var17 - 2.0F);
            }

            var13 = (int)(32.0F * (var17 / 2.0F + 1.0F));
         }

         int var18 = var7.getDrawX(this.x) - var13 / 2;
         int var15 = var7.getDrawY(this.y) - var13 / 2;
         final TextureDrawOptionsEnd var16 = this.texture.initDraw().light(var9.minLevelCopy(Math.min(var9.getLevel() + 100.0F, 150.0F))).size(var13, var13).rotate(var11, var13 / 2, var13 / 2).pos(var18, var15 - (int)var12);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var16.draw();
            }
         });
         this.addShadowDrawables(var2, var18, var15, var9, var11, var13 / 2);
      }
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("voidwiz", 4);
   }
}
