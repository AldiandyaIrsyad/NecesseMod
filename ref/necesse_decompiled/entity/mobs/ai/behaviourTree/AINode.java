package necesse.entity.mobs.ai.behaviourTree;

import java.util.Collections;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class AINode<T extends Mob> {
   public AINodeResult lastResult;
   private AINode<T> root;
   private AINode<T> parent;
   private T mob;
   private Blackboard<T> blackboard;

   public AINode() {
   }

   public final void makeRoot(T var1, Blackboard<T> var2) {
      if (this.root != null) {
         throw new IllegalStateException("Cannot make node root if already has root");
      } else {
         this.root = this;
         this.parent = null;
         this.mob = var1;
         this.blackboard = var2;
         this.onRootSet(this.root, var1, var2);
      }
   }

   protected final void setChildRoot(AINode<T> var1) {
      if (this.root == this) {
         throw new IllegalStateException("Cannot set child root on root node");
      } else {
         this.parent = var1;
         if (var1.root != null) {
            this.root = var1.root;
            this.mob = var1.mob;
            this.blackboard = var1.blackboard;
            this.onRootSet(this.root, this.mob, this.blackboard);
         }

      }
   }

   public T mob() {
      return this.mob;
   }

   public Blackboard<T> getBlackboard() {
      return this.blackboard;
   }

   protected abstract void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3);

   public abstract void init(T var1, Blackboard<T> var2);

   public abstract AINodeResult tick(T var1, Blackboard<T> var2);

   public final void interruptRunning() {
      if (this.root != null) {
         this.root.interruptRunning();
      }

   }

   protected void onInterruptRunning(T var1, Blackboard<T> var2) {
      this.lastResult = null;
   }

   protected void onForceSetRunning(AINode<T> var1) {
   }

   private void forceSetRunning(AINode<T> var1) {
      if (this.parent != null && this.parent != this) {
         this.parent.forceSetRunning(var1);
         this.onForceSetRunning(var1);
      }

   }

   public final void forceSetRunning() {
      this.forceSetRunning(this);
   }

   public Iterable<AINode<T>> debugChildren() {
      return Collections.emptyList();
   }

   public void addDebugTooltips(ListGameTooltips var1) {
   }

   public void addDebugActions(SelectionFloatMenu var1) {
   }
}
