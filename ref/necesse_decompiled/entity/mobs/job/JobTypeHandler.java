package necesse.entity.mobs.job;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.GameTileRange;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.JobsLevelData;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class JobTypeHandler {
   public long globalCooldown;
   public int lastPerformedJobID = -1;
   public int prioritizeNextJobID = -1;
   public boolean resetPrioritizeNextJobIfFound = true;
   private HashMap<Integer, SubHandler<?>> handlers = new HashMap();
   private HashMap<String, TypePriority> priorities = new HashMap();

   public JobTypeHandler() {
      Iterator var1 = JobTypeRegistry.getTypes().iterator();

      while(var1.hasNext()) {
         JobType var2 = (JobType)var1.next();
         this.priorities.put(var2.getStringID(), new TypePriority(var2));
      }

   }

   public TypePriority getPriority(String var1) {
      return (TypePriority)this.priorities.get(var1);
   }

   public TypePriority getPriority(int var1) {
      JobType var2 = JobTypeRegistry.getJobType(var1);
      return var2 == null ? null : (TypePriority)this.priorities.get(var2.getStringID());
   }

   public <T extends LevelJob> void setJobHandler(Class<T> var1, int var2, int var3, int var4, int var5, BiPredicate<SubHandler<T>, EntityJobWorker> var6, Function<FoundJob<T>, JobSequence> var7) {
      this.setJobHandler(var1, var2, var3, var4, var5, var6, var7, (BiFunction)null);
   }

   public <T extends LevelJob> void setJobHandler(Class<T> var1, int var2, int var3, int var4, int var5, BiPredicate<SubHandler<T>, EntityJobWorker> var6, Function<FoundJob<T>, JobSequence> var7, BiFunction<EntityJobWorker, SubHandler<T>, Object> var8) {
      int var9 = LevelJobRegistry.getJobID(var1);
      if (var9 == -1) {
         throw new IllegalArgumentException("Job class must be registered");
      } else {
         TypePriority var10 = this.getPriority(LevelJobRegistry.getJobTypeID(var1));
         this.handlers.put(var9, new SubHandler(var9, var10, var2, var3, var4, var5, var6, var7, var8));
      }
   }

   public <T extends LevelJob> SubHandler<T> getJobHandler(Class<T> var1) {
      int var2 = LevelJobRegistry.getJobID(var1);
      if (var2 == -1) {
         throw new IllegalArgumentException("Job class must be registered");
      } else {
         return (SubHandler)this.handlers.get(var2);
      }
   }

   public SubHandler<?> getJobHandler(int var1) {
      return (SubHandler)this.handlers.get(var1);
   }

   public void startGlobalCooldown(long var1, int var3) {
      this.globalCooldown = var1 + (long)var3;
   }

   public boolean isOnGlobalCooldown(long var1) {
      return var1 < this.globalCooldown;
   }

   public void startCooldowns(long var1) {
      Iterator var3 = this.handlers.values().iterator();

      while(var3.hasNext()) {
         SubHandler var4 = (SubHandler)var3.next();
         var4.startCooldown(var1);
      }

   }

   public Collection<SubHandler<?>> getJobHandlers() {
      return this.handlers.values();
   }

   public Collection<TypePriority> getTypePriorities() {
      return this.priorities.values();
   }

   public Stream<LevelJob> streamJobs(EntityJobWorker var1) {
      Mob var2 = var1.getMobWorker();
      Point var3 = var1.getJobSearchTile();
      GameTileRange var4 = null;
      Stream var5 = null;
      Iterator var6 = this.handlers.values().iterator();

      while(true) {
         SubHandler var7;
         do {
            if (!var6.hasNext()) {
               if (var4 != null) {
                  JobsLevelData var9 = JobsLevelData.getJobsLevelData(var2.getLevel());
                  Stream var10 = var9.streamJobsInRegionsShape(var4.getRangeBounds(var3), 0).map((var0) -> {
                     return (LevelJob)var0.object;
                  });
                  if (var5 == null) {
                     var5 = var10;
                  } else {
                     var5 = Stream.concat(var5, var10);
                  }
               }

               if (var5 == null) {
                  return Stream.empty();
               }

               return var5;
            }

            var7 = (SubHandler)var6.next();
         } while(!var7.canPerform(var1));

         if (var7.searchInLevelJobData && (var4 == null || var4.maxRange < ((GameTileRange)var7.tileRange.apply(var2.getLevel())).maxRange)) {
            var4 = (GameTileRange)var7.tileRange.apply(var2.getLevel());
         }

         if (var7.extraJobStreamer != null) {
            Stream var8 = var7.extraJobStreamer.stream(var1, var7);
            if (var8 != null) {
               if (var5 == null) {
                  var5 = var8.map((var0) -> {
                     return var0;
                  });
               } else {
                  var5 = Stream.concat(var5, var8);
               }
            }
         }
      }
   }

   public static class TypePriority {
      public final JobType type;
      public int priority;
      public boolean disabledBySettler;
      public boolean disabledByPlayer = false;

      public TypePriority(JobType var1) {
         this.type = var1;
         this.disabledBySettler = var1.defaultDisabledBySettler;
      }

      public void loadSaveData(LoadData var1) {
         this.priority = var1.getInt("priority", this.priority, false);
         this.disabledByPlayer = var1.getBoolean("disabledByPlayer", this.disabledByPlayer, false);
      }

      public void addSaveData(SaveData var1) {
         var1.addInt("priority", this.priority);
         var1.addBoolean("disabledByPlayer", this.disabledByPlayer);
      }
   }

   public static class SubHandler<T extends LevelJob> {
      public final int jobID;
      public final TypePriority priority;
      public final Function<Level, GameTileRange> tileRange;
      public int minCooldownMS;
      public int maxCooldownMS;
      public int minWorkBreakBufferUsage;
      public int maxWorkBreakBufferUsage;
      public boolean disabledBySettler;
      public BiPredicate<SubHandler<T>, EntityJobWorker> canPerform;
      public Function<FoundJob<T>, JobSequence> sequenceFunction;
      public BiFunction<EntityJobWorker, SubHandler<T>, Object> preSequenceCompute;
      public long nextCooldownComplete;
      public JobStreamSupplier<? extends T> extraJobStreamer;
      public boolean searchInLevelJobData = true;

      public SubHandler(int var1, TypePriority var2, int var3, int var4, int var5, int var6, BiPredicate<SubHandler<T>, EntityJobWorker> var7, Function<FoundJob<T>, JobSequence> var8, BiFunction<EntityJobWorker, SubHandler<T>, Object> var9) {
         this.jobID = var1;
         this.priority = var2;
         this.tileRange = var2.type.tileRange;
         this.minCooldownMS = var3;
         this.maxCooldownMS = var4;
         this.minWorkBreakBufferUsage = var5;
         this.maxWorkBreakBufferUsage = var6;
         this.canPerform = var7;
         this.sequenceFunction = var8;
         this.preSequenceCompute = var9;
      }

      public void startCooldown(long var1) {
         this.nextCooldownComplete = var1 + (long)GameRandom.globalRandom.getIntBetween(this.minCooldownMS, this.maxCooldownMS);
      }

      public boolean isOnCooldown(long var1) {
         return var1 <= this.nextCooldownComplete;
      }

      public int nextWorkBreakBufferUsage() {
         return GameRandom.globalRandom.getIntBetween(this.minWorkBreakBufferUsage, this.maxWorkBreakBufferUsage);
      }

      public boolean canPerform(EntityJobWorker var1) {
         if (this.disabledBySettler) {
            return false;
         } else {
            return this.isOnCooldown(var1.getMobWorker().getWorldEntity().getTime()) ? false : this.canPerform.test(this, var1);
         }
      }

      public Stream<? extends T> streamJobs(EntityJobWorker var1) {
         ZoneTester var2 = var1.getJobRestrictZone();
         Point var3 = var1.getJobSearchTile();
         Mob var4 = var1.getMobWorker();
         Stream var5 = null;
         if (this.searchInLevelJobData) {
            GameTileRange var6 = (GameTileRange)this.tileRange.apply(var4.getLevel());
            JobsLevelData var7 = JobsLevelData.getJobsLevelData(var4.getLevel());
            var5 = var7.streamInRegionsInTileRange(var3.x * 32 + 16, var3.y * 32 + 16, var6.maxRange).filter((var1x) -> {
               return ((LevelJob)var1x.object).isWithinRestrictZone(var2);
            }).filter((var2x) -> {
               return var6.isWithinRange(var3, ((LevelJob)var2x.object).tileX, ((LevelJob)var2x.object).tileY);
            }).map((var0) -> {
               return (LevelJob)var0.object;
            }).filter((var1x) -> {
               return var1x.getID() == this.jobID;
            }).map((var0) -> {
               return var0;
            });
         }

         if (this.extraJobStreamer != null) {
            Stream var8 = this.extraJobStreamer.stream(var1, this);
            if (var8 != null) {
               if (var5 == null) {
                  var5 = var8;
               } else {
                  var5 = Stream.concat(var5, var8);
               }
            }
         }

         return var5 == null ? Stream.empty() : var5;
      }

      public ComputedValue<Object> getPreSequenceCompute(EntityJobWorker var1) {
         return new ComputedValue(() -> {
            return this.preSequenceCompute == null ? null : this.preSequenceCompute.apply(var1, this);
         });
      }

      public Stream<FoundJob<T>> streamFoundJobs(EntityJobWorker var1) {
         Mob var2 = var1.getMobWorker();
         ComputedValue var3 = this.getPreSequenceCompute(var1);
         return this.streamJobs(var1).filter((var1x) -> {
            return var1x.reservable.isAvailable(var2);
         }).map((var3x) -> {
            return new FoundJob(var1, var3x, this, this.priority, var3);
         });
      }

      public Stream<FoundJob<T>> streamFoundJobsFiltered(EntityJobWorker var1) {
         return this.streamFoundJobs(var1).filter((var1x) -> {
            return (var1x.priority == null || !var1x.priority.disabledBySettler && !var1x.priority.disabledByPlayer) && var1x.handler.canPerform(var1);
         }).filter(FoundJob::isWithinWorkRange);
      }
   }

   public interface JobStreamSupplier<T extends LevelJob> {
      Stream<T> stream(EntityJobWorker var1, SubHandler<?> var2);
   }
}
