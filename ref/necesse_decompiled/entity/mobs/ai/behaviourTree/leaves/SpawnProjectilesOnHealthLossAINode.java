package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public abstract class SpawnProjectilesOnHealthLossAINode<T extends Mob> extends AINode<T> {
   public int totalProjectiles;
   public int maxPerSecond;
   public boolean isPaused;
   public int lastHealth;
   public float nextProjectile;

   public SpawnProjectilesOnHealthLossAINode(int var1, int var2) {
      this.totalProjectiles = var1;
      this.maxPerSecond = var2;
   }

   public SpawnProjectilesOnHealthLossAINode(int var1) {
      this(var1, 5);
   }

   protected void onRootSet(AINode<T> var1, T var2, Blackboard<T> var3) {
   }

   public void init(T var1, Blackboard<T> var2) {
   }

   public float getTotalProjectiles(T var1) {
      return (float)this.totalProjectiles;
   }

   public AINodeResult tick(T var1, Blackboard<T> var2) {
      if (!this.isPaused) {
         int var3 = var1.getHealth();
         int var4 = this.lastHealth - var3;
         this.lastHealth = var3;
         if (var4 > 0) {
            float var5 = (float)var1.getMaxHealth() / (float)var1.getMaxHealthFlat();
            float var6 = (float)var1.getMaxHealthFlat() / this.getTotalProjectiles(var1) * var5;
            float var7 = (float)var4 / var6;
            this.nextProjectile += Math.min((float)this.maxPerSecond / 20.0F, var7);
         }

         if (this.nextProjectile > 1.0F) {
            this.shootProjectile(var1);
            --this.nextProjectile;
         }
      }

      return AINodeResult.SUCCESS;
   }

   public void pause() {
      this.isPaused = true;
   }

   public void resume() {
      if (this.isPaused) {
         this.lastHealth = this.mob().getHealth();
      }
   }

   public abstract void shootProjectile(T var1);
}
