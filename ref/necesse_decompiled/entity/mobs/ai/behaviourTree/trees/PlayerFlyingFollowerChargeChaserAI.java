package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerBaseSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerFocusTargetSetterAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFlyingFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.SummonTargetFinderAINode;
import necesse.entity.mobs.mobMovement.MobMovementRelative;

public class PlayerFlyingFollowerChargeChaserAI<T extends Mob> extends SelectorAINode<T> {
   protected int lastTarget;

   public PlayerFlyingFollowerChargeChaserAI(int var1, CooldownAttackTargetAINode.CooldownTimer var2, final int var3, final int var4, int var5, int var6) {
      SequenceAINode var7 = new SequenceAINode();
      var7.addChild(new FollowerBaseSetterAINode());
      var7.addChild(new FollowerFocusTargetSetterAINode());
      SummonTargetFinderAINode var8 = new SummonTargetFinderAINode(var1);
      var7.addChild(var8);
      final CooldownAttackTargetAINode var9 = new CooldownAttackTargetAINode<T>(var2, var3, -1) {
         public boolean attackTarget(T var1, Mob var2) {
            Point2D.Float var3;
            if (PlayerFlyingFollowerChargeChaserAI.this.lastTarget != var2.getUniqueID()) {
               var3 = GameMath.getAngleDir((float)GameRandom.globalRandom.nextInt(360));
               PlayerFlyingFollowerChargeChaserAI.this.lastTarget = var2.getUniqueID();
            } else {
               var3 = GameMath.normalize(var1.x - var2.x, var1.y - var2.y);
            }

            this.getBlackboard().mover.setCustomMovement(this, new MobMovementRelative(var2, -var3.x * (float)var4, -var3.y * (float)var4));
            return true;
         }

         public boolean canAttackTarget(T var1, Mob var2) {
            return true;
         }
      };
      var7.addChild(var9);
      this.addChild(var7);
      this.addChild(new AINode<T>() {
         protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3x) {
         }

         public void init(T var1, Blackboard<T> var2) {
         }

         public AINodeResult tick(T var1, Blackboard<T> var2) {
            if (PlayerFlyingFollowerChargeChaserAI.this.lastTarget != -1) {
               PlayerFlyingFollowerChargeChaserAI.this.lastTarget = -1;
               var9.attackTimer = (long)GameRandom.globalRandom.nextInt(var3);
            }

            return AINodeResult.FAILURE;
         }
      });
      this.addChild(new PlayerFlyingFollowerAINode(var5, var6));
   }
}
