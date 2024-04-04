package necesse.entity.mobs.ai.behaviourTree;

import java.util.ArrayList;
import java.util.Iterator;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;

public abstract class CompositeTypedAINode<T extends Mob, C extends AINode<T>> extends AINode<T> {
   protected ArrayList<C> children = new ArrayList();
   protected C runningNode;

   public CompositeTypedAINode() {
   }

   public CompositeTypedAINode<T, C> addChild(C var1) {
      this.children.add(var1);
      var1.setChildRoot(this);
      return this;
   }

   public CompositeTypedAINode<T, C> addChildFirst(C var1) {
      this.children.add(0, var1);
      var1.setChildRoot(this);
      return this;
   }

   public void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
      Iterator var4 = this.children.iterator();

      while(var4.hasNext()) {
         AINode var5 = (AINode)var4.next();
         var5.setChildRoot(this);
      }

   }

   public void onInterruptRunning(T var1, Blackboard<T> var2) {
      super.onInterruptRunning(var1, var2);
      this.runningNode = null;
      Iterator var3 = this.children.iterator();

      while(var3.hasNext()) {
         AINode var4 = (AINode)var3.next();
         var4.onInterruptRunning(var1, var2);
      }

   }

   protected void onForceSetRunning(AINode<T> var1) {
      if (this.children.contains(var1)) {
         this.runningNode = var1;
      }

      Iterator var2 = this.children.iterator();

      while(var2.hasNext()) {
         AINode var3 = (AINode)var2.next();
         var3.onForceSetRunning(var1);
      }

   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (this.runningNode != null) {
         AINodeResult var6 = this.runningNode.lastResult = this.runningNode.tick(var1, var2);
         if (var6 == AINodeResult.RUNNING) {
            return AINodeResult.RUNNING;
         } else {
            int var7 = this.children.indexOf(this.runningNode) + 1;
            AINode var5 = this.runningNode;
            this.runningNode = null;
            return this.tickChildren(var5, var6, this.children.subList(var7, this.children.size()), var1, var2);
         }
      } else {
         Iterator var3 = this.children.iterator();

         while(var3.hasNext()) {
            AINode var4 = (AINode)var3.next();
            var4.lastResult = null;
            var4.init(var1, var2);
         }

         return this.tickChildren((AINode)null, (AINodeResult)null, this.children, var1, var2);
      }
   }

   protected abstract AINodeResult tickChildren(C var1, AINodeResult var2, Iterable<C> var3, T var4, Blackboard<T> var5);

   public Iterable<AINode<T>> debugChildren() {
      return this.children;
   }

   public void addDebugActions(SelectionFloatMenu var1) {
      var1.add("Clear running node", () -> {
         this.runningNode = null;
         var1.remove();
      });
   }
}
