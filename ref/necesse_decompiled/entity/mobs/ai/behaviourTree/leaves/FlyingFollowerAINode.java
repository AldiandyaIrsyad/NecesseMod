package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.network.server.FollowerPosition;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.mobMovement.MobMovement;

public abstract class FlyingFollowerAINode<T extends Mob> extends AINode<T> {
   private FollowerPosition targetPoint = null;
   public int teleportDistance;
   public int stoppingDistance;
   public int teleportToAccuracy = 1;

   public FlyingFollowerAINode(int var1, int var2) {
      this.teleportDistance = var1;
      this.stoppingDistance = var2;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = this.getFollowingMob(var1);
      if (var3 != null && var3.isSamePlace(var1)) {
         ServerClient var4 = var1.getFollowingServerClient();
         this.targetPoint = var4 == null ? null : var4.getFollowerPos(var1, var3, this.targetPoint);
         FollowerPosition var5 = this.targetPoint != null ? this.targetPoint : new FollowerPosition(0, 0);
         float var6 = var1.getDistance(var3.x, var3.y);
         float var7 = (float)(new Point(var5.x, var5.y)).distance(0.0, 0.0);
         float var8 = var1.getDistance(var3.x + (float)var5.x, var3.y + (float)var5.y);
         if (this.teleportDistance > 0 && var6 > (float)this.teleportDistance && var8 > (float)this.teleportDistance + var7) {
            this.teleportTo(var1, var3, this.teleportToAccuracy);
         } else {
            var2.mover.setCustomMovement(this, (MobMovement)var5.movementGetter.apply(var3));
            if (this.targetPoint == null && var8 < (float)this.stoppingDistance) {
               var2.mover.stopMoving(var1);
            }
         }

         return AINodeResult.SUCCESS;
      } else {
         return AINodeResult.FAILURE;
      }
   }

   public void teleportTo(T var1, Mob var2, int var3) {
      FollowerAINode.teleportCloseTo(var1, var2, var3);
      this.getBlackboard().mover.stopMoving(var1);
      var1.sendMovementPacket(true);
   }

   public abstract Mob getFollowingMob(T var1);
}
