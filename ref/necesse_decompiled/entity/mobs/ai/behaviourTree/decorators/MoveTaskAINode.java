package necesse.entity.mobs.ai.behaviourTree.decorators;

import java.awt.Point;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.util.MoveToTileAITask;

public abstract class MoveTaskAINode<T extends Mob> extends TaskAINode<T> {
   public MoveTaskAINode() {
   }

   public final AINodeResult moveToTileTask(int var1, int var2, BiPredicate<Point, Point> var3, int var4, Function<MoveToTileAITask.AIPathResult, AINodeResult> var5) {
      return this.startTask(this.getBlackboard().mover.moveToTileTask(this, var1, var2, var3, var4, var5));
   }

   public final AINodeResult moveToTileTask(int var1, int var2, BiPredicate<Point, Point> var3, Function<MoveToTileAITask.AIPathResult, AINodeResult> var4) {
      return this.startTask(this.getBlackboard().mover.moveToTileTask(this, var1, var2, var3, var4));
   }
}
