package necesse.entity.mobs.ai.behaviourTree;

import necesse.entity.mobs.Mob;

public class TestAINode extends AINode<Mob> {
   public String name;
   public int runningTicks;
   private int currentTick;

   public TestAINode(String var1, int var2) {
      this.name = var1;
      this.runningTicks = var2;
   }

   public TestAINode(String var1) {
      this(var1, 0);
   }

   public void onRootSet(AINode<Mob> var1, Mob var2, Blackboard<Mob> var3) {
      System.out.println(this.name + " onRootSet " + var1 + ", " + var3);
   }

   public void init(Mob var1, Blackboard<Mob> var2) {
      System.out.println(this.name + " init " + var1);
      this.currentTick = 0;
   }

   public AINodeResult tick(Mob var1, Blackboard<Mob> var2) {
      if (this.currentTick < this.runningTicks) {
         ++this.currentTick;
         System.out.println(this.name + " tick running " + this.currentTick + "/" + this.runningTicks + " " + var1);
         return AINodeResult.RUNNING;
      } else {
         System.out.println(this.name + " tick success " + var1);
         return AINodeResult.SUCCESS;
      }
   }
}
