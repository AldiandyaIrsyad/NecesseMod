package necesse.entity.mobs.job;

import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Stream;
import necesse.engine.util.ComputedValue;
import necesse.entity.mobs.Mob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class JobFinder {
   public final EntityJobWorker worker;
   public final JobTypeHandler handler;
   public final Mob mob;
   protected final HashMap<Integer, ComputedValue<Object>> preSequenceComputes = new HashMap();

   public JobFinder(EntityJobWorker var1) {
      this.worker = var1;
      this.mob = var1.getMobWorker();
      this.handler = var1.getJobTypeHandler();
   }

   public Stream<FoundJob> streamFoundJobs() {
      if (this.handler == null) {
         return Stream.empty();
      } else {
         ZoneTester var1 = this.worker.getJobRestrictZone();
         return this.handler.streamJobs(this.worker).filter((var1x) -> {
            return var1x.reservable.isAvailable(this.mob);
         }).filter((var1x) -> {
            return var1x.isWithinRestrictZone(var1);
         }).map((var1x) -> {
            ComputedValue var2 = (ComputedValue)this.preSequenceComputes.compute(var1x.getID(), (var2x, var3) -> {
               if (var3 != null) {
                  return var3;
               } else {
                  JobTypeHandler.SubHandler var4 = this.handler.getJobHandler(var1x.getID());
                  return var4.getPreSequenceCompute(this.worker);
               }
            });
            return new FoundJob(this.worker, var1x, this.handler, var2);
         }).filter((var1x) -> {
            return (var1x.priority == null || !var1x.priority.disabledBySettler && !var1x.priority.disabledByPlayer) && var1x.handler.canPerform(this.worker);
         }).filter(FoundJob::isWithinWorkRange);
      }
   }

   public FoundJob<?> findJob() {
      Comparator var1 = Comparator.comparingInt((var0) -> {
         return -var0.job.getFirstPriority();
      });
      var1 = var1.thenComparingInt((var1x) -> {
         return this.handler.prioritizeNextJobID == var1x.job.getID() ? 0 : 1;
      }).thenComparingInt((var1x) -> {
         return this.handler.lastPerformedJobID == var1x.job.getID() ? 0 : 1;
      }).thenComparingInt((var0) -> {
         return var0.priority == null ? 100000 : -var0.priority.priority;
      }).thenComparingInt((var0) -> {
         return var0.priority == null ? 100000 : -var0.priority.type.getID();
      }).thenComparingInt((var0) -> {
         return -var0.job.getSameTypePriority();
      }).thenComparing((var0, var1x) -> {
         return -Integer.compare(var0.job.getSameJobPriority(), var1x.job.getSameJobPriority());
      }).thenComparingDouble(FoundJob::getDistanceFromWorker);
      return (FoundJob)this.streamFoundJobs().sorted(var1).filter(FoundJob::canMoveTo).filter((var0) -> {
         return var0.getSequence() != null;
      }).findFirst().orElse((Object)null);
   }
}
