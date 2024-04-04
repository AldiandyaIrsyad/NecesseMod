package necesse.entity.projectile;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillarList;
import necesse.entity.manager.GroundPillarHandler;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.CryoQueenMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class CryoQuakeProjectile extends Projectile {
   private double distCounter;
   private double distBuffer;
   private final GroundPillarList<CryoQueenMob.CryoPillar> pillars = new GroundPillarList();

   public CryoQuakeProjectile() {
   }

   public CryoQuakeProjectile(float var1, float var2, float var3, float var4, int var5, GameDamage var6, int var7, Mob var8) {
      this.x = var1;
      this.y = var2;
      this.setAngle(var3);
      this.speed = var4;
      this.setDistance(var5);
      this.setDamage(var6);
      this.knockback = var7;
      this.setOwner(var8);
   }

   public void init() {
      super.init();
      this.maxMovePerTick = 12;
      this.isSolid = false;
      this.height = 0.0F;
      this.piercing = 1000;
      this.setWidth(24.0F);
      if (this.isClient()) {
         this.getLevel().entityManager.addPillarHandler(new GroundPillarHandler<CryoQueenMob.CryoPillar>(this.pillars) {
            protected boolean canRemove() {
               return CryoQuakeProjectile.this.removed();
            }

            public double getCurrentDistanceMoved() {
               return CryoQuakeProjectile.this.distCounter;
            }
         });
      }

   }

   public Trail getTrail() {
      return null;
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      this.distCounter += var2;
      this.distBuffer += var2;

      while(this.distBuffer > 12.0) {
         this.distBuffer -= 12.0;
         synchronized(this.pillars) {
            this.pillars.add(new CryoQueenMob.CryoPillar((int)(this.x + GameRandom.globalRandom.floatGaussian() * 6.0F), (int)(this.y + GameRandom.globalRandom.floatGaussian() * 4.0F), this.distCounter, this.getWorldEntity().getLocalTime()));
         }
      }

   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }
}
