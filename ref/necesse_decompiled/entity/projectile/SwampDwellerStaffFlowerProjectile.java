package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
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

public class SwampDwellerStaffFlowerProjectile extends Projectile {
   private long spawnTime;
   private float startSpeed;

   public SwampDwellerStaffFlowerProjectile() {
   }

   public SwampDwellerStaffFlowerProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = var5;
      this.setDistance(var6);
      this.setDamage(var7);
      this.knockback = var8;
      this.setOwner(var9);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.startSpeed);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.startSpeed = var1.getNextFloat();
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.startSpeed = this.speed;
      this.setWidth(10.0F);
      this.spawnTime = this.getLevel().getWorldEntity().getTime();
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      float var4 = Math.abs(GameMath.limit(this.traveledDistance / (float)this.distance, 0.0F, 1.0F) - 1.0F);
      this.speed = Math.max(10.0F, var4 * this.startSpeed);
   }

   public void doHitLogic(Mob var1, LevelObjectHit var2, float var3, float var4) {
      super.doHitLogic(var1, var2, var3, var4);
      if (this.isServer()) {
         byte var5;
         if (var1 == null) {
            var5 = 8;
         } else {
            var5 = 4;
         }

         float var6 = var3 - this.dx * 2.0F;
         float var7 = var4 - this.dy * 2.0F;
         float var8 = (float)GameRandom.globalRandom.nextInt(360);

         for(int var9 = 0; var9 < var5; ++var9) {
            Point2D.Float var10 = GameMath.getAngleDir(var8 + (float)var9 * 360.0F / (float)var5);
            SwampDwellerStaffPetalProjectile var11 = new SwampDwellerStaffPetalProjectile(this.getLevel(), this.getOwner(), var6, var7, var6 + var10.x * 100.0F, var7 + var10.y * 100.0F, this.startSpeed, this.distance * 100, this.getDamage().modFinalMultiplier(0.66F), this.knockback);
            if (this.modifier != null) {
               this.modifier.initChildProjectile(var11, 1.0F, var5 / 2);
            }

            if (var1 != null) {
               var11.startHitCooldown(var1);
            }

            this.getLevel().entityManager.projectiles.add(var11);
         }

      }
   }

   public Color getParticleColor() {
      return new Color(46, 71, 50);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0F, 200, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
            }
         });
         TextureDrawOptionsEnd var13 = this.shadowTexture.initDraw().light(var9).rotate(this.getAngle(), this.shadowTexture.getWidth() / 2, this.shadowTexture.getHeight() / 2).pos(var10, var11);
         var2.add((var1x) -> {
            var13.draw();
         });
      }
   }

   public float getAngle() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime) / 2.0F;
   }
}
