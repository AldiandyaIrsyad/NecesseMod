package necesse.level.maps.levelData.jobs;

import java.awt.Shape;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.engine.util.gameAreaSearch.JobsRegionSearch;
import necesse.level.maps.Level;
import necesse.level.maps.layers.LevelLayer;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.regionSystem.LevelRegionsSpliterator;
import necesse.level.maps.regionSystem.RegionPosition;

public class JobsLevelData extends LevelData {
   public static int secondsToTickLevel = 180;
   protected boolean firstRun = true;
   protected int tickerTileX;
   protected int tickerTileY;
   protected int tileTicksPerTick;
   protected GameLinkedList<LevelJob>[][] jobs;

   public JobsLevelData() {
   }

   public void setLevel(Level var1) {
      super.setLevel(var1);
      if (this.jobs == null) {
         this.jobs = new GameLinkedList[var1.regionManager.getRegionsWidth()][var1.regionManager.getRegionsHeight()];
         this.tileTicksPerTick = var1.width * var1.height / 20 / 10;
      }

   }

   public void tick() {
      super.tick();

      for(int var1 = 0; var1 < this.tileTicksPerTick; ++var1) {
         this.addLevelJobs(this.tickerTileX, this.tickerTileY);
         ++this.tickerTileX;
         if (this.tickerTileX >= this.level.width) {
            this.tickerTileX = 0;
            ++this.tickerTileY;
            if (this.tickerTileY >= this.level.height) {
               this.tickerTileY = 0;
               if (this.firstRun) {
                  this.tileTicksPerTick = this.level.width * this.level.height / 20 / secondsToTickLevel;
                  this.firstRun = false;
               } else {
                  this.cleanupJobs();
               }
            }
         }
      }

   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      SaveData var2 = new SaveData("JOBS");
      GameLinkedList[][] var3 = this.jobs;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         GameLinkedList[] var6 = var3[var5];
         GameLinkedList[] var7 = var6;
         int var8 = var6.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            GameLinkedList var10 = var7[var9];
            if (var10 != null) {
               synchronized(var10) {
                  Iterator var12 = var10.iterator();

                  while(var12.hasNext()) {
                     LevelJob var13 = (LevelJob)var12.next();
                     if (var13.shouldSave() && var13.isValid()) {
                        SaveData var14 = new SaveData(var13.getStringID());
                        var13.addSaveData(var14);
                        var2.addSaveData(var14);
                     }
                  }
               }
            }
         }
      }

      var1.addSaveData(var2);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      LoadData var2 = var1.getFirstLoadDataByName("JOBS");
      if (var2 != null) {
         Iterator var3 = var2.getLoadData().iterator();

         while(var3.hasNext()) {
            LoadData var4 = (LoadData)var3.next();
            if (var4.isArray()) {
               String var5 = var4.getName();

               try {
                  LevelJob var6 = LevelJobRegistry.loadJob(var5, var4);
                  this.addJob(var6, false, true);
               } catch (Exception var7) {
                  GameLog.warn.println("Could not load level job");
                  var7.printStackTrace();
               }
            }
         }
      }

   }

   public void cleanupJobs() {
      GameLinkedList[][] var1 = this.jobs;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         GameLinkedList[] var4 = var1[var3];
         GameLinkedList[] var5 = var4;
         int var6 = var4.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            GameLinkedList var8 = var5[var7];
            if (var8 != null) {
               synchronized(var8) {
                  var8.removeIf((var0) -> {
                     return !var0.isValid();
                  });
               }
            }
         }
      }

   }

   public void addLevelJobs(int var1, int var2) {
      LevelLayer[] var3 = this.level.layers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         LevelLayer var6 = var3[var5];
         List var7 = var6.getLevelJobs(var1, var2);
         if (var7 != null) {
            Iterator var8 = var7.iterator();

            while(var8.hasNext()) {
               LevelJob var9 = (LevelJob)var8.next();
               this.addJob(var9);
            }
         }
      }

   }

   public LevelJob addJob(LevelJob var1) {
      return this.addJob(var1, false);
   }

   public LevelJob addJob(LevelJob var1, boolean var2) {
      return this.addJob(var1, var2, false);
   }

   protected LevelJob addJob(LevelJob var1, boolean var2, boolean var3) {
      RegionPosition var4 = this.level.regionManager.getRegionPosByTile(var1.tileX, var1.tileY);
      GameLinkedList var5 = this.getJobsInRegion(var4);
      synchronized(var5) {
         GameLinkedList.Element var7;
         if (!var3) {
            var7 = var5.getFirstElement();

            while(var7 != null) {
               if (!((LevelJob)var7.object).isValid()) {
                  GameLinkedList.Element var8 = var7;
                  var7 = var7.next();
                  var8.remove();
               } else {
                  if (((LevelJob)var7.object).isSameJob(var1)) {
                     if (!var2) {
                        return (LevelJob)var7.object;
                     }

                     var7.remove();
                     break;
                  }

                  var7 = var7.next();
               }
            }
         }

         var7 = var5.addLast(var1);
         var1.init(this.level, var7);
         if (!var3 && !var1.isValid()) {
            var7.remove();
            return null;
         } else {
            return var1;
         }
      }
   }

   public GameLinkedList<LevelJob> getJobsInRegion(RegionPosition var1) {
      return this.getJobsInRegion(var1.regionX, var1.regionY);
   }

   public GameLinkedList<LevelJob> getJobsInRegion(int var1, int var2) {
      GameLinkedList var3 = this.jobs[var1][var2];
      if (var3 == null) {
         var3 = new GameLinkedList();
         this.jobs[var1][var2] = var3;
      }

      return var3;
   }

   public Stream<LevelJob> streamJobsInTile(int var1, int var2) {
      RegionPosition var3 = this.level.regionManager.getRegionPosByTile(var1, var2);
      return this.getJobsInRegion(var3).stream().filter((var2x) -> {
         return var2x.tileX == var1 && var2x.tileY == var2;
      });
   }

   public GameAreaStream<GameLinkedList<LevelJob>.Element> streamAreaJobs(int var1, int var2, int var3) {
      return (new JobsRegionSearch(this.level, var1, var2, var3)).streamEach();
   }

   public Stream<GameLinkedList<LevelJob>.Element> streamJobsInRegionsShape(Shape var1, int var2) {
      return (new LevelRegionsSpliterator(this.level, var1, var2)).stream().flatMap((var1x) -> {
         return this.getJobsInRegion(var1x).streamElements();
      }).filter((var0) -> {
         return ((LevelJob)var0.object).isValid();
      });
   }

   public Stream<GameLinkedList<LevelJob>.Element> streamInRegionsInRange(float var1, float var2, int var3) {
      return this.streamJobsInRegionsShape(GameUtils.rangeBounds(var1, var2, var3), 0);
   }

   public Stream<GameLinkedList<LevelJob>.Element> streamInRegionsInTileRange(int var1, int var2, int var3) {
      return this.streamJobsInRegionsShape(GameUtils.rangeTileBounds(var1, var2, var3), 0);
   }

   public <T extends LevelJob> Stream<T> mapClass(Class<T> var1, Stream<LevelJob> var2) {
      Objects.requireNonNull(var1);
      Stream var10000 = var2.filter(var1::isInstance);
      Objects.requireNonNull(var1);
      return var10000.map(var1::cast);
   }

   public static JobsLevelData getJobsLevelData(Level var0) {
      if (var0.isClient()) {
         throw new IllegalArgumentException("Level cannot be client level");
      } else {
         LevelData var1 = var0.getLevelData("jobs");
         if (var1 instanceof JobsLevelData) {
            return (JobsLevelData)var1;
         } else {
            JobsLevelData var2 = new JobsLevelData();
            var0.addLevelData("jobs", var2);
            return var2;
         }
      }
   }

   public static LevelJob addJob(Level var0, LevelJob var1) {
      JobsLevelData var2 = getJobsLevelData(var0);
      return var2.addJob(var1);
   }
}
