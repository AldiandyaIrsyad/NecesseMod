package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampBallProjectile extends Projectile {
   public SwampBallProjectile() {
   }

   public SwampBallProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
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

   public void init() {
      super.init();
      this.height = 16.0F;
      this.isSolid = false;
      this.givesLight = true;
      this.setWidth(5.0F);
      this.particleDirOffset = -30.0F;
      this.particleRandomOffset = 3.0F;
   }

   public Color getParticleColor() {
      return new Color(9, 106, 58);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(9, 106, 58), 16.0F, 500, 16.0F);
   }

   protected int getExtraSpinningParticles() {
      return super.getExtraSpinningParticles() + 1;
   }

   protected void modifySpinningParticle(ParticleOption var1) {
      super.modifySpinningParticle(var1);
      var1.sizeFades(8, 14);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - 16;
         int var11 = var7.getDrawY(this.y);
         int var12 = GameUtils.getAnim(this.getWorldEntity().getTime(), 6, 400);
         final TextureDrawOptionsEnd var13 = this.texture.initDraw().sprite(var12, 0, 32, 64).light(var9).rotate(this.getAngle(), 16, 0).pos(var10, var11 - (int)this.getHeight());
         var1.add(new EntityDrawable(this) {
            public void draw(TickManager var1) {
               var13.draw();
            }
         });
         TextureDrawOptionsEnd var14 = this.shadowTexture.initDraw().sprite(var12, 0, 32, 64).light(var9).rotate(this.getAngle(), 16, 0).pos(var10, var11);
         var2.add((var1x) -> {
            var14.draw();
         });
      }
   }
}
