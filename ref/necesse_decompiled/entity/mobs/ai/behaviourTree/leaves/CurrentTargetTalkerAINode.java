package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public abstract class CurrentTargetTalkerAINode<T extends Mob> extends AINode<T> {
   public String chaserTargetKey;
   public boolean onlyTalkOnce;
   public int talkCooldown;
   public Mob lastTarget;
   public long lastTalkTime;

   public CurrentTargetTalkerAINode(String var1, boolean var2, int var3) {
      this.chaserTargetKey = var1;
      this.onlyTalkOnce = var2;
      this.talkCooldown = var3;
   }

   public CurrentTargetTalkerAINode(boolean var1, int var2) {
      this("chaserTarget", var1, var2);
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      Mob var3 = (Mob)var2.getObject(Mob.class, this.chaserTargetKey);

      try {
         AINodeResult var4;
         if (var3 != null) {
            if (this.onlyTalkOnce) {
               if (var3 != this.lastTarget && this.lastTalkTime + (long)this.talkCooldown < var1.getWorldEntity().getTime()) {
                  this.talk(var1, var3);
                  this.lastTalkTime = var1.getWorldEntity().getTime();
                  var4 = AINodeResult.SUCCESS;
                  return var4;
               }
            } else if (this.lastTalkTime + (long)this.talkCooldown < var1.getWorldEntity().getTime()) {
               this.talk(var1, var3);
               this.lastTalkTime = var1.getWorldEntity().getTime();
               var4 = AINodeResult.SUCCESS;
               return var4;
            }
         }

         var4 = AINodeResult.FAILURE;
         return var4;
      } finally {
         this.lastTarget = var3;
      }
   }

   public abstract void talk(T var1, Mob var2);
}
