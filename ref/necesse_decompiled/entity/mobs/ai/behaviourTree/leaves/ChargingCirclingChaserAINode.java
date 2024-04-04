package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.geom.Point2D;
import necesse.engine.network.NetworkClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class ChargingCirclingChaserAINode<T extends Mob> extends AINode<T> {
   public String targetKey = "currentTarget";
   public String chaserTargetKey = "chaserTarget";
   public int circlingRange;
   public int nextAngleOffset;
   public boolean backOffOnReset = true;
   public int circlingTicks = 1;
   protected Mob chargingTarget;
   protected int nextAttack;
   protected float circlingDirection = 1.0F;
   private int startMoveAccuracy;

   public ChargingCirclingChaserAINode(int var1, int var2) {
      this.circlingRange = var1;
      this.nextAngleOffset = var2;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      this.startMoveAccuracy = var2.moveAccuracy;
   }

   public void init(T var1, Blackboard<T> var2) {
      var2.put(this.chaserTargetKey, (Object)null);
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.targetKey);
      if (var3 != null && var3.isSamePlace(var1)) {
         float var4;
         float var5;
         if (this.chargingTarget != null) {
            if (!this.chargingTarget.removed() && this.chargingTarget.canBeTargeted(var1, (NetworkClient)null)) {
               var2.mover.setMobTarget(this, this.chargingTarget);
               if (var1.getDistance(this.chargingTarget) < (float)this.circlingRange / 3.0F) {
                  var4 = GameMath.getAngle(new Point2D.Float(var1.dx, var1.dy));
                  var5 = GameMath.getAngle(new Point2D.Float(var3.x - var1.x, var3.y - var1.y));
                  float var6 = GameMath.getAngleDifference(var4, var5);
                  if (Math.abs(var6) >= 40.0F || var1.dx == 0.0F && var1.dy == 0.0F) {
                     this.startCircling(var1, var2, var3, this.circlingTicks);
                  }
               }
            } else {
               this.startCircling(var1, var2, var3, this.circlingTicks);
            }

            var2.put(this.chaserTargetKey, this.chargingTarget);
         } else {
            if (!var2.mover.isCurrentlyMovingFor(this) || var1.hasArrivedAtTarget()) {
               --this.nextAttack;
               if (this.nextAttack <= 0) {
                  this.startCharge(var1, var2, var3);
               } else {
                  var4 = GameMath.getAngle(new Point2D.Float(var1.x - var3.x, var1.y - var3.y));
                  var5 = var4 + GameRandom.globalRandom.getFloatBetween(0.0F, (float)this.nextAngleOffset * this.circlingDirection);
                  this.findNewPosition(var5, var1, var2, var3);
               }
            }

            var2.put(this.chaserTargetKey, var3);
         }

         return AINodeResult.SUCCESS;
      } else {
         this.chargingTarget = null;
         return AINodeResult.FAILURE;
      }
   }

   public void startCharge(T var1, Blackboard<T> var2, Mob var3) {
      var1.moveAccuracy = 5;
      var2.mover.setMobTarget(this, var3);
      this.chargingTarget = var3;
   }

   public void startCircling(T var1, Blackboard<T> var2, Mob var3, int var4) {
      this.startCircling(var1, var2, var3, var4, 0.0F);
   }

   public void startCircling(T var1, Blackboard<T> var2, Mob var3, int var4, float var5) {
      float var6;
      if (this.backOffOnReset) {
         var6 = GameMath.getAngle(new Point2D.Float(var1.x - var3.x, var1.y - var3.y));
      } else {
         var6 = GameMath.getAngle(new Point2D.Float(var3.x - var1.x, var3.y - var1.y));
      }

      this.findNewPosition((float)((int)var6), var1, var2, var3);
      this.chargingTarget = null;
      this.nextAttack = var4;
      this.circlingDirection = Math.signum(var5);
      if (this.circlingDirection == 0.0F) {
         float var7 = GameMath.getAngle(new Point2D.Float(var1.dx, var1.dy));
         float var8 = GameMath.getAngleDifference(var6, var7);
         this.circlingDirection = var8 > 0.0F ? 1.0F : -1.0F;
      }

   }

   public void findNewPosition(float var1, T var2, Blackboard<T> var3, Mob var4) {
      float var5 = (float)Math.cos(Math.toRadians((double)var1));
      float var6 = (float)Math.sin(Math.toRadians((double)var1));
      var2.moveAccuracy = this.startMoveAccuracy;
      var3.mover.directMoveTo(this, (int)(var4.x + var5 * (float)this.circlingRange), (int)(var4.y + var6 * (float)this.circlingRange));
   }

   public void fixMoveAccuracy() {
      this.mob().moveAccuracy = this.startMoveAccuracy;
   }
}
