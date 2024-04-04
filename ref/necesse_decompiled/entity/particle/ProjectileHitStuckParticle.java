package necesse.entity.particle;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public abstract class ProjectileHitStuckParticle extends Particle {
   protected Mob target;
   protected float xOffset;
   protected float yOffset;
   protected float angle;

   public ProjectileHitStuckParticle(Mob var1, Projectile var2, float var3, float var4, float var5, long var6) {
      super(var2.getLevel(), var3, var4, var6);
      this.target = var1;
      this.xOffset = var3 - (var1 == null ? 0.0F : var1.x);
      this.yOffset = var4 - (var1 == null ? 0.0F : var1.y);
      Point2D.Float var8 = GameMath.normalize(var2.dx, var2.dy);
      this.xOffset += var8.x * var5;
      this.yOffset += var8.y * var5;
      this.angle = var2.getAngle();
   }

   private float levelDrawX() {
      return this.target == null ? this.xOffset : (float)this.target.getDrawX() + this.xOffset;
   }

   private float levelDrawY() {
      return this.target == null ? this.yOffset : (float)this.target.getDrawY() + this.yOffset;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if ((this.target == null || !this.target.removed()) && !this.removed()) {
         this.x = this.levelDrawX();
         this.y = this.levelDrawY();
         this.addDrawables(this.target, this.x, this.y, this.angle, var1, var2, var3, var5, var6, var7, var8);
      } else {
         this.remove();
      }
   }

   public abstract void addDrawables(Mob var1, float var2, float var3, float var4, List<LevelSortedDrawable> var5, OrderableDrawables var6, OrderableDrawables var7, Level var8, TickManager var9, GameCamera var10, PlayerMob var11);
}
