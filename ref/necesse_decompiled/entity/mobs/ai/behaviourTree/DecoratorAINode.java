package necesse.entity.mobs.ai.behaviourTree;

import java.util.Collections;
import necesse.entity.mobs.Mob;

public abstract class DecoratorAINode<T extends Mob> extends AINode<T> {
   private boolean rootSet;
   private AINode<T> child;

   public DecoratorAINode(AINode<T> var1) {
      this.child = var1;
   }

   public AINode<T> getChild() {
      return this.child;
   }

   protected void setChild(AINode<T> var1) {
      this.child = var1;
      if (this.rootSet && var1 != null) {
         var1.setChildRoot(this);
      }

   }

   public void onInterruptRunning(T var1, Blackboard<T> var2) {
      if (this.child != null) {
         this.child.onInterruptRunning(var1, var2);
      }

   }

   protected void onForceSetRunning(AINode<T> var1) {
      if (this.child != null) {
         this.child.onForceSetRunning(var1);
      }

   }

   public void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      this.rootSet = true;
      if (this.child != null) {
         this.child.setChildRoot(this);
      }

   }

   public void init(T var1, Blackboard<T> var2) {
      if (this.child != null) {
         this.child.lastResult = null;
         this.child.init(var1, var2);
      }

   }

   public Iterable<AINode<T>> debugChildren() {
      return this.child == null ? Collections.emptyList() : Collections.singletonList(this.child);
   }

   public final AINodeResult tick(T var1, Blackboard<T> var2) {
      return this.tickChild(this.child, var1, var2);
   }

   public abstract AINodeResult tickChild(AINode<T> var1, T var2, Blackboard<T> var3);
}
