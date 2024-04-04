package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class VoidMissileProjectile extends FollowingProjectile {
   public VoidMissileProjectile() {
   }

   public VoidMissileProjectile(float var1, float var2, float var3, float var4, float var5, int var6, GameDamage var7, int var8, Mob var9) {
      this.applyData(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void init() {
      super.init();
      this.stopsAtTarget = true;
      this.turnSpeed = 10000.0F;
      this.givesLight = true;
      this.height = 0.0F;
      this.piercing = 0;
      this.setWidth(30.0F);
   }

   protected void replaceTrail() {
   }

   public Color getParticleColor() {
      return new Color(50, 0, 102);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(50, 0, 102), 12.0F, 300, 0.0F);
   }

   public void clientTick() {
      super.clientTick();
      this.spawnSpinningParticle();
   }

   public void onMaxMoveTick() {
      if (this.isClient()) {
         this.spawnSpinningParticle();
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }
}
