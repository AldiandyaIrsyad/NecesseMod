package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.boomerangProjectile.BoomerangProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SageBoomerangProjectile extends BoomerangProjectile {
   public SageBoomerangProjectile() {
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.setWidth(14.0F);
      this.bouncing = 8;
   }

   protected int getExtraSpinningParticles() {
      return super.getExtraSpinningParticles() + 2;
   }

   protected void modifySpinningParticle(ParticleOption var1) {
      var1.givesLight(200.0F, 0.7F);
      var1.lifeTime(1000);
   }

   public Color getParticleColor() {
      return ParticleOption.randomizeColor(200.0F, 0.5F, 0.44F, 0.0F, 0.0F, 0.1F);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(65, 105, 151), 25.0F, 700, 18.0F);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this.getX() / 32, this.getY() / 32);
         int var10 = var7.getDrawX(this.x) - this.texture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.texture.getHeight() / 2;
         float var12 = this.getAngle();
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().light(var9).rotate(var12, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         this.addShadowDrawables(var2, var10, var11, var9, var12, this.shadowTexture.getHeight() / 2);
      }
   }

   public float getAngle() {
      return super.getAngle() * 1.5F;
   }

   public void playMoveSound() {
   }
}
