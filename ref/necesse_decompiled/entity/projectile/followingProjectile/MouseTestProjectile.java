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

public class MouseTestProjectile extends FollowingProjectile {
   public MouseTestProjectile() {
   }

   public void init() {
      super.init();
      this.stopsAtTarget = true;
      this.turnSpeed = 10000.0F;
      this.givesLight = true;
      this.height = 0.0F;
      this.piercing = 0;
      this.bouncing = 2;
      this.setWidth(200.0F);
      this.isSolid = false;
   }

   public Color getParticleColor() {
      return new Color(127, 0, 0);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(127, 0, 0), 6.0F, 500, 0.0F);
   }

   public void updateTarget() {
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, final GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y);
         final TextureDrawOptionsEnd var12 = this.texture.initDraw().light(var9).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var12.draw();
               MouseTestProjectile.this.drawDebug(var7);
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), 0);
      }
   }
}
