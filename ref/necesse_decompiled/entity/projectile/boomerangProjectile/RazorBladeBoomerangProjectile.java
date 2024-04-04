package necesse.entity.projectile.boomerangProjectile;

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

public class RazorBladeBoomerangProjectile extends BoomerangProjectile {
   public RazorBladeBoomerangProjectile() {
   }

   public void init() {
      super.init();
      this.setWidth(10.0F, true);
      this.height = 18.0F;
      this.bouncing = 100;
   }

   public Color getParticleColor() {
      return new Color(78, 112, 31);
   }

   protected int getExtraSpinningParticles() {
      return 0;
   }

   public Trail getTrail() {
      return null;
   }

   protected Color getWallHitColor() {
      return new Color(78, 112, 31);
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
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), this.shadowTexture.getHeight() / 2);
      }
   }

   public float getAngle() {
      return super.getAngle() * 1.5F;
   }

   public void playMoveSound() {
   }
}
