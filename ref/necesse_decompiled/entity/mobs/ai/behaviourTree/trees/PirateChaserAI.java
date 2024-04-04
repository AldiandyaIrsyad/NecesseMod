package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.hostile.pirates.PirateMob;

public class PirateChaserAI<T extends PirateMob> extends SequenceAINode<T> {
   public final CooldownAttackTargetAINode<T> shootAtTargetNode;
   public final TargetFinderAINode<T> targetFinderNode;
   public final ChaserAINode<T> chaserNode;

   public PirateChaserAI(int var1, int var2, int var3, int var4) {
      if (var1 > 0) {
         this.addChild(this.shootAtTargetNode = new CooldownAttackTargetAINode<T>(CooldownAttackTargetAINode.CooldownTimer.TICK, var2, var1) {
            public boolean attackTarget(T var1, Mob var2) {
               if (var1.canAttack()) {
                  var1.startShootingAbility.runAndSend(var2);
                  return true;
               } else {
                  return false;
               }
            }

            // $FF: synthetic method
            // $FF: bridge method
            public boolean attackTarget(Mob var1, Mob var2) {
               return this.attackTarget((PirateMob)var1, var2);
            }
         });
      } else {
         this.shootAtTargetNode = null;
      }

      TargetFinderDistance var5 = new TargetFinderDistance(var4);
      var5.targetLostAddedDistance = var4 * 2;
      this.addChild(this.targetFinderNode = new TargetFinderAINode<T>(var5) {
         public GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3) {
            return TargetFinderAINode.streamPlayersAndHumans(var1, var2, var3);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public GameAreaStream streamPossibleTargets(Mob var1, Point var2, TargetFinderDistance var3) {
            return this.streamPossibleTargets((PirateMob)var1, var2, var3);
         }
      });
      this.addChild(this.chaserNode = new ChaserAINode<T>(var3, false, true) {
         public boolean attackTarget(T var1, Mob var2) {
            if (var1.canAttack()) {
               var1.attack(var2.getX(), var2.getY(), false);
               var2.isServerHit(new GameDamage((float)var1.meleeDamage), var2.x - var1.x, var2.y - var1.y, 100.0F, var1);
               return true;
            } else {
               return false;
            }
         }

         // $FF: synthetic method
         // $FF: bridge method
         public boolean attackTarget(Mob var1, Mob var2) {
            return this.attackTarget((PirateMob)var1, var2);
         }
      });
   }
}
