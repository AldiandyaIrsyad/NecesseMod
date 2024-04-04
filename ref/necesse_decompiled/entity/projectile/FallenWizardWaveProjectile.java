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
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallenWizardWaveProjectile extends Projectile {
   public FallenWizardWaveProjectile() {
   }

   public FallenWizardWaveProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this.x = var1;
      this.y = var2;
      this.setTarget(var3, var4);
      this.speed = var5;
      this.setDistance(var6);
      this.setDamage(var7);
      this.knockback = var8;
      this.setOwner(var9);
   }

   public void init() {
      super.init();
      this.piercing = 9999;
      this.setWidth(200.0F, 20.0F);
      this.height = 0.0F;
      this.isSolid = false;
      this.givesLight = true;
      this.particleRandomOffset = -8.0F;
      this.particleRandomPerpOffset = 60.0F;
   }

   protected CollisionFilter getLevelCollisionFilter() {
      return super.getLevelCollisionFilter().addFilter((var0) -> {
         return var0.object().object.isWall || var0.object().object.isRock;
      });
   }

   public Color getParticleColor() {
      return new Color(50, 0, 102);
   }

   public Trail getTrail() {
      return null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         float var9 = this.getFadeAlphaTime(300, 300);
         GameLight var10 = var5.getLightLevel(this);
         int var11 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var12 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var10.minLevelCopy(Math.min(var10.getLevel() + 100.0F, 150.0F))).alpha(var9).rotate(this.getAngle() - 135.0F, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var11, var12);
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
      }
   }
}
