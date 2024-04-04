package necesse.entity.trails;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.util.GameMath;

public class TrailPointList {
   private final Trail trail;
   private ArrayList<TrailPoint> list = new ArrayList();

   public TrailPointList(Trail var1) {
      this.trail = var1;
   }

   public TrailPointList copy() {
      TrailPointList var1 = new TrailPointList(this.trail);
      var1.list.addAll(this.list);
      return var1;
   }

   public TrailPoint getLastPoint() {
      return (TrailPoint)this.list.get(this.list.size() - 1);
   }

   public TrailPoint getNextPoint(TrailVector var1, long var2) {
      TrailPoint var4 = new TrailPoint(var1, var2);
      var4.updatePrev((TrailPoint)this.list.get(this.list.size() - 1));
      this.getLastPoint().updateNext(var4);
      return var4;
   }

   public TrailPointList getNextPointSection(TrailPoint var1) {
      TrailPointList var2 = new TrailPointList(this.trail);
      var2.list.add(this.getLastPoint());
      var2.list.add(var1);
      return var2;
   }

   public TrailPoint add(TrailVector var1, long var2) {
      return this.add(new TrailPoint(var1, var2));
   }

   private TrailPoint add(TrailPoint var1) {
      if (!this.isEmpty()) {
         TrailPoint var2 = this.getLastPoint();
         var2.updateNext(var1);
         var1.updatePrev(var2);
      } else {
         var1.updatePrev((TrailPoint)null);
      }

      this.list.add(var1);
      return var1;
   }

   public TrailPoint get(int var1) {
      return (TrailPoint)this.list.get(var1);
   }

   public TrailPoint removeFirst() {
      TrailPoint var1 = (TrailPoint)this.list.remove(0);
      if (!this.isEmpty()) {
         ((TrailPoint)this.list.get(0)).prevPoint = null;
      }

      return var1;
   }

   public int size() {
      return this.list.size();
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public void ensureCapacity(int var1) {
      this.list.ensureCapacity(var1);
   }

   public class TrailPoint {
      public final TrailVector vector;
      public final long spawnTime;
      private TrailPoint prevPoint;
      private TrailPoint nextPoint;
      private Point2D.Float drawPos1;
      private Point2D.Float drawPos2;

      private TrailPoint(TrailVector var2, long var3) {
         this.vector = var2;
         this.spawnTime = var3;
      }

      private void updatePrev(TrailPoint var1) {
         this.prevPoint = var1;
         this.drawPos1 = this.getDrawPoint1(this.vector.thickness);
         this.drawPos2 = this.getDrawPoint2(this.vector.thickness);
         if (TrailPointList.this.trail.smoothCorners && var1 != null && (new Line2D.Float(this.drawPos1, this.drawPos2)).intersectsLine(new Line2D.Float(var1.drawPos1, var1.drawPos2))) {
            float var2 = GameMath.getAngleDifference(var1.vector.getAngle(), this.vector.getAngle());
            if (var2 == 0.0F) {
               this.drawPos1 = var1.drawPos1;
               this.drawPos2 = var1.drawPos2;
            } else if (var2 > 0.0F) {
               this.drawPos1 = var1.drawPos1;
            } else {
               this.drawPos2 = var1.drawPos2;
            }
         }

      }

      private void updateNext(TrailPoint var1) {
         this.nextPoint = var1;
      }

      private Point2D.Float getDrawPoint1(float var1) {
         return GameMath.getPerpendicularPoint(this.vector.pos, var1 / 2.0F, this.vector.dx, this.vector.dy);
      }

      private Point2D.Float getDrawPoint2(float var1) {
         return GameMath.getPerpendicularPoint(this.vector.pos, -var1 / 2.0F, this.vector.dx, this.vector.dy);
      }

      public TrailPoint getPrevPoint() {
         return this.prevPoint;
      }

      public TrailPoint getNextPoint() {
         return this.nextPoint;
      }

      public Point2D.Float getDrawPos1() {
         return this.drawPos1;
      }

      public Point2D.Float getDrawPos2() {
         return this.drawPos2;
      }

      // $FF: synthetic method
      TrailPoint(TrailVector var2, long var3, Object var5) {
         this(var2, var3);
      }
   }
}
