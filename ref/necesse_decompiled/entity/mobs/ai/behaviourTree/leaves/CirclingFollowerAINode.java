package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public abstract class CirclingFollowerAINode<T extends Mob> extends AINode<T> {
   public int teleportDistance;
   public int teleportToAccuracy;
   public int circlingRange;
   private Point currentTargetOffset = new Point(0, 0);
   private int lastAngle;

   public CirclingFollowerAINode(int var1, int var2) {
      this.teleportDistance = var1;
      this.circlingRange = var2;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = this.getFollowingMob(var1);
      if (var3 != null && var3.isSamePlace(var1)) {
         Point var4 = new Point(var3.getX() + this.currentTargetOffset.x, var3.getY() + this.currentTargetOffset.y);
         float var5 = var1.getDistance(var3.x, var3.y);
         float var6 = (float)(new Point(var4.x, var4.y)).distance(0.0, 0.0);
         float var7 = var1.getDistance(var3.x + (float)var4.x, var3.y + (float)var4.y);
         if (this.teleportDistance > 0 && var5 > (float)this.teleportDistance && var7 > (float)this.teleportDistance + var6) {
            this.teleportTo(var1, var3, this.teleportToAccuracy);
         } else {
            if (!var2.mover.isMoving() || var1.hasArrivedAtTarget()) {
               this.findNewPosition(var1);
            }

            var2.mover.setCustomMovement(this, new MobMovementRelative(var3, (float)this.currentTargetOffset.x, (float)this.currentTargetOffset.y, false, false));
         }

         return AINodeResult.SUCCESS;
      } else {
         return AINodeResult.FAILURE;
      }
   }

   public void findNewPosition(T var1) {
      int var2 = this.lastAngle += GameRandom.globalRandom.getIntBetween(160, 240);
      float var3 = (float)Math.cos(Math.toRadians((double)var2));
      float var4 = (float)Math.sin(Math.toRadians((double)var2));
      this.currentTargetOffset = new Point((int)(var3 * (float)this.circlingRange), (int)(var4 * (float)this.circlingRange));
   }

   public void teleportTo(T var1, Mob var2, int var3) {
      FollowerAINode.teleportCloseTo(var1, var2, var3);
      this.getBlackboard().mover.stopMoving(var1);
      var1.sendMovementPacket(true);
   }

   public abstract Mob getFollowingMob(T var1);
}
