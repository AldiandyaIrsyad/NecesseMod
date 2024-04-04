package necesse.entity.mobs.job;

import java.awt.Point;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public interface EntityJobWorker {
   default Mob getMobWorker() {
      return (Mob)this;
   }

   default Level getLevel() {
      return this.getMobWorker().getLevel();
   }

   default Point getJobSearchTile() {
      Mob var1 = this.getMobWorker();
      return new Point(var1.getX() / 32, var1.getY() / 32);
   }

   default JobSequence findJob() {
      JobTypeHandler var1 = this.getJobTypeHandler();
      long var2 = this.getMobWorker().getWorldEntity().getTime();
      if (var1.isOnGlobalCooldown(var2)) {
         return null;
      } else {
         FoundJob var4 = (new JobFinder(this)).findJob();
         if (var1.resetPrioritizeNextJobIfFound) {
            var1.prioritizeNextJobID = -1;
         }

         if (var4 != null) {
            var4.handler.startCooldown(var2);
            this.useWorkBreakBuffer(var4.handler.nextWorkBreakBufferUsage());
            if (var4.job.prioritizeForSameJobAgain()) {
               var1.lastPerformedJobID = var4.job.getID();
            } else {
               var1.lastPerformedJobID = -1;
            }

            return var4.getSequence();
         } else {
            int var5 = GameRandom.globalRandom.getIntBetween(5000, 10000);
            var1.startGlobalCooldown(var2, var5);
            var1.lastPerformedJobID = -1;
            var1.prioritizeNextJobID = -1;
            return null;
         }
      }
   }

   default void onHitCausedFailed(boolean var1) {
      int var2 = GameRandom.globalRandom.getIntBetween(5000, 10000);
      if (!var1) {
         var2 /= 2;
      }

      this.getJobTypeHandler().startGlobalCooldown(this.getMobWorker().getWorldEntity().getTime(), var2);
   }

   default void onTargetFoundCausedFailed(Mob var1) {
      int var2 = GameRandom.globalRandom.getIntBetween(5000, 10000);
      this.getJobTypeHandler().startGlobalCooldown(this.getMobWorker().getWorldEntity().getTime(), var2);
   }

   boolean estimateCanMoveTo(int var1, int var2, boolean var3);

   ZoneTester getJobRestrictZone();

   JobTypeHandler getJobTypeHandler();

   WorkInventory getWorkInventory();

   void showPickupAnimation(int var1, int var2, Item var3, int var4);

   void showPlaceAnimation(int var1, int var2, Item var3, int var4);

   void showWorkAnimation(int var1, int var2, Item var3, int var4);

   void showAttackAnimation(int var1, int var2, Item var3, int var4);

   boolean hasActiveJob();

   boolean isInWorkAnimation();

   boolean isJobCancelled();

   void resetJobCancelled();

   int getWorkBreakBuffer();

   boolean isOnWorkBreak();

   void useWorkBreakBuffer(int var1);

   boolean isOnStrike();

   void attemptStartStrike(boolean var1, boolean var2);

   default void setPrioritizeNextJob(int var1, boolean var2) {
      JobTypeHandler var3 = this.getJobTypeHandler();
      var3.prioritizeNextJobID = var1;
      var3.resetPrioritizeNextJobIfFound = var2;
   }

   default void setPrioritizeNextJob(Class<? extends LevelJob> var1, boolean var2) {
      this.setPrioritizeNextJob(LevelJobRegistry.getJobID(var1), var2);
   }
}
