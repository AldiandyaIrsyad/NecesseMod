package necesse.entity.projectile.laserProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class LaserProjectile extends Projectile {
   public LaserProjectile() {
   }

   public void onMaxMoveTick() {
      if (this.isClient()) {
         this.spawnSpinningParticle();
      }

   }

   public float tickMovement(float var1) {
      if (this.removed()) {
         return 0.0F;
      } else {
         this.moveDist((double)this.distance);
         return (float)this.distance;
      }
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }

   protected Color getWallHitColor() {
      return null;
   }
}
