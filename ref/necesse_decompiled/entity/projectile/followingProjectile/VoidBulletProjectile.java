package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class VoidBulletProjectile extends FollowingProjectile {
   public VoidBulletProjectile() {
      this.height = 18.0F;
   }

   public VoidBulletProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this();
      this.setLevel(var9.getLevel());
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
      var1.putNextFloat(this.height);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.height = var1.getNextFloat();
   }

   public void init() {
      super.init();
      this.turnSpeed = 0.1F;
      this.givesLight = true;
      this.trailOffset = 0.0F;
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(47, 0, 142), 22.0F, 100, this.height);
      var1.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
      return var1;
   }

   protected Color getWallHitColor() {
      return new Color(47, 0, 142);
   }

   public void refreshParticleLight() {
      this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 260.0F, this.lightSaturation);
   }

   public void updateTarget() {
      if (this.traveledDistance > 50.0F) {
         this.findTarget((var0) -> {
            return var0.isHostile;
         }, 80.0F, 160.0F);
      }

   }

   public float getTurnSpeed(int var1, int var2, float var3) {
      return this.getTurnSpeed(var3) * this.getTurnSpeedMod(var1, var2, 20.0F, 90.0F, 160.0F);
   }

   public float getTurnSpeedMod(int var1, int var2, float var3, float var4, float var5) {
      float var6 = (float)(new Point(var1, var2)).distance((double)this.getX(), (double)this.getY());
      if (var6 < var5 && var6 > 5.0F) {
         float var7 = Math.abs(this.getAngleDifference(this.getAngleToTarget((float)var1, (float)var2)));
         float var8 = var7 > var4 ? 1.0F : (var7 - var4) / var4;
         float var9 = Math.abs(var6 - var5) / var5;
         return 1.0F + var9 * var3 + var8 * var3;
      } else {
         return 1.0F;
      }
   }

   public float getTurnSpeedMod(int var1, int var2, float var3, float var4, float var5, float var6) {
      float var7 = (float)(new Point(var1, var2)).distance((double)this.getX(), (double)this.getY());
      if (var7 < var5 && var7 > 5.0F) {
         float var8 = Math.abs(this.getAngleDifference(this.getAngleToTarget((float)var1, (float)var2)));
         float var9 = var8 > var3 ? 1.0F : var8 / var3;
         float var10 = var7 / var5;
         return 1.0F + var10 * var6 + var9 * var4;
      } else {
         return 1.0F;
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }

   public void playHitSound(float var1, float var2) {
      Screen.playSound(GameResources.gunhit, SoundEffect.effect(var1, var2));
   }
}
