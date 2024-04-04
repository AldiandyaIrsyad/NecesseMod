package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class CirclingChaserAINode<T extends Mob> extends AINode<T> {
   public String targetKey = "currentTarget";
   public String chaserTargetKey = "chaserTarget";
   public int circlingRange;
   public int nextAngleOffset;
   private Point currentTargetOffset = new Point(0, 0);
   private int lastAngle;

   public CirclingChaserAINode(int var1, int var2) {
      this.circlingRange = var1;
      this.nextAngleOffset = var2;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
      var2.put(this.chaserTargetKey, (Object)null);
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.targetKey);
      if (var3 != null && var3.isSamePlace(var1)) {
         if (!var2.mover.isMoving() || var1.hasArrivedAtTarget()) {
            this.findNewPosition(var1);
         }

         var2.mover.setCustomMovement(this, new MobMovementRelative(var3, (float)this.currentTargetOffset.x, (float)this.currentTargetOffset.y, false, false));
         var2.put(this.chaserTargetKey, var3);
         return AINodeResult.SUCCESS;
      } else {
         return AINodeResult.FAILURE;
      }
   }

   public void findNewPosition(T var1) {
      int var2 = this.lastAngle += GameRandom.globalRandom.getIntOffset(180, this.nextAngleOffset);
      float var3 = (float)Math.cos(Math.toRadians((double)var2));
      float var4 = (float)Math.sin(Math.toRadians((double)var2));
      this.currentTargetOffset = new Point((int)(var3 * (float)this.circlingRange), (int)(var4 * (float)this.circlingRange));
   }
}
