package necesse.entity.projectile.pathProjectile;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.TrailVector;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelObjectHit;

public abstract class PathProjectile extends Projectile {
   protected boolean canMoveLessThanDist;
   protected boolean autoSetDirection;

   public PathProjectile() {
      this.isSolid = false;
      this.canMoveLessThanDist = false;
      this.autoSetDirection = true;
      this.dx = 1.0F;
      this.dy = 0.0F;
   }

   protected double getDistanceMovedBeforeCollision(double var1) {
      Point2D.Float var3 = new Point2D.Float(this.x, this.y);
      Point2D.Float var4 = this.getPosition(var1);
      this.x = var4.x;
      this.y = var4.y;
      if (this.autoSetDirection) {
         Point2D.Float var5 = GameMath.normalize(var4.x - var3.x, var4.y - var3.y);
         this.dx = var5.x;
         this.dy = var5.y;
         if (this.dx == 0.0F && this.dy == 0.0F) {
            this.dx = 1.0F;
            this.dy = 0.0F;
         }

         this.angle = (float)Math.toDegrees(Math.atan2((double)this.dy, (double)this.dx));
         this.angle += 90.0F;
         this.fixAngle();
      }

      Line2D.Float var14 = new Line2D.Float(var3, var4);
      if (!this.isSolid) {
         this.checkHitCollision(var14);
      } else {
         Point2D.Float var6 = var3;
         Line2D.Float var7 = var14;
         CollisionFilter var8 = this.getLevelCollisionFilter();
         ArrayList var9 = this.getLevel().getCollisions(var14, var8);
         if (var9.isEmpty() && this.useWidthForCollision) {
            float var10 = this.getWidth();

            for(int var11 = 8; (float)var11 < var10 / 2.0F; var11 += 8) {
               var6 = GameMath.getPerpendicularPoint(var3, (float)var11, this.dx, this.dy);
               Point2D.Float var12 = new Point2D.Float(var6.x + (float)((double)this.dx * var1), var6.y + (float)((double)this.dy * var1));
               Line2D.Float var13 = new Line2D.Float(var6, var12);
               var9 = this.getLevel().getCollisions(var13, var8);
               if (!var9.isEmpty()) {
                  var7 = var13;
                  break;
               }

               var6 = GameMath.getPerpendicularPoint(var3, (float)(-var11), this.dx, this.dy);
               var12 = new Point2D.Float(var6.x + (float)((double)this.dx * var1), var6.y + (float)((double)this.dy * var1));
               var13 = new Line2D.Float(var6, var12);
               var9 = this.getLevel().getCollisions(var13, var8);
               if (!var9.isEmpty()) {
                  var7 = var13;
                  break;
               }
            }
         }

         IntersectionPoint var17 = this.getLevel().getCollisionPoint(var9, var7, false);
         if (var17 != null) {
            Point2D.Float var18 = new Point2D.Float(this.x - var6.x, this.y - var6.y);
            this.x = (float)var17.getX() + var18.x;
            this.y = (float)var17.getY() + var18.y;
            if (this.bounced < this.bouncing + (Integer)this.getOwner().buffManager.getModifier(BuffModifiers.PROJECTILE_BOUNCES) && this.canBounce) {
               this.onBounce(var17);
               this.updateAngle();
               this.sendPositionUpdate = true;
            } else if (var17.dir == IntersectionPoint.Dir.UP) {
               this.y += 8.0F;
            } else if (var17.dir == IntersectionPoint.Dir.RIGHT) {
               this.x -= 8.0F;
            } else if (var17.dir == IntersectionPoint.Dir.DOWN) {
               this.y -= 8.0F;
            } else if (var17.dir == IntersectionPoint.Dir.LEFT) {
               this.x += 8.0F;
            }

            this.onHit((Mob)null, (LevelObjectHit)var17.target, (float)var17.getX(), (float)var17.getY(), false, (ServerClient)null);
            ++this.bounced;
            this.checkHitCollision(new Line2D.Float(this.x, this.y, (float)var17.x, (float)var17.y));
         } else {
            this.checkHitCollision(var14);
         }
      }

      if (this.canMoveLessThanDist) {
         double var15 = var3.distance(var4);
         if (var15 < var1) {
            double var16 = var1 - var15;
            this.traveledDistance = (float)((double)this.traveledDistance - var16);
            this.tickDistMoved = (float)((double)this.tickDistMoved - var16);
            this.lightDistMoved = (float)((double)this.lightDistMoved - var16);
         }
      }

      return var1;
   }

   public void onBounce(IntersectionPoint var1) {
      if (this.trail != null) {
         this.trail.addBreakPoint(new TrailVector((float)var1.getX(), (float)var1.getY(), this.dx, this.dy, this.trail.thickness, this.getHeight()));
      }

      if (var1.dir != IntersectionPoint.Dir.RIGHT && var1.dir != IntersectionPoint.Dir.LEFT) {
         if (var1.dir == IntersectionPoint.Dir.UP || var1.dir == IntersectionPoint.Dir.DOWN) {
            this.dy = -this.dy;
            this.y += Math.signum(this.dy) * 4.0F;
         }
      } else {
         this.dx = -this.dx;
         this.x += Math.signum(this.dx) * 4.0F;
      }

   }

   public abstract Point2D.Float getPosition(double var1);
}
