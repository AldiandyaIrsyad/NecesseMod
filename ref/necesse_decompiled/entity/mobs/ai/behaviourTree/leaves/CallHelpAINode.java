package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;

public class CallHelpAINode<T extends Mob> extends AINode<T> {
   public String currentTargetKey = "currentTarget";
   public String chaserTargetKey = "chaserTarget";
   public final String eventType;
   public int tileRange;
   public int callCooldown;
   public boolean recursive = false;
   public Mob calledTarget;
   public long lastCallTime;

   public CallHelpAINode(String var1, int var2, int var3) {
      this.eventType = var1;
      this.tileRange = var2;
      this.callCooldown = var3;
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      var3.onEvent(this.eventType, (var3x) -> {
         if (var3x instanceof AICallHelpEvent) {
            Mob var4 = (Mob)var3.getObject(Mob.class, this.chaserTargetKey);
            if (var4 == null) {
               Mob var5 = ((AICallHelpEvent)var3x).target;
               this.calledTarget = var5;
               this.lastCallTime = var2.getWorldEntity().getTime();
               var3.put(this.currentTargetKey, var5);
            }
         }

      });
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (this.calledTarget != null && (!this.calledTarget.isSamePlace(var1) || this.calledTarget.removed())) {
         this.calledTarget = null;
      }

      Mob var3 = (Mob)var2.getObject(Mob.class, this.chaserTargetKey);
      if (var3 != null) {
         if ((this.recursive || var3 != this.calledTarget) && this.lastCallTime + (long)this.callCooldown < var1.getWorldEntity().getTime()) {
            var1.getLevel().entityManager.mobs.getInRegionByTileRange(var1.getX() / 32, var1.getY() / 32, this.tileRange).forEach((var2x) -> {
               var2x.ai.blackboard.submitEvent(this.eventType, new AICallHelpEvent(var3));
            });
            this.lastCallTime = var1.getWorldEntity().getTime();
            return AINodeResult.SUCCESS;
         }
      } else {
         this.calledTarget = null;
      }

      return AINodeResult.FAILURE;
   }

   public static class AICallHelpEvent extends AIEvent {
      public final Mob target;

      public AICallHelpEvent(Mob var1) {
         this.target = var1;
      }
   }
}
