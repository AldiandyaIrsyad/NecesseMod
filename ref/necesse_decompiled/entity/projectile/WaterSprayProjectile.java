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

public class WaterSprayProjectile extends Projectile {
   public WaterSprayProjectile() {
   }

   public void init() {
      super.init();
      this.maxMovePerTick = 24;
      this.height = 18.0F;
      this.piercing = 1;
      this.bouncing = 0;
   }

   public void onMaxMoveTick() {
      if (this.isClient()) {
         this.spawnSpinningParticle();
      }

   }

   public Color getParticleColor() {
      return new Color(55, 125, 216);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0F, 250, this.getHeight());
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }
}
