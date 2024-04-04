package necesse.entity.projectile.followingProjectile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.function.Predicate;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketProjectileTargetUpdate;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public abstract class FollowingProjectile extends Projectile {
   public Entity target;
   public Point targetPos;
   public float turnSpeed = 0.5F;
   public boolean clearTargetPosWhenAligned;
   public boolean clearTargetWhenAligned;
   public boolean stopsAtTarget;
   private float originalSpeed;
   private boolean isStoppedAtTarget;
   public float angleLeftToTurn = -1.0F;
   private int updateTicker;

   public FollowingProjectile(boolean var1, boolean var2) {
      super(var1, var2);
   }

   public FollowingProjectile(boolean var1) {
      super(var1);
   }

   public FollowingProjectile() {
   }

   public void addTargetData(PacketWriter var1) {
      if (this.target != null) {
         var1.putNextMaxValue(1, 2);
         if (this.target == null) {
            var1.putNextInt(-1);
         } else {
            var1.putNextInt(this.target.getUniqueID());
         }
      } else if (this.targetPos != null) {
         var1.putNextMaxValue(2, 2);
         var1.putNextInt(this.targetPos.x);
         var1.putNextInt(this.targetPos.y);
      } else {
         var1.putNextMaxValue(0, 2);
      }

   }

   public void applyTargetData(PacketReader var1) {
      switch (var1.getNextMaxValue(2)) {
         case 0:
            this.target = null;
            this.targetPos = null;
            break;
         case 1:
            this.targetPos = null;
            int var2 = var1.getNextInt();
            if (var2 == -1) {
               this.target = null;
            } else {
               this.target = GameUtils.getLevelMob(var2, this.getLevel());
            }
            break;
         case 2:
            this.target = null;
            int var3 = var1.getNextInt();
            int var4 = var1.getNextInt();
            this.targetPos = new Point(var3, var4);
      }

   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      this.addTargetData(var1);
      var1.putNextBoolean(this.angleLeftToTurn >= 0.0F);
      if (this.angleLeftToTurn >= 0.0F) {
         var1.putNextFloat(this.angleLeftToTurn);
      }

   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.applyTargetData(var1);
      if (var1.getNextBoolean()) {
         this.angleLeftToTurn = var1.getNextFloat();
      }

   }

   public void init() {
      super.init();
      this.updateTicker = 0;
      this.stopsAtTarget = false;
      this.isStoppedAtTarget = false;
      this.originalSpeed = 0.0F;
   }

   public float tickMovement(float var1) {
      if (this.removed()) {
         return 0.0F;
      } else {
         float var2 = this.hasTarget() ? (float)(new Point(this.getTargetX(), this.getTargetY())).distance((double)this.x, (double)this.y) : 1.0F;
         if (this.isStoppedAtTarget && var2 >= 1.0F) {
            this.speed = this.originalSpeed;
            this.isStoppedAtTarget = false;
         }

         if (this.isStoppedAtTarget) {
            float var3 = this.getMoveDist(this.dx * this.originalSpeed, var1);
            float var4 = this.getMoveDist(this.dy * this.originalSpeed, var1);
            double var5 = Math.sqrt((double)(var3 * var3 + var4 * var4));
            if (Double.isNaN(var5)) {
               var5 = 0.0;
            }

            this.traveledDistance = (float)((double)this.traveledDistance + var5);
            this.checkRemoved();
            float var7 = this.getWidth();
            int var8 = (int)(var7 <= 0.0F ? 2.0 : Math.ceil((double)var7));
            this.checkCollision(new Rectangle(this.getX() - var8 / 2, this.getY() - var8 / 2, var8, var8));
            return (float)var5;
         } else {
            return super.tickMovement(var1);
         }
      }
   }

   public void onMoveTick(Point2D.Float var1, double var2) {
      super.onMoveTick(var1, var2);
      if (!this.isStoppedAtTarget) {
         this.updateTarget();
         if (this.hasTarget()) {
            float var4 = (float)var2;
            int var5 = this.getTargetX();
            int var6 = this.getTargetY();
            float var7 = this.getTurnSpeed(var5, var6, var4);
            if (this.angleLeftToTurn >= 0.0F) {
               var7 = Math.min(this.angleLeftToTurn, var7);
               this.angleLeftToTurn -= var7;
            }

            if (this.turnToward((float)var5, (float)var6, var7)) {
               if (this.clearTargetPosWhenAligned) {
                  this.targetPos = null;
               }

               if (this.clearTargetWhenAligned) {
                  this.target = null;
               }

               if (this.stopsAtTarget) {
                  float var8 = this.hasTarget() ? (float)(new Point(this.getTargetX(), this.getTargetY())).distance((double)this.x, (double)this.y) : 1.0F;
                  if ((double)var8 <= var2) {
                     this.isStoppedAtTarget = true;
                     this.originalSpeed = this.speed;
                  }
               }
            }
         }
      }

      if (this.isStoppedAtTarget) {
         this.speed = 0.0F;
         this.x = (float)this.getTargetX();
         this.y = (float)this.getTargetY();
      }

   }

   public float getOriginalSpeed() {
      return this.originalSpeed;
   }

   public final float getTurnSpeed(float var1) {
      return this.turnSpeed * var1;
   }

   public float getTurnSpeed(int var1, int var2, float var3) {
      return this.getTurnSpeed(var3);
   }

   protected float dynamicTurnSpeedMod(int var1, int var2, float var3) {
      float var4 = (float)(new Point(var1, var2)).distance((double)this.getX(), (double)this.getY());
      return var4 < var3 && var4 > 5.0F ? Math.abs(var4 - var3) / var3 * 4.0F + 1.0F : 1.0F;
   }

   protected float dynamicTurnSpeedMod(int var1, int var2) {
      return this.dynamicTurnSpeedMod(var1, var2, this.speed * 1.5F);
   }

   protected float invDynamicTurnSpeedMod(int var1, int var2, float var3) {
      float var4 = (float)(new Point(var1, var2)).distance((double)this.getX(), (double)this.getY());
      if (var4 > var3 && var4 > 5.0F) {
         float var5 = Math.abs(this.getAngleDifference(this.getAngleToTarget((float)var1, (float)var2)));
         float var6 = Math.abs(var4 - var3) / var3;
         if (var5 < 90.0F) {
            var6 *= 3.0F;
         }

         return 1.0F + var6;
      } else {
         return 1.0F;
      }
   }

   protected float invDynamicTurnSpeedMod(int var1, int var2) {
      return this.invDynamicTurnSpeedMod(var1, var2, (float)this.getTurnRadius());
   }

   public static int getTurnRadius(float var0, float var1) {
      float var2 = var0 / var1;
      float var3 = var2 * 360.0F;
      return (int)((double)var3 / 6.283185307179586);
   }

   public int getTurnRadius() {
      return getTurnRadius(this.getMoveDist(this.speed, 1.0F), this.getTurnSpeed(1.0F));
   }

   public void serverTick() {
      super.serverTick();
      if (this.isStoppedAtTarget) {
         this.updateTarget();
      }

      ++this.updateTicker;
      if (this.updateTicker >= 20) {
         this.sendServerTargetUpdate();
         this.updateTicker = 0;
      }

   }

   public void clientTick() {
      super.clientTick();
      if (this.isStoppedAtTarget) {
         this.updateTarget();
      }

   }

   public void sendClientTargetUpdate() {
      this.getLevel().getClient().network.sendPacket(new PacketProjectileTargetUpdate(this));
   }

   public void sendServerTargetUpdate() {
      if (!Settings.giveClientsPower) {
         this.getLevel().getServer().network.sendToClientsWithEntity(new PacketProjectileTargetUpdate(this), this);
      }

   }

   public boolean hasTarget() {
      return this.target != null && !this.target.removed() || this.targetPos != null;
   }

   public int getTargetX() {
      if (this.target != null && !this.target.removed()) {
         return this.target.getX();
      } else {
         return this.targetPos != null ? this.targetPos.x : this.getX();
      }
   }

   public int getTargetY() {
      if (this.target != null && !this.target.removed()) {
         return this.target.getY();
      } else {
         return this.targetPos != null ? this.targetPos.y : this.getY();
      }
   }

   public void updateTarget() {
   }

   public void findTarget(Predicate<Mob> var1, float var2, float var3) {
      this.target = null;
      int var4 = (int)(this.x + this.dx * var2);
      int var5 = (int)(this.y + this.dy * var2);
      ComputedObjectValue var6 = (ComputedObjectValue)GameUtils.streamTargetsRange(this.getOwner(), var4, var5, (int)var3).filter((var0) -> {
         return var0 != null && !var0.removed();
      }).filter(var1).map((var2x) -> {
         return new ComputedObjectValue(var2x, () -> {
            return var2x.getPositionPoint().distance((double)var4, (double)var5);
         });
      }).min(Comparator.comparing(ComputedValue::get)).orElse((Object)null);
      if (var6 != null && (Double)var6.get() <= (double)var3) {
         this.target = (Entity)var6.object;
      }

   }

   public void drawDebug(GameCamera var1) {
      FontOptions var2 = new FontOptions(16);
      int var3 = var1.getDrawX(this.x) - 16;
      int var4 = var1.getDrawY(this.y - this.getHeight());
      float var5 = this.getAngleToTarget((float)var1.getMouseLevelPosX(), (float)var1.getMouseLevelPosY());
      float var6 = this.getAngleDifference(var5);
      FontManager.bit.drawString((float)var3, (float)(var4 - 16), "" + var6, var2);
      FontManager.bit.drawString((float)var3, (float)(var4 - 32), "" + var5, var2);
      FontManager.bit.drawString((float)var3, (float)(var4 - 48), "" + this.getAngle(), var2);
      int var7 = (int)(this.x + this.dx * 160.0F);
      int var8 = (int)(this.y + this.dy * 160.0F);
      Screen.initQuadDraw(6, 6).color(0.0F, 1.0F, 0.0F).draw(var1.getDrawX(var7) - 3, var1.getDrawY(var8) - 3);
      if (this.target != null) {
         Screen.initQuadDraw(30, 30).color(1.0F, 0.0F, 0.0F, 0.5F).draw(var1.getDrawX(this.target.getX()) - 15, var1.getDrawY(this.target.getY()) - 15);
      }

      FontManager.bit.drawString((float)var3, (float)(var4 - 64), "" + this.dx + ", " + this.dy, var2);
      FontManager.bit.drawString((float)var3, (float)(var4 - 80), "" + this.getTeam(), var2);
      FontManager.bit.drawString((float)var3, (float)(var4 - 96), "" + this.getOwnerID(), var2);
      FontManager.bit.drawString((float)var3, (float)(var4 - 112), "" + (this.target == null ? "null" : this.target.getUniqueID()), var2);
      LineHitbox.fromAngled((float)var1.getDrawX(this.x), (float)var1.getDrawY(this.y), this.getAngle(), 10.0F, this.getWidth()).draw(1.0F, 0.0F, 0.0F, 0.5F);
      Mob var9 = this.target != null ? (Mob)this.target : (Mob)this.streamTargets(this.getOwner(), (Shape)null).min((var1x, var2x) -> {
         return (int)(var1x.getDistance(this.x, this.y) - var2x.getDistance(this.x, this.y));
      }).orElse((Object)null);
      if (var9 != null) {
         Line2D.Float var10 = new Line2D.Float(var9.x, var9.y, var9.x + -this.dy, var9.y + this.dx);
         Point2D var11 = GameMath.getIntersectionPoint(new Line2D.Float(this.x, this.y, this.x + this.dx, this.y + this.dy), var10, true);
         Screen.drawLineRGBA(var1.getDrawX(var9.x), var1.getDrawY(var9.y), var1.getDrawX((float)var11.getX()), var1.getDrawY((float)var11.getY()), 1.0F, 0.0F, 0.0F, 1.0F);
         Screen.drawLineRGBA(var1.getDrawX(this.x), var1.getDrawY(this.y), var1.getDrawX((float)var11.getX()), var1.getDrawY((float)var11.getY()), 1.0F, 0.0F, 0.0F, 1.0F);
         Rectangle var12 = var9.getHitBox();
         Screen.initQuadDraw(var12.width, var12.height).color(0.0F, 0.0F, 1.0F, 0.5F).draw(var1.getDrawX(var12.x), var1.getDrawY(var12.y));
      }

   }
}
