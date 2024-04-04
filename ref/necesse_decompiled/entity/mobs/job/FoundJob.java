package necesse.entity.mobs.job;

import java.awt.Point;
import necesse.engine.GameTileRange;
import necesse.engine.tickManager.Performance;
import necesse.engine.util.ComputedValue;
import necesse.entity.mobs.Mob;
import necesse.level.maps.levelData.jobs.LevelJob;

public class FoundJob<T extends LevelJob> {
   public final EntityJobWorker worker;
   public final T job;
   public final JobTypeHandler.SubHandler<T> handler;
   public final JobTypeHandler.TypePriority priority;
   public final ComputedValue<Object> preSequenceCompute;
   private double workerDistance = -1.0;
   private boolean canMoveToComputed;
   private boolean canMoveTo;
   private boolean sequenceComputed;
   private JobSequence sequence;

   public FoundJob(EntityJobWorker var1, T var2, JobTypeHandler.SubHandler<T> var3, JobTypeHandler.TypePriority var4, ComputedValue<Object> var5) {
      this.worker = var1;
      this.job = var2;
      this.handler = var3;
      this.priority = var4;
      this.preSequenceCompute = var5;
   }

   public FoundJob(EntityJobWorker var1, T var2, JobTypeHandler var3, ComputedValue<Object> var4) {
      this.worker = var1;
      this.job = var2;
      this.handler = var3.getJobHandler(var2.getID());
      this.priority = this.handler.priority;
      this.preSequenceCompute = var4;
   }

   public double getDistanceFromWorker() {
      if (this.workerDistance < 0.0) {
         Mob var1 = this.worker.getMobWorker();
         this.workerDistance = (new Point(var1.getX(), var1.getY())).distance((double)(this.job.tileX * 32 + 16), (double)(this.job.tileY * 32 + 16));
      }

      return this.workerDistance;
   }

   public boolean isWithinWorkRange() {
      Point var1 = this.worker.getJobSearchTile();
      return ((GameTileRange)this.priority.type.tileRange.apply(this.worker.getLevel())).isWithinRange(var1, this.job.tileX, this.job.tileY);
   }

   public boolean canMoveTo() {
      if (!this.canMoveToComputed) {
         this.canMoveTo = this.worker.estimateCanMoveTo(this.job.tileX, this.job.tileY, true);
         this.canMoveToComputed = true;
      }

      return this.canMoveTo;
   }

   public JobSequence getSequence() {
      if (!this.sequenceComputed) {
         Mob var1 = this.worker.getMobWorker();
         Performance.record(var1.getLevel().tickManager(), "getSequence", (Runnable)(() -> {
            Performance.record(var1.getLevel().tickManager(), this.job.getStringID(), (Runnable)(() -> {
               this.sequence = (JobSequence)this.handler.sequenceFunction.apply(this);
               this.sequenceComputed = true;
            }));
         }));
      }

      return this.sequence;
   }
}
