package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.pathProjectile.PathProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PathTestProjectile extends PathProjectile {
   public boolean inverted = false;
   public float startX;
   public float startY;
   public float startDx;
   public float startDy;
   public float movedDist;

   public PathTestProjectile() {
   }

   public PathTestProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10, boolean var11) {
      this.setLevel(var1);
      this.setOwner(var2);
      this.x = var3;
      this.y = var4;
      this.startX = var3;
      this.startY = var4;
      this.setTarget(var5, var6);
      this.startDx = this.dx;
      this.startDy = this.dy;
      this.speed = var7;
      this.distance = var8;
      this.setDamage(var9);
      this.knockback = var10;
      this.inverted = var11;
   }

   public void setupPositionPacket(PacketWriter var1) {
      super.setupPositionPacket(var1);
      var1.putNextFloat(this.startX);
      var1.putNextFloat(this.startY);
      var1.putNextFloat(this.startDx);
      var1.putNextFloat(this.startDy);
      var1.putNextFloat(this.movedDist);
      var1.putNextBoolean(this.inverted);
   }

   public void applyPositionPacket(PacketReader var1) {
      super.applyPositionPacket(var1);
      this.startX = var1.getNextFloat();
      this.startY = var1.getNextFloat();
      this.startDx = var1.getNextFloat();
      this.startDy = var1.getNextFloat();
      this.movedDist = var1.getNextFloat();
      this.inverted = var1.getNextBoolean();
   }

   public void init() {
      super.init();
      this.height = 18.0F;
      this.piercing = 200;
      this.bouncing = 10;
      this.givesLight = true;
      this.trailOffset = 0.0F;
      this.isSolid = true;
      this.autoSetDirection = true;
   }

   public Point2D.Float getPosition(double var1) {
      this.movedDist = (float)((double)this.movedDist + var1);
      Point2D.Float var3 = new Point2D.Float(this.startX + this.startDx * this.movedDist, this.startY + this.startDy * this.movedDist);
      float var4 = GameMath.sin(this.movedDist) * 40.0F;
      Point2D.Float var5 = GameMath.getPerpendicularPoint(var3, this.inverted ? -var4 : var4, this.startDx, this.startDy);
      return new Point2D.Float(var5.x, var5.y);
   }

   public Color getParticleColor() {
      return new Color(50, 0, 102);
   }

   protected int getExtraSpinningParticles() {
      return super.getExtraSpinningParticles() + 2;
   }

   protected void modifySpinningParticle(ParticleOption var1) {
      var1.lifeTime(2000);
   }

   public Trail getTrail() {
      return new Trail(this, this.getLevel(), new Color(50, 0, 102), 12.0F, 1500, 18.0F);
   }

   public void addDrawables(List<LevelSortedDrawable> var1, OrderableDrawables var2, OrderableDrawables var3, OrderableDrawables var4, Level var5, TickManager var6, GameCamera var7, PlayerMob var8) {
      if (!this.removed()) {
         GameLight var9 = var5.getLightLevel(this);
         int var10 = var7.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
         int var11 = var7.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
         this.addShadowDrawables(var2, var10, var11, var9, this.getAngle(), this.shadowTexture.getHeight() / 2);
      }
   }

   public void onBounce(IntersectionPoint var1) {
      super.onBounce(var1);
      this.startX = this.x;
      this.startY = this.y;
      this.movedDist = 0.0F;
      if (var1.dir != IntersectionPoint.Dir.RIGHT && var1.dir != IntersectionPoint.Dir.LEFT) {
         if (var1.dir == IntersectionPoint.Dir.UP || var1.dir == IntersectionPoint.Dir.DOWN) {
            this.startDy = -this.startDy;
         }
      } else {
         this.startDx = -this.startDx;
      }

   }
}
