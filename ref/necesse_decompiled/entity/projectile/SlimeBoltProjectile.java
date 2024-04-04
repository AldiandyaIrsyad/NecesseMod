package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
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

public class SlimeBoltProjectile extends Projectile {
   public SlimeBoltProjectile() {
   }

   public SlimeBoltProjectile(Level var1, float var2, float var3, float var4, float var5, float var6, int var7, GameDamage var8, Mob var9) {
      this.setLevel(var1);
      this.x = var2;
      this.y = var3;
      this.setTarget(var4, var5);
      this.speed = var6;
      this.setDamage(var8);
      this.setOwner(var9);
      this.setDistance(var7);
      this.bouncing = 1;
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.heightBasedOnDistance = true;
      this.setWidth(8.0F);
   }

   public Color getParticleColor() {
      return new Color(196, 232, 205, 255);
   }

   protected void modifySpinningParticle(ParticleOption var1) {
      var1.givesLight(75.0F, 0.5F).lifeTime(1000);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(196, 232, 205), 12.0F, 250, this.getHeight());
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
