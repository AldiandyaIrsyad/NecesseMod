package necesse.entity.projectile;

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

public class EmpressSlashProjectile extends Projectile {
   private final int startThickness = 40;

   public EmpressSlashProjectile() {
   }

   public EmpressSlashProjectile(float var1, float var2, float var3, GameDamage var4, Mob var5) {
      this.x = var1;
      this.y = var2;
      this.setAngle(var3);
      this.setDamage(var4);
      this.setOwner(var5);
      this.setDistance(500);
      this.speed = 500.0F;
   }

   public void init() {
      super.init();
      this.piercing = Integer.MAX_VALUE;
      this.isSolid = false;
   }

   public float getTrailThickness() {
      return 40.0F * (1.0F - this.traveledDistance / (float)this.distance);
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(250, 50, 50), 40.0F, 250, 18.0F);
      var1.drawOnTop = true;
      return var1;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }
}
