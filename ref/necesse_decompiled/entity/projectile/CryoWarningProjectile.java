package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class CryoWarningProjectile extends Projectile {
   public CryoWarningProjectile() {
   }

   public CryoWarningProjectile(float var1, float var2, float var3, float var4, int var5) {
      this.x = var1;
      this.y = var2;
      this.setAngle(var3);
      this.speed = var4;
      this.setDistance(var5);
   }

   public void init() {
      super.init();
      this.canHitMobs = false;
      this.isSolid = false;
      this.height = 0.0F;
   }

   public Color getParticleColor() {
      return new Color(116, 131, 224);
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(116, 131, 224), 28.0F, 1000, this.getHeight());
      var1.drawOnTop = true;
      return var1;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }
}
