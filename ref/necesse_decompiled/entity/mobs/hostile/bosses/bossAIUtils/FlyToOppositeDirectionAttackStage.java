package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class FlyToOppositeDirectionAttackStage<T extends Mob> extends AINode<T> implements AttackStageInterface<T> {
   public boolean isRunningWhileMoving;
   public float distance;
   public float randomAngleMaxOffset;
   public String targetKey;

   public FlyToOppositeDirectionAttackStage(boolean var1, float var2, float var3, String var4) {
      this.isRunningWhileMoving = var1;
      this.distance = var2;
      this.randomAngleMaxOffset = var3;
      this.targetKey = var4;
   }

   public FlyToOppositeDirectionAttackStage(boolean var1, float var2, float var3) {
      this(var1, var2, var3, "currentTarget");
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public void onStarted(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.targetKey);
      if (var3 != null) {
         Point2D.Float var4 = GameMath.normalize(var3.x - var1.x, var3.y - var1.y);
         float var5 = GameMath.getAngle(var4) + GameRandom.globalRandom.getFloatBetween(-this.randomAngleMaxOffset, this.randomAngleMaxOffset);
         Point2D.Float var6 = GameMath.getAngleDir(var5);
         var2.mover.directMoveTo(this, (int)(var3.x + var6.x * this.distance), (int)(var3.y + var6.y * this.distance));
      }

   }

   public void onEnded(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      return this.isRunningWhileMoving && var2.mover.isMoving() ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
   }
}
