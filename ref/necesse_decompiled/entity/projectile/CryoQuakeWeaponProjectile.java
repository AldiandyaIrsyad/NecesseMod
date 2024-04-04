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

public class CryoQuakeWeaponProjectile extends Projectile {
   private double distCounter;
   private double distBuffer;
   private final GroundPillarList<CryoQueenMob.CryoPillar> pillars = new GroundPillarList();

   public CryoQuakeWeaponProjectile() {
   }

   public CryoQuakeWeaponProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.setTarget(var5, var6);
      this.speed = var7;
      this.distance = var8;
      this.setDamage(var9);
      this.knockback = var10;
   }

   public void init() {
      super.init();
      this.maxMovePerTick = 12;
      this.height = 0.0F;
      this.piercing = 1;
      this.setWidth(30.0F);
      if (this.isClient()) {
         this.getLevel().entityManager.addPillarHandler(new GroundPillarHandler<CryoQueenMob.CryoPillar>(this.pillars) {
            protected boolean canRemove() {
               return CryoQuakeWeaponProjectile.this.removed();
            }

            public double getCurrentDistanceMoved() {
               return CryoQuakeWeaponProjectile.this.distCounter;
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
