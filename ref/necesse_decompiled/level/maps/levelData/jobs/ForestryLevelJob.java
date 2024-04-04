package necesse.level.maps.levelData.jobs;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.entity.TileDamageResult;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.activeJob.MineObjectActiveJob;
import necesse.level.gameObject.ForestryJobObject;
import necesse.level.gameObject.ForestrySaplingObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.zones.SettlementForestryZone;

public class ForestryLevelJob extends MineObjectLevelJob {
   public SettlementForestryZone zone;
   public boolean destroySapling;

   public ForestryLevelJob(int var1, int var2, SettlementForestryZone var3, boolean var4) {
      super(var1, var2);
      this.zone = var3;
      this.destroySapling = var4;
   }

   public ForestryLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean isValid() {
      return !this.zone.isRemoved() && (this.destroySapling || this.zone.isChoppingAllowed()) ? super.isValid() : false;
   }

   public boolean isValidObject(LevelObject var1) {
      if (!this.zone.containsTile(this.tileX, this.tileY)) {
         return false;
      } else {
         return this.destroySapling && var1.object instanceof ForestrySaplingObject && !this.zone.replantChoppedDownTrees() ? true : var1.object instanceof ForestryJobObject;
      }
   }

   public boolean shouldSave() {
      return false;
   }

   public static <T extends ForestryLevelJob> JobSequence getJobSequence(EntityJobWorker var0, final boolean var1, final FoundJob<T> var2) {
      GameObject var3 = ((ForestryLevelJob)var2.job).getObject().object;
      LocalMessage var4 = new LocalMessage("activities", "chopping", "target", var3.getLocalization());
      final GameLinkedListJobSequence var5 = new GameLinkedListJobSequence(var4);
      var5.add(new MineObjectActiveJob(var0, var2.priority, ((ForestryLevelJob)var2.job).tileX, ((ForestryLevelJob)var2.job).tileY, (var1x) -> {
         return !((ForestryLevelJob)var2.job).isRemoved() && ((ForestryLevelJob)var2.job).isValidObject(var1x);
      }, ((ForestryLevelJob)var2.job).reservable, "ironaxe", 20, 500, 0) {
         public void onObjectDestroyed(TileDamageResult var1x) {
            this.addItemPickupJobs(var2.priority, var1x, var5);
            PlantSaplingLevelJob var2x = ((ForestryLevelJob)var2.job).zone.getNewPlantJob(this.tileX, this.tileY, var1x.levelObject.object);
            if (var2x != null) {
               int var3 = LevelJobRegistry.getJobTypeID(PlantSaplingLevelJob.class);
               var2x.reservable.reserve(this.worker.getMobWorker());
               var5.addLast(var2x.getActiveJob(this.worker, this.worker.getJobTypeHandler().getPriority(var3), var1));
            }

            ((ForestryLevelJob)var2.job).remove();
         }
      });
      return var5;
   }
}
