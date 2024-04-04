package necesse.engine.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import necesse.engine.GameEvents;
import necesse.engine.GameLog;
import necesse.engine.events.worldGeneration.GeneratedLevelEvent;
import necesse.engine.network.server.Server;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.DebugLevel;
import necesse.level.maps.Level;
import necesse.level.maps.TemporaryDummyLevel;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.dungeon.DungeonArenaLevel;
import necesse.level.maps.biomes.dungeon.DungeonLevel;
import necesse.level.maps.biomes.temple.TempleArenaLevel;
import necesse.level.maps.biomes.temple.TempleLevel;

public abstract class WorldGenerator {
   private static final List<WorldGenerator> generators = new ArrayList();
   private static boolean registryOpen = true;

   public WorldGenerator() {
   }

   public static void registerGenerator(WorldGenerator var0) {
      if (!registryOpen) {
         throw new IllegalStateException("World generator registration is closed");
      } else {
         Objects.requireNonNull(var0);
         generators.add(0, var0);
      }
   }

   public static void closeRegistry() {
      if (!registryOpen) {
         throw new IllegalStateException("World generator registry already closed");
      } else {
         registryOpen = false;
      }
   }

   public static Level generateNewLevel(int var0, int var1, int var2, Server var3) {
      Object var4 = null;
      Iterator var5 = generators.iterator();

      WorldGenerator var6;
      while(var5.hasNext()) {
         var6 = (WorldGenerator)var5.next();
         var4 = var6.getNewLevel(var0, var1, var2, var3);
         if (var4 != null) {
            break;
         }
      }

      if (var4 == null) {
         var4 = new TemporaryDummyLevel(new LevelIdentifier(var0, var1, var2), var3.world.worldEntity);
      }

      ((Level)var4).makeServerLevel(var3);
      ((Level)var4).setWorldEntity(var3.world.worldEntity);
      var5 = generators.iterator();

      while(var5.hasNext()) {
         var6 = (WorldGenerator)var5.next();
         var6.modifyLevel((Level)var4, var3);
      }

      GameEvents.triggerEvent(new GeneratedLevelEvent((Level)var4));
      return (Level)var4;
   }

   public static Biome getBiome(int var0, int var1) {
      Iterator var2 = generators.iterator();

      Biome var4;
      do {
         if (!var2.hasNext()) {
            throw new IllegalStateException("Could not get biome");
         }

         WorldGenerator var3 = (WorldGenerator)var2.next();
         var4 = var3.biome(var0, var1);
      } while(var4 == null);

      return var4;
   }

   public static long getSeed(int var0, int var1) {
      Iterator var2 = generators.iterator();

      long var4;
      do {
         if (!var2.hasNext()) {
            throw new IllegalStateException("Could not get seed");
         }

         WorldGenerator var3 = (WorldGenerator)var2.next();
         var4 = var3.islandSeed(var0, var1);
      } while(var4 == 0L);

      return var4;
   }

   public static float getIslandSize(int var0, int var1) {
      Iterator var2 = generators.iterator();

      float var4;
      do {
         if (!var2.hasNext()) {
            throw new IllegalStateException("Could not get island size");
         }

         WorldGenerator var3 = (WorldGenerator)var2.next();
         var4 = var3.islandSize(var0, var1);
      } while(var4 == 0.0F);

      return var4;
   }

   public static Point getStartingIsland(int var0) {
      Iterator var1 = generators.iterator();

      Point var3;
      do {
         if (!var1.hasNext()) {
            throw new IllegalStateException("Could not get starting island");
         }

         WorldGenerator var2 = (WorldGenerator)var1.next();
         var3 = var2.startingIsland(var0);
      } while(var3 == null);

      return var3;
   }

   public abstract Level getNewLevel(int var1, int var2, int var3, Server var4);

   public void modifyLevel(Level var1, Server var2) {
   }

   public Biome biome(int var1, int var2) {
      return BiomeRegistry.getRandomBiome(getSeed(var1, var2));
   }

   public float islandSize(int var1, int var2) {
      return (new GameRandom(getSeed(var1, var2))).nextFloat();
   }

   public long islandSeed(int var1, int var2) {
      return (new GameRandom((long)(var1 * 1289969 + var2 * 888161))).nextLong();
   }

   public Point startingIsland(int var1) {
      GameRandom var2 = new GameRandom((long)var1);
      long var3 = 0L;

      Point var7;
      do {
         if (var3 >= 2500L) {
            return new Point(var2.getIntBetween(-10000, 10000), var2.getIntBetween(-10000, 10000));
         }

         ++var3;
         int var5 = var2.getIntBetween(-10000, 10000);
         int var6 = var2.getIntBetween(-10000, 10000);
         var7 = new Point(var5, var6);
      } while(!BiomeRegistry.defaultSpawnIslandFilter.test(var7));

      GameLog.debug.println("Took " + var3 + " iterations to find a spawn island");
      return var7;
   }

   static {
      registerGenerator(new WorldGenerator() {
         public Level getNewLevel(int var1, int var2, int var3, Server var4) {
            Biome var5 = var4.world.getBiome(var1, var2);
            return var5.getNewLevel(var1, var2, var3, var4, var4.world.worldEntity);
         }
      });
      registerGenerator(new WorldGenerator() {
         public Level getNewLevel(int var1, int var2, int var3, Server var4) {
            if (var3 == -100) {
               DungeonLevel var6 = new DungeonLevel(var1, var2, var3, var4.world.worldEntity);
               var6.biome = BiomeRegistry.DUNGEON_ISLAND;
               return var6;
            } else if (var3 == -101) {
               DungeonArenaLevel var5 = new DungeonArenaLevel(var1, var2, var3, var4.world.worldEntity);
               var5.biome = BiomeRegistry.DUNGEON_ISLAND;
               return var5;
            } else {
               return null;
            }
         }
      });
      registerGenerator(new WorldGenerator() {
         public Level getNewLevel(int var1, int var2, int var3, Server var4) {
            if (var3 == -200) {
               Level var6 = TempleLevel.generateNew(var1, var2, var3, var4.world.worldEntity);
               var6.biome = BiomeRegistry.TEMPLE;
               return var6;
            } else if (var3 == -201) {
               TempleArenaLevel var5 = new TempleArenaLevel(var1, var2, var3, var4.world.worldEntity);
               var5.biome = BiomeRegistry.TEMPLE;
               return var5;
            } else {
               return null;
            }
         }
      });
      registerGenerator(new WorldGenerator() {
         public Level getNewLevel(int var1, int var2, int var3, Server var4) {
            return var3 == 1337 ? new DebugLevel(var1, var2, var3, var4.world.worldEntity) : null;
         }
      });
   }
}
