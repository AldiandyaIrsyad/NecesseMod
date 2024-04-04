package necesse.entity.projectile.pathProjectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class CryoWarningCirclingProjectile extends PositionedCirclingProjectile {
   protected float radius;
   protected boolean clockwise;

   public CryoWarningCirclingProjectile() {
   }

   public CryoWarningCirclingProjectile(float var1, float var2, float var3, float var4, boolean var5, float var6, int var7) {
      this.centerX = var1;
      this.centerY = var2;
      this.radius = var3;
      this.currentAngle = var4;
      this.clockwise = var5;
      this.speed = var6;
      this.setDistance(var7);
   }

   public void init() {
      super.init();
      this.maxMovePerTick = 12;
      this.height = 0.0F;
      this.canHitMobs = false;
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
   }

   public Color getParticleColor() {
      return new Color(116, 131, 224);
   }

   public Trail getTrail() {
      Trail var1 = new Trail(this, this.getLevel(), new Color(116, 131, 224), 36.0F, 1000, this.getHeight());
      var1.drawOnTop = true;
      return var1;
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
   }
}
