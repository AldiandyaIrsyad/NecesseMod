package necesse.level.maps.levelData.jobs;

import java.util.Objects;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.entity.TileDamageResult;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.MineObjectActiveJob;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SeedObject;
import necesse.level.maps.LevelObject;

public class HarvestCropLevelJob extends MineObjectLevelJob {
   public HarvestCropLevelJob(int var1, int var2) {
      super(var1, var2);
   }

   public HarvestCropLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean isValidObject(LevelObject var1) {
      return var1.object.isSeed && ((SeedObject)var1.object).isLastStage();
   }

   public boolean shouldSave() {
      return false;
   }

   public static <T extends HarvestCropLevelJob> JobSequence getJobSequence(EntityJobWorker var0, final boolean var1, final FoundJob<T> var2) {
      GameObject var3 = ((HarvestCropLevelJob)var2.job).getObject().object;
      LocalMessage var4 = new LocalMessage("activities", "harvesting", "target", var3.getLocalization());
      final GameLinkedListJobSequence var5 = new GameLinkedListJobSequence(var4);
      JobTypeHandler.TypePriority var10004 = var2.priority;
      int var10005 = ((HarvestCropLevelJob)var2.job).tileX;
      int var10006 = ((HarvestCropLevelJob)var2.job).tileY;
      HarvestCropLevelJob var10007 = (HarvestCropLevelJob)var2.job;
      Objects.requireNonNull(var10007);
      var5.add(new MineObjectActiveJob(var0, var10004, var10005, var10006, var10007::isValidObject, ((HarvestCropLevelJob)var2.job).reservable, "sickle", 100, 500, 0) {
         public void onObjectDestroyed(TileDamageResult var1x) {
            this.addItemPickupJobs(var2.priority, var1x, var5);
            if (var1x.levelObject.object.isSeed) {
               int var2x = ((SeedObject)var1x.levelObject.object).stageIDs[0];
               if (var2x != -1) {
                  PlantCropLevelJob var3 = new PlantCropLevelJob(this.tileX, this.tileY, var2x);
                  var3.reservable.reserve(this.worker.getMobWorker());
                  if (JobsLevelData.addJob(this.getLevel(), var3) == var3) {
                     int var4 = LevelJobRegistry.getJobTypeID(PlantCropLevelJob.class);
                     var5.addLast(var3.getActiveJob(this.worker, this.worker.getJobTypeHandler().getPriority(var4), var1));
                  }
               }
            }

            ((HarvestCropLevelJob)var2.job).remove();
         }
      });
      return var5;
   }
}
