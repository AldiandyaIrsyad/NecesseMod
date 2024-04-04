package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperScythePlayerProjectile extends Projectile {
   private long spawnTime;

   public ReaperScythePlayerProjectile() {
   }

   public ReaperScythePlayerProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this();
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setTarget(var5, var6);
      this.speed = var7;
      this.distance = var8;
      this.setDamage(var9);
      this.knockback = var10;
   }

   public void init() {
      super.init();
      this.setWidth(90.0F, true);
      this.isCircularHitbox = true;
      this.isSolid = false;
      this.height = 18.0F;
      this.piercing = 2;
      this.spawnTime = this.getLevel().getWorldEntity().getTime();
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
         int var10 = var7.getDrawX(this.x) - 48;
         int var11 = var7.getDrawY(this.y) - 48;
         int var12 = (int)this.getHeight();
         boolean var13 = this.dx < 0.0F;
         float var14 = this.getAngle() * (float)(var13 ? -1 : 1);
         TextureDrawOptionsEnd var15 = this.texture.initDraw().sprite(0, 0, 96).mirror(false, !var13).light(var9).rotate(var14, 48, 48);
         byte var16 = 100;
         TextureDrawOptionsEnd var17 = this.texture.initDraw().sprite(0, 1, 96).mirror(false, !var13).light(var9.minLevelCopy((float)var16)).alpha(0.6F).rotate(var14, 48, 48).pos(var10, var11 - var12);
         TextureDrawOptionsEnd var18 = var15.copy().pos(var10, var11 - var12);
         TextureDrawOptionsEnd var19 = var15.copy().alpha(0.6F).rotate(var14 + (float)(var13 ? 60 : -60), 48, 48).pos(var10, var11 - var12);
         TextureDrawOptionsEnd var20 = var15.copy().alpha(0.3F).rotate(var14 + (float)(var13 ? 120 : -120), 48, 48).pos(var10, var11 - var12);
         var3.add((var4x) -> {
            var20.draw();
            var19.draw();
            var18.draw();
            var17.draw();
         });
      }
   }

   public float getAngle() {
      return (float)(this.getWorldEntity().getTime() - this.spawnTime);
   }
}
