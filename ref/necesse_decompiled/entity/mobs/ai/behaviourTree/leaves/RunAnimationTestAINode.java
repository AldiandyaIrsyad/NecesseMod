package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.mobMovement.MobMovementLevelPos;

public class RunAnimationTestAINode<T extends Mob> extends AINode<T> {
   private int radius;
   private int baseX;
   private int baseY;
   private int currentPos = 0;
   private Point[] positions = new Point[]{new Point(-1, -2), new Point(1, -2), new Point(2, -1), new Point(2, 1), new Point(1, 2), new Point(-1, 2), new Point(-2, 1), new Point(-2, -1)};

   public RunAnimationTestAINode(int var1) {
      this.radius = var1;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      this.baseX = var2.getX();
      this.baseY = var2.getY();
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (var1.hasArrivedAtTarget()) {
         this.currentPos = (this.currentPos + 1) % this.positions.length;
         Point var3 = this.positions[this.currentPos];
         int var4 = this.baseX + var3.x * this.radius / 2;
         int var5 = this.baseY + var3.y * this.radius / 2;
         var1.setMovement(new MobMovementLevelPos((float)var4, (float)var5));
      }

      return AINodeResult.SUCCESS;
   }
}
