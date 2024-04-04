package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.util.Iterator;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.CompositeAINode;
import necesse.entity.mobs.ai.behaviourTree.CompositeTypedAINode;

public class AttackStageManagerNode<T extends Mob> extends CompositeAINode<T> implements AttackStageInterface<T> {
   public AINodeResult returnResult;
   public boolean allowSkippingBack;
   private boolean hasSkipToObjects;

   public AttackStageManagerNode(AINodeResult var1) {
      this.allowSkippingBack = true;
      this.hasSkipToObjects = false;
      this.returnResult = var1;
   }

   public AttackStageManagerNode() {
      this(AINodeResult.SUCCESS);
   }

   public CompositeTypedAINode<T, AINode<T>> addChild(AINode<T> var1) {
      if (var1 instanceof AttackStageSkipTo) {
         this.hasSkipToObjects = true;
      }

      return super.addChild(var1);
   }

   public CompositeTypedAINode<T, AINode<T>> addChildFirst(AINode<T> var1) {
      if (var1 instanceof AttackStageSkipTo) {
         this.hasSkipToObjects = true;
      }

      return super.addChildFirst(var1);
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (this.hasSkipToObjects) {
         int var3;
         if (this.allowSkippingBack) {
            var3 = this.runningNode == null ? this.children.size() - 1 : this.children.indexOf(this.runningNode);

            for(int var4 = 1; var4 < this.children.size(); ++var4) {
               int var5 = Math.floorMod(var3 - var4, this.children.size());
               if (this.children.get(var5) instanceof AttackStageSkipTo) {
                  AttackStageSkipTo var6 = (AttackStageSkipTo)this.children.get(var5);
                  if (var6.shouldSkipTo(var1, this.runningNode == this.children.get(Math.floorMod(var5 - 1, this.children.size())))) {
                     if (this.runningNode != null) {
                        this.runningNode.lastResult = null;
                        if (this.runningNode instanceof AttackStageInterface) {
                           ((AttackStageInterface)this.runningNode).onEnded(var1, var2);
                        }

                        this.onInterruptRunning(var1, var2);
                     }

                     this.runningNode = (AINode)this.children.get(var5);
                     if (this.runningNode instanceof AttackStageInterface) {
                        ((AttackStageInterface)this.runningNode).onStarted(var1, var2);
                     }
                     break;
                  }
               }
            }
         } else {
            for(var3 = this.children.size() - 1; var3 >= 0 && this.runningNode != this.children.get(var3); --var3) {
               if (this.children.get(var3) instanceof AttackStageSkipTo) {
                  AttackStageSkipTo var7 = (AttackStageSkipTo)this.children.get(var3);
                  if (var7.shouldSkipTo(var1, var3 > 0 && this.runningNode == this.children.get(var3 - 1))) {
                     if (this.runningNode != null) {
                        this.runningNode.lastResult = null;
                        if (this.runningNode instanceof AttackStageInterface) {
                           ((AttackStageInterface)this.runningNode).onEnded(var1, var2);
                        }

                        this.onInterruptRunning(var1, var2);
                     }

                     this.runningNode = (AINode)this.children.get(var3);
                     if (this.runningNode instanceof AttackStageInterface) {
                        ((AttackStageInterface)this.runningNode).onStarted(var1, var2);
                     }
                     break;
                  }
               }
            }
         }
      }

      return super.tick(var1, var2);
   }

   protected AINodeResult tickChildren(AINode<T> var1, AINodeResult var2, Iterable<AINode<T>> var3, T var4, Blackboard<T> var5) {
      if (var1 instanceof AttackStageInterface) {
         ((AttackStageInterface)var1).onEnded(var4, var5);
      }

      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         AINode var7 = (AINode)var6.next();
         if (var7 instanceof AttackStageInterface) {
            ((AttackStageInterface)var7).onStarted(var4, var5);
         }

         AINodeResult var8 = var7.lastResult = var7.tick(var4, var5);
         if (var8 == AINodeResult.RUNNING) {
            this.runningNode = var7;
            return AINodeResult.RUNNING;
         }

         if (var7 instanceof AttackStageInterface) {
            ((AttackStageInterface)var7).onEnded(var4, var5);
         }
      }

      return this.returnResult;
   }

   public void onStarted(T var1, Blackboard<T> var2) {
   }

   public void onEnded(T var1, Blackboard<T> var2) {
   }
}
