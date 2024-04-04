package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BloodBoltProjectile extends FollowingProjectile {
   public BloodBoltProjectile() {
   }

   public void init() {
      super.init();
      this.turnSpeed = 0.5F;
      this.givesLight = true;
      this.height = 18.0F;
      this.piercing = 0;
      this.bouncing = 2;
   }

   protected int getExtraSpinningParticles() {
      return super.getExtraSpinningParticles() + 1;
   }

   public Color getParticleColor() {
      return new Color(127, 0, 0);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(127, 0, 0), 6.0F, 500, 18.0F);
   }

   public void updateTarget() {
      if (this.traveledDistance > 100.0F) {
         this.findTarget((var0) -> {
            return var0.isHostile;
         }, 160.0F, 160.0F);
      }

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
