package necesse.entity.mobs.ai.behaviourTree.util;

import java.awt.Point;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;

public class FlyingAIMover extends AIMover {
   public FlyingAIMover() {
   }

   public MoveToTileAITask moveToTileTask(AINode<?> var1, int var2, int var3, BiPredicate<Point, Point> var4, int var5, Function<MoveToTileAITask.AIPathResult, AINodeResult> var6) {
      MoveToTileAITask var7 = MoveToTileAITask.directMoveToTile(this, var1, var2, var3, var6);
      var7.runNow();
      return var7;
   }
}
