package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class FlyToRandomPositionAttackStage<T extends Mob> extends AINode<T> implements AttackStageInterface<T> {
   public int baseDistance;
   public boolean isRunningWhileMoving;
   public String targetKey;

   public FlyToRandomPositionAttackStage(boolean var1, int var2, String var3) {
      this.isRunningWhileMoving = var1;
      this.baseDistance = var2;
      this.targetKey = var3;
   }

   public FlyToRandomPositionAttackStage(boolean var1, int var2) {
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

      Point2D.Float var5 = new Point2D.Float(var1.x, var1.y);

      for(int var6 = 0; var6 < 10; ++var6) {
         int var7 = GameRandom.globalRandom.nextInt(360);
         Point2D.Float var8 = GameMath.getAngleDir((float)var7);
         var5 = new Point2D.Float(var4.x + var8.x * (float)this.baseDistance, var4.y + var8.y * (float)this.baseDistance);
         if (var1.getDistance(var5.x, var5.y) >= (float)this.baseDistance / 4.0F) {
            break;
         }
      }

      var2.mover.directMoveTo(this, (int)var5.x, (int)var5.y);
   }

   public void onEnded(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      return this.isRunningWhileMoving && var2.mover.isMoving() ? AINodeResult.RUNNING : AINodeResult.SUCCESS;
   }
}
