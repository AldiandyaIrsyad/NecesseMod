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

public class VultureHatchlingProjectile extends Projectile {
   public VultureHatchlingProjectile() {
   }

   public VultureHatchlingProjectile(Mob var1, int var2, int var3, int var4, int var5, GameDamage var6) {
      this.setOwner(var1);
      this.x = (float)var2;
      this.y = (float)var3;
      this.setTarget((float)var4, (float)var5);
      this.setDamage(var6);
      this.speed = 100.0F;
      this.setDistance(1000);
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.piercing = 0;
      this.width = 5.0F;
      this.isSolid = false;
   }

   public Color getParticleColor() {
      return null;
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(65, 59, 21), 8.0F, 100, this.getHeight());
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
