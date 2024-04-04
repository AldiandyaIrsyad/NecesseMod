package necesse.entity.projectile.boomerangProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.ReaperMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperScytheProjectile extends BoomerangProjectile {
   private ReaperMob reaperMob;

   public ReaperScytheProjectile() {
   }

   public ReaperScytheProjectile(ReaperMob var1, int var2, int var3, int var4, int var5, GameDamage var6, int var7, int var8) {
      this.reaperMob = var1;
      this.setOwner(var1);
      this.x = (float)var2;
      this.y = (float)var3;
      this.setTarget((float)var4, (float)var5);
      this.setDamage(var6);
      this.speed = (float)var7;
      this.setDistance(var8);
   }

   public void init() {
      super.init();
      this.setWidth(120.0F, true);
      this.isCircularHitbox = true;
      this.height = 18.0F;
      this.isSolid = false;
      this.piercing = 10000;
   }

   public Color getParticleColor() {
      return null;
   }

   public Trail getTrail() {
      return null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         GameTexture var10 = MobRegistry.Textures.reaper;
         GameTexture var11 = MobRegistry.Textures.reaperGlow;
         int var12 = var7.getDrawX(this.x) - 64;
         int var13 = var7.getDrawY(this.y) - 64;
         int var14 = (int)this.getHeight();
         float var15 = this.getAngle();
         TextureDrawOptionsEnd var16 = var10.initDraw().sprite(0, 4, 128).light(var9).rotate(var15, 64, 64);
         byte var17 = 100;
         TextureDrawOptionsEnd var18 = var11.initDraw().sprite(0, 4, 128).light(var9.minLevelCopy((float)var17)).rotate(var15, 64, 64).pos(var12, var13 - var14);
         TextureDrawOptionsEnd var19 = var16.copy().pos(var12, var13 - var14);
         TextureDrawOptionsEnd var20 = var16.copy().alpha(0.6F).rotate(var15 - 60.0F, 64, 64).pos(var12, var13 - var14);
         TextureDrawOptionsEnd var21 = var16.copy().alpha(0.3F).rotate(var15 - 120.0F, 64, 64).pos(var12, var13 - var14);
         var3.add((var4x) -> {
            var21.draw();
            var20.draw();
            var19.draw();
            var18.draw();
         });
      }
   }

   public float getAngle() {
      return super.getAngle();
   }

   public void playMoveSound() {
   }

   public void remove() {
      if (!this.removed() && this.reaperMob != null) {
         this.reaperMob.setHasScythe(true);
      }

      super.remove();
   }
}
