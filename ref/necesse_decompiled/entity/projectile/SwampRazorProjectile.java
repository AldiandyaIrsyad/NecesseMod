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
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampRazorProjectile extends Projectile {
   protected long spawnTime;

   public SwampRazorProjectile() {
   }

   public SwampRazorProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.setLevel(var1);
      this.x = var3;
      this.y = var4;
      this.speed = var7;
      this.setTarget(var5, var6);
      this.setDamage(var9);
      this.knockback = var10;
      this.setDistance(var8);
      this.setOwner(var2);
   }

   public SwampRazorProjectile(Level var1, Mob var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8) {
      this(var1, var2, var2.x, var2.y, var3, var4, var5, var6, var7, var8);
   }

   public void init() {
      super.init();
      this.spawnTime = this.getWorldEntity().getTime();
      this.piercing = 2;
      this.bouncing = 8;
      this.isSolid = true;
      this.setWidth(24.0F);
   }

   public Color getParticleColor() {
      return null;
   }

   public Trail getTrail() {
      return null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         float var9 = 1.0F;
         if (this.traveledDistance > (float)(this.distance - 100)) {
            var9 = ((float)this.distance - this.traveledDistance) / 100.0F;
         }

         GameLight var10 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
         int var11 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var12 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         float var13 = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 1.5F;
         final TextureDrawOptionsEnd var14 = this.texture.initDraw().light(var10).alpha(var9).rotate(var13, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var11, var12 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var14.draw();
            }
         });
         this.addShadowDrawables(var2, var11, var12, var10, var13, this.shadowTexture.getHeight() / 2);
      }
   }
}
