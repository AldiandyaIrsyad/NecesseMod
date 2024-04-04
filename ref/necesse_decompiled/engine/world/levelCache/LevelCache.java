package necesse.engine.world.levelCache;

import java.awt.Point;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.Server;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.World;
import necesse.engine.world.WorldFile;
import necesse.engine.world.WorldGenerator;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.layers.SettlementLevelLayer;

public class LevelCache {
   private static final int MIN_LOAD_THREADS = 1;
   private static final int MAX_LOAD_THREADS = 4;
   private static final int ISLANDS_PER_LOAD_THREAD = 4;
   private final World world;
   private Map<String, CacheObject> islands;
   private final LinkedList<FutureIslandTask> loadQueue;

   public LevelCache(World var1) {
      this.world = var1;
      this.islands = Collections.synchronizedMap(new HashMap());
      this.loadQueue = new LinkedList();
      Iterator var2 = var1.fileSystem.getLevelFiles().iterator();

      while(var2.hasNext()) {
         WorldFile var3 = (WorldFile)var2.next();
         if (var1.server.isStopped()) {
            break;
         }

         String var4 = GameUtils.removeFileExtension(var3.getFileName().toString());
         if (var4.endsWith("d0")) {
            int var5 = var4.indexOf("x");
            if (var5 != -1) {
               try {
                  int var6 = Integer.parseInt(var4.substring(0, var5));
                  int var7 = Integer.parseInt(var4.substring(var5 + 1, var4.indexOf("d0")));
                  this.loadQueue.add(new FutureIslandTask(var6, var7, var3));
               } catch (Exception var8) {
                  GameLog.warn.println("Could not load level cache: " + var4);
               } catch (ClassFormatError var9) {
                  GameLog.warn.println("Class error loading level cache: " + var4);
               }
            }
         }
      }

      int var10 = GameMath.limit(this.loadQueue.size() / 4, 1, 4);

      for(int var11 = 0; var11 < var10; ++var11) {
         this.startThread("world-biome-cache-" + var11);
      }

   }

   private void startThread(String var1) {
      (new Thread(() -> {
         while(true) {
            FutureIslandTask var1 = null;
            synchronized(this.loadQueue) {
               Iterator var3 = this.loadQueue.iterator();

               while(var3.hasNext()) {
                  FutureIslandTask var4 = (FutureIslandTask)var3.next();
                  if (!var4.isBeingProcessed) {
                     var1 = var4;
                     var4.isBeingProcessed = true;
                     break;
                  }
               }
            }

            if (var1 != null) {
               var1.run();
               this.handleTask(var1);
               if (var1.isPrioritized) {
                  synchronized(this.loadQueue) {
                     try {
                        this.loadQueue.wait(500L);
                     } catch (InterruptedException var9) {
                     }
                  }
               }

               synchronized(this.loadQueue) {
                  this.loadQueue.remove(var1);
                  if (!this.loadQueue.isEmpty()) {
                     continue;
                  }
               }
            }

            return;
         }
      }, var1)).start();
   }

   private CacheObject handleTask(FutureIslandTask var1) {
      try {
         CacheObject var2 = (CacheObject)var1.get();
         if (var2 != null) {
            this.setCache(var1.islandX, var1.islandY, var2);
         }

         return var2;
      } catch (InterruptedException var3) {
      } catch (ExecutionException var4) {
         GameLog.warn.println("Could not load island: " + var1.islandX + ", " + var1.islandY);
      }

      return null;
   }

   private String getHashKey(int var1, int var2) {
      return var1 + "x" + var2;
   }

   private FutureIslandTask getAndPrioritizeTask(int var1, int var2) {
      FutureIslandTask var3 = null;
      synchronized(this.loadQueue) {
         ListIterator var5 = this.loadQueue.listIterator();

         while(var5.hasNext()) {
            FutureIslandTask var6 = (FutureIslandTask)var5.next();
            if (var6.islandX == var1 && var6.islandY == var2) {
               var3 = var6;
               var5.remove();
            }
         }

         if (var3 != null) {
            var3.isPrioritized = true;
            this.loadQueue.addFirst(var3);
            this.loadQueue.notifyAll();
         }

         return var3;
      }
   }

   private void setCache(int var1, int var2, CacheObject var3) {
      this.islands.compute(this.getHashKey(var1, var2), (var1x, var2x) -> {
         if (var2x != null) {
            var2x.biome = var3.biome;
            if (var3.settlement.loaded) {
               var2x.settlement = var3.settlement;
            }
         } else {
            var2x = var3;
         }

         return var2x;
      });
   }

