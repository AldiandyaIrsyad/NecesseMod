package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.function.BiPredicate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;

public class MoveToRandomTileAttackStage<T extends Mob> extends MoveTaskAINode<T> implements AttackStageInterface<T> {
   public int baseDistance;
   public boolean isRunningWhileMoving;
   public String targetKey;

   public MoveToRandomTileAttackStage(boolean var1, int var2, String var3) {
      this.isRunningWhileMoving = var1;
      this.baseDistance = var2;
      this.targetKey = var3;
   }

   public MoveToRandomTileAttackStage(boolean var1, int var2) {
      this(var1, var2, "currentTarget");
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public void onStarted(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.targetKey);
      Point2D.Float var4 = new Point2D.Float(var1.x, var1.y);
      if (var3 != null) {
         var4 = new Point2D.Float(var3.x, var3.y);
      }

      Point var5 = var1.getPathMoveOffset();
      Point var6 = new Point(var1.getX() / 32, var1.getY() / 32);

      for(int var7 = 0; var7 < 20; ++var7) {
         int var8 = GameRandom.globalRandom.nextInt(360);
         Point2D.Float var9 = GameMath.getAngleDir((float)var8);
         var6 = new Point((int)(var4.x + var9.x * (float)this.baseDistance) / 32, (int)(var4.y + var9.y * (float)this.baseDistance) / 32);
         if (!var1.collidesWith(var1.getLevel(), var6.x * 32 + var5.x, var6.y * 32 + var5.y) && var1.getDistance((float)(var6.x * 32 + var5.x), (float)(var6.y * 32 + var5.y)) >= (float)this.baseDistance / 4.0F && var1.estimateCanMoveTo(var6.x, var6.y, false)) {
            break;
         }
      }

      this.moveToTileTask(var6.x, var6.y, (BiPredicate)null, (var1x) -> {
         if (var1x.moveIfWithin(-1, -1, (Runnable)null)) {
            return this.isRunningWhileMoving ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
         } else {
            return AINodeResult.FAILURE;
         }
      });
   }

   public void onEnded(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tickNode(T var1, Blackboard<T> var2) {
      return this.isRunningWhileMoving && var2.mover.isMoving() ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
   }
}
