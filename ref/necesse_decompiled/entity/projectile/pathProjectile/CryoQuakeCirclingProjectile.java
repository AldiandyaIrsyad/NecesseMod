package necesse.entity.projectile.pathProjectile;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
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

public class CryoQuakeCirclingProjectile extends PositionedCirclingProjectile {
   private double distCounter;
   private double distBuffer;
   private final GroundPillarList<CryoQueenMob.CryoPillar> pillars = new GroundPillarList();
   protected float radius;
   protected boolean clockwise;

   public CryoQuakeCirclingProjectile() {
   }

   public CryoQuakeCirclingProjectile(float var1, float var2, float var3, float var4, boolean var5, float var6, int var7, GameDamage var8, int var9, Mob var10) {
      this.centerX = var1;
      this.centerY = var2;
      this.radius = var3;
      this.currentAngle = var4;
      this.clockwise = var5;
      this.speed = var6;
      this.setDistance(var7);
      this.setDamage(var8);
      this.knockback = var9;
      this.setOwner(var10);
   }

   public void init() {
      super.init();
      this.maxMovePerTick = 12;
      this.height = 0.0F;
      this.piercing = 1000;
      this.setWidth(24.0F);
      if (this.isClient()) {
         this.getLevel().entityManager.addPillarHandler(new GroundPillarHandler<CryoQueenMob.CryoPillar>(this.pillars) {
            protected boolean canRemove() {
               return CryoQuakeCirclingProjectile.this.removed();
            }

            public double getCurrentDistanceMoved() {
               return CryoQuakeCirclingProjectile.this.distCounter;
            }
         });
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextFloat(this.radius);
      var1.putNextBoolean(this.clockwise);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.radius = var1.getNextFloat();
      this.clockwise = var1.getNextBoolean();
   }

   public float getRadius() {
      return this.radius;
   }

   public boolean rotatesClockwise() {
      return this.clockwise;
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      this.radius = (float)((double)this.radius + var2 * 1.2);
      this.distCounter += var2;
      this.distBuffer += var2;

      while(this.distBuffer > 8.0) {
         this.distBuffer -= 8.0;
         synchronized(this.pillars) {
            this.pillars.add(new CryoQueenMob.CryoPillar((int)(this.x + GameRandom.globalRandom.floatGaussian() * 6.0F), (int)(this.y + GameRandom.globalRandom.floatGaussian() * 4.0F), this.distCounter, this.getWorldEntity().getLocalTime()));
         }
      }

   }

   public Trail getTrail() {
      return null;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }
}
