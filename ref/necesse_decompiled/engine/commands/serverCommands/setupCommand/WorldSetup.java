package necesse.engine.commands.serverCommands.setupCommand;

import java.awt.Point;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.commands.CommandLog;
import necesse.engine.network.packet.PacketRegionData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.gameAreaSearch.GameAreaPointSearch;
import necesse.entity.mobs.Mob;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.RandomBreakObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.regionSystem.Region;

@FunctionalInterface
public interface WorldSetup {
   void apply(Server var1, ServerClient var2, boolean var3, CommandLog var4);

   static Point findClosestBiome(ServerClient var0, int var1, boolean var2, String... var3) {
      Biome[] var4 = new Biome[var3.length];

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var4[var5] = BiomeRegistry.getBiome(var3[var5]);
      }

      return findClosestBiome(var0, var1, var2, var4);
   }

   static Point findClosestBiome(ServerClient var0, int var1, boolean var2, Biome... var3) {
      Server var4 = var0.getServer();
      int var5 = var4.world.worldEntity.spawnLevelIdentifier.getIslandX();
      int var6 = var4.world.worldEntity.spawnLevelIdentifier.getIslandY();
      if (var0.getLevelIdentifier().isIslandPosition()) {
         var5 = var0.getLevelIdentifier().getIslandX();
         var6 = var0.getLevelIdentifier().getIslandY();
      }

      return (Point)(new GameAreaPointSearch(var5, var6, 50)).stream().filter((var5x) -> {
         if (var2 && var0.getServer().world.levelExists(new LevelIdentifier(var5x.x, var5x.y, var1))) {
            return false;
         } else {
            Biome var6 = var4.levelCache.getBiome(var5x.x, var5x.y);
            return Arrays.stream(var3).anyMatch((var1x) -> {
               return var6 == var1x;
            });
         }
      }).findFirst().orElse((Object)null);
   }

   static void buildRandomArena(Level var0, GameRandom var1, int var2, int var3, int var4, int var5) {
      LinesGeneration var6 = new LinesGeneration(var2, var3);
      int var7 = var1.nextInt(360);
      byte var8 = 20;
      int var9 = 360 / var8;

      for(int var10 = 0; var10 < var8; ++var10) {
         var7 += var1.getIntOffset(var9, var9 / 4);
         float var11 = var1.getFloatBetween((float)var4, (float)var5);
         float var12 = 3.1415927F * (float)var5 * 2.0F / (float)var8 * 2.0F;
         var6.addArm((float)var7, var11, var12);
      }

      CellAutomaton var13 = var6.toCellularAutomaton(var1);
      var13.cleanHardEdges();
      var13.doCellularAutomaton(0, 4, 2);
      HashMap var14 = new HashMap();
      var13.forEachTile(var0, (var1x, var2x, var3x) -> {
         GameTile var4 = var1x.getTile(var2x, var3x);
         if (!var4.isFloor && !var4.isLiquid) {
            var14.compute(var4.getID(), (var0, var1) -> {
               return var1 == null ? 1 : var1 + 1;
            });
         }

         var1x.setObject(var2x, var3x, 0, 0);
      });
      var14.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).map(Map.Entry::getKey).ifPresent((var2x) -> {
         var13.forEachTile(var0, (var1, var2, var3) -> {
            GameTile var4 = var1.getTile(var2, var3);
            if (var4.isLiquid) {
               var1.setTile(var2, var3, var2x);
            }

         });
      });
      placeTorches(var0, var1, var2, var3, var5);
      var0.entityManager.mobs.streamArea((float)(var2 * 32 + 16), (float)(var3 * 32 + 16), var5 * 32).filter((var3x) -> {
         return var3x.isHostile && var3x.getDistance((float)(var2 * 32 + 16), (float)(var3 * 32 + 16)) <= (float)(var5 * 32);
      }).forEach(Mob::remove);
   }

   static void placeTorches(Level var0, GameRandom var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < var4 + 4; var5 += 5) {
         placeTorchCircle(var0, var1, var2, var3, var5);
      }

   }

   static void placeTorchCircle(Level var0, GameRandom var1, int var2, int var3, int var4) {
      GameObject var5 = ObjectRegistry.getObject("torch");
      float var6 = 3.1415927F * (float)var4 * 2.0F;
      int var7 = Math.max(1, (int)(var6 / 5.0F));
      int var8 = var1.nextInt(360);
      int var9 = 360 / var7;

      for(int var10 = 0; var10 < var7; ++var10) {
         var8 += var1.getIntOffset(var9, Math.min(3, var9 / 4));
         int var11 = (int)((float)var2 + GameMath.sin((float)var8) * (float)var4);
         int var12 = (int)((float)var3 + GameMath.cos((float)var8) * (float)var4);
         if (var5.canPlace(var0, var11, var12, 0) == null) {
            var5.placeObject(var0, var11, var12, 0);
         }
      }

   }

   static void clearBreakableObjects(Level var0, int var1, int var2, int var3) {
      for(int var4 = var1 - var3; var4 <= var1 + var3; ++var4) {
         for(int var5 = var2 - var3; var5 <= var2 + var3; ++var5) {
            if (var0.getObject(var4, var5) instanceof RandomBreakObject && (new Point(var4, var5)).distance((double)var1, (double)var2) <= (double)var3) {
               var0.setObject(var4, var5, 0, 0);
            }
         }
      }

   }

   static void updateClientsLevel(Level var0, int var1, int var2, int var3) {
      for(int var4 = var1 - var3; var4 <= var1 + var3 + 15 - 1; var4 += 15) {
         for(int var5 = var2 - var3; var5 <= var2 + var3 + 15 - 1; var5 += 15) {
            Region var6 = var0.regionManager.getRegionByTile(var4, var5);
            GameUtils.streamServerClients(var0).forEach((var1x) -> {
               var1x.sendPacket(new PacketRegionData(var6, var1x));
            });
         }
      }

   }
}