   public void updateLevel(Level var1) {
      LevelIdentifier var2 = var1.getIdentifier();
      if (var2.isIslandPosition() && var2.getIslandDimension() == 0) {
         this.setCache(var2.getIslandX(), var2.getIslandY(), new CacheObject(var1));
      }
   }

   private CacheObject getStoredCache(int var1, int var2) {
      CacheObject var3 = (CacheObject)this.islands.get(this.getHashKey(var1, var2));
      if (var3 == null) {
         FutureIslandTask var4 = this.getAndPrioritizeTask(var1, var2);
         if (var4 != null) {
            return this.handleTask(var4);
         }
      }

      return var3;
   }

   public int getBiomeID(int var1, int var2) {
      CacheObject var3 = this.getStoredCache(var1, var2);
      return var3 != null ? var3.biome.getID() : WorldGenerator.getBiome(var1, var2).getID();
   }

   public Biome getBiome(int var1, int var2) {
      return BiomeRegistry.getBiome(this.getBiomeID(var1, var2));
   }

   public SettlementCache getSettlement(int var1, int var2) {
      CacheObject var3 = this.getStoredCache(var1, var2);
      return var3 != null ? var3.settlement : new SettlementCache(var1, var2);
   }

   public List<SettlementCache> getSettlements() {
      return (List)this.islands.values().stream().map((var0) -> {
         return var0.settlement;
      }).collect(Collectors.toList());
   }

   private static CacheObject loadCache(Server var0, int var1, int var2, LoadData var3) throws InfoNotFoundException {
      Biome var4 = LevelSave.getLevelSaveBiomeRaw(var3, false);
      if (var4 != null && var4 != BiomeRegistry.UNKNOWN) {
         boolean var5 = false;
         GameMessage var6 = null;
         long var7 = -1L;
         int var9 = -1;
         if (LevelSave.settlementHasObjectEntityPos(var3) || SettlementLevelLayer.getActive(var3)) {
            var5 = true;
            var6 = SettlementLevelLayer.getName(var3);
            var7 = SettlementLevelLayer.getOwnerAuth(var3);
            if (var6 == null) {
               var6 = LevelSave.getSettlementDataName(var3);
            }

            if (var7 == -1L) {
               var7 = LevelSave.getSettlementOwnerAuth(var3);
            }

            var9 = SettlementLevelLayer.getTeamID(var0, var7);
         }

         return new CacheObject(var1, var2, var4, var5, var6, var7, var9);
      } else {
         throw new InfoNotFoundException("Not valid");
      }
   }

   private static class CacheObject {
      public final int islandX;
      public final int islandY;
      public Biome biome;
      public SettlementCache settlement;

      public CacheObject(int var1, int var2, Biome var3, boolean var4, GameMessage var5, long var6, int var8) {
         this.islandX = var1;
         this.islandY = var2;
         this.biome = var3;
         this.settlement = new SettlementCache(var1, var2, var4, var5, var6, var8);
      }

      public CacheObject(Level var1) {
         this.islandX = var1.getIslandX();
         this.islandY = var1.getIslandY();
         this.biome = var1.biome;
         this.settlement = new SettlementCache(var1);
      }
   }

   private class FutureIslandTask extends FutureTask<CacheObject> {
      public final int islandX;
      public final int islandY;
      public boolean isBeingProcessed;
      public boolean isPrioritized;

      public FutureIslandTask(int var2, int var3, WorldFile var4) {
         super(() -> {
            LoadData var4x = LevelCache.this.world.loadLevelScript(var4);
            if (var4x != null) {
               Point var5 = var4x.getPoint("island");
               if (var5.x == var2 && var5.y == var3) {
                  return LevelCache.loadCache(LevelCache.this.world.server, var2, var3, var4x);
               } else {
                  GameLog.warn.println("Incorrect island coordinates in file: " + var4);
                  return null;
               }
            } else {
               return null;
            }
         });
         this.islandX = var2;
         this.islandY = var3;
      }
   }

   private static class InfoNotFoundException extends Exception {
      public InfoNotFoundException() {
      }

      public InfoNotFoundException(String var1) {
         super(var1);
      }

      public InfoNotFoundException(String var1, Throwable var2) {
         super(var1, var2);
      }

      public InfoNotFoundException(Throwable var1) {
         super(var1);
      }

      public InfoNotFoundException(String var1, Throwable var2, boolean var3, boolean var4) {
         super(var1, var2, var3, var4);
      }
   }
}
