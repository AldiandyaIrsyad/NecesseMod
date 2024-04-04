package necesse.engine.save;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.network.server.Server;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.LevelRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.levelData.LevelEventSave;
import necesse.engine.save.levelData.MobSave;
import necesse.engine.save.levelData.ObjectEntitySave;
import necesse.engine.save.levelData.PickupEntitySave;
import necesse.engine.tickManager.Performance;
import necesse.engine.tickManager.PerformanceTimerManager;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.PickupEntity;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.layers.LevelLayer;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class LevelSave {
   public static PerformanceTimerManager debugLoadingPerformance;

   public LevelSave() {
   }

   public static Level loadSave(LoadData var0, Server var1) {
      try {
         return (Level)Performance.recordConstant(debugLoadingPerformance, "levelLoading", () -> {
            Level var2 = (Level)Performance.recordConstant(debugLoadingPerformance, "initial", () -> {
               LevelIdentifier var2;
               int var5;
               try {
                  var2 = new LevelIdentifier(var0.getUnsafeString("identifier", (String)null, false));
               } catch (InvalidLevelIdentifierException var12) {
                  Point var4 = var0.getPoint("island");
                  var5 = var0.getInt("dimension");
                  var2 = new LevelIdentifier(var4.x, var4.y, var5);
               }

               String var3 = var0.getUnsafeString("stringID", (String)null, false);
               if (var3 == null) {
                  var3 = restoreLevelStringID(var0, var2, var1);
                  if (var3 == null) {
                     GameLog.warn.println("Could not recover level type for level with identifier " + var2 + ". Using default instead.");
                  } else {
                     GameLog.debug.println("Recovered level type for level with identifier " + var2 + ": " + var3);
                  }
               }

               int var13 = var0.getInt("width");
               var5 = var0.getInt("height");
               Level var6 = null;
               if (var3 != null) {
                  int var7 = LevelRegistry.getLevelID(var3);
                  if (var7 != -1) {
                     var6 = LevelRegistry.getNewLevel(var7, var2, var13, var5, var1.world.worldEntity);
                  }
               }

               if (var6 == null) {
                  var6 = new Level(var2, var13, var5, var1.world.worldEntity);
               }

               var6.makeServerLevel(var1);
               LoadData var14 = var0.getFirstLoadDataByName("MODS");
               if (var14 != null) {
                  var6.lastMods = new ArrayList();
                  Iterator var8 = var14.getLoadData().iterator();

                  while(var8.hasNext()) {
                     LoadData var9 = (LoadData)var8.next();

                     try {
                        var6.lastMods.add(new ModSaveInfo(var9));
                     } catch (LoadDataException var11) {
                        GameLog.warn.print("Could not load mod info: " + var11.getMessage());
                     }
                  }
               }

               return var6;
            });
            Performance.recordConstant(debugLoadingPerformance, "applyLoadData", () -> {
               var2.applyLoadData(var0);
            });
            return var2;
         });
      } catch (Exception var3) {
         System.err.println("Level file is corrupt.");
         var3.printStackTrace();
         return null;
      }
   }

   public static SaveData getSave(Level var0) {
      SaveData var1 = new SaveData("LEVEL");
      var1.addSafeString("gameVersion", "0.24.2");
      var1.addUnsafeString("stringID", var0.getStringID());
      LevelIdentifier var2 = var0.getIdentifier();
      var1.addUnsafeString("identifier", var2.stringID);
      if (var2.isIslandPosition()) {
         var1.addPoint("island", new Point(var2.getIslandX(), var2.getIslandY()));
         var1.addInt("dimension", var2.getIslandDimension());
      }

      var1.addInt("width", var0.width);
      var1.addInt("height", var0.height);
      SaveData var3 = new SaveData("MODS");
      Iterator var4 = ModLoader.getEnabledMods().iterator();

      while(var4.hasNext()) {
         LoadedMod var5 = (LoadedMod)var4.next();
         SaveData var6 = (new ModSaveInfo(var5)).getSaveData();
         var3.addSaveData(var6);
      }

      var1.addSaveData(var3);
      var0.addSaveData(var1);
      return var1;
   }

   public static String restoreLevelStringID(LoadData var0, LevelIdentifier var1, Server var2) {
      int var3 = var1.getIslandDimension();
      if (var3 == 1337) {
         return "debug";
      } else if (var3 == -100) {
         return "dungeon";
      } else if (var3 == -101) {
         return "dungeonarena";
      } else if (var3 == -200) {
         return "temple";
      } else if (var3 == -201) {
         return "templearena";
      } else {
         Biome var4 = getLevelSaveBiome(var0);
         if (var4 == null) {
            var4 = var2.world.getBiome(var1.getIslandX(), var1.getIslandY());
         }

         if (var4 != BiomeRegistry.FOREST && var4 != BiomeRegistry.FOREST_VILLAGE && var4 != BiomeRegistry.PIRATE_ISLAND) {
            if (var4 == BiomeRegistry.PLAINS) {
               if (var3 == 0) {
                  return "plainssurface";
               }

               if (var3 == -1) {
                  return "forestcave";
               }

               if (var3 < -1) {
                  return "forestdeepcave";
               }
            } else if (var4 != BiomeRegistry.SNOW && var4 != BiomeRegistry.SNOW_VILLAGE) {
               if (var4 == BiomeRegistry.SWAMP) {
                  if (var3 == 0) {
                     return "swampsurface";
                  }

                  if (var3 == -1) {
                     return "swampcave";
                  }

                  if (var3 < -1) {
                     return "swampdeepcave";
                  }
               } else if (var4 == BiomeRegistry.DESERT || var4 == BiomeRegistry.DESERT_VILLAGE) {
                  if (var3 == 0) {
                     return "desertsurface";
                  }

                  if (var3 == -1) {
                     return "desertcave";
                  }

                  if (var3 < -1) {
                     return "desertdeepcave";
                  }
               }
            } else {
               if (var3 == 0) {
                  return "snowsurface";
               }

               if (var3 == -1) {
                  return "snowcave";
               }

               if (var3 < -1) {
                  return "snowdeepcave";
               }
            }
         } else {
            if (var3 == 0) {
               return "forestsurface";
            }

            if (var3 == -1) {
               return "forestcave";
            }

            if (var3 < -1) {
               return "forestdeepcave";
            }
         }

         return null;
      }
   }

   public static void addLevelBasics(Level var0, SaveData var1) {
      var1.addBoolean("isCave", var0.isCave);
      var1.addLong("lastWorldTime", var0.lastWorldTime);
      var1.addInt("tileTickX", var0.tileTickX);
      var1.addInt("tileTickY", var0.tileTickY);
      var1.addUnsafeString("biome", var0.biome.getStringID());
      var1.addBoolean("isProtected", var0.isProtected);
      if (!var0.childLevels.isEmpty()) {
         var1.addStringHashSet("childLevels", (HashSet)var0.childLevels.stream().map((var0x) -> {
            return var0x.stringID;
         }).collect(Collectors.toCollection(HashSet::new)));
      }

      LevelLayer[] var2 = var0.layers;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         LevelLayer var5 = var2[var4];
         var5.addSaveData(var1);
      }

      SaveData var10 = new SaveData("GNDData");
      var0.gndData.addSaveData(var10);
      var1.addSaveData(var10);
      SaveData var11 = new SaveData("LEVELDATA");
      var0.levelDataManager.addSaveData(var11);
      var1.addSaveData(var11);
      SaveData var12 = new SaveData("MOBS");
      Iterator var13 = var0.entityManager.mobs.iterator();

      while(var13.hasNext()) {
         Mob var6 = (Mob)var13.next();
         if (!var6.removed() && var6.shouldSave()) {
            var12.addSaveData(MobSave.getSave("MOB", var6));
         }
      }

      var1.addSaveData(var12);
      SaveData var14 = new SaveData("PICKUPENTITIES");
      Iterator var15 = var0.entityManager.pickups.iterator();

      while(var15.hasNext()) {
         PickupEntity var7 = (PickupEntity)var15.next();
         if (!var7.removed() && var7.shouldSave()) {
            var14.addSaveData(PickupEntitySave.getSave(var7));
         }
      }

      var1.addSaveData(var14);
      SaveData var16 = new SaveData("OBJECTENTITIES");
      Iterator var17 = var0.entityManager.objectEntities.iterator();

      while(var17.hasNext()) {
         ObjectEntity var8 = (ObjectEntity)var17.next();
         if (!var8.removed() && var8.shouldSave()) {
            var16.addSaveData(ObjectEntitySave.getSave(var8));
         }
      }

      var1.addSaveData(var16);
      SaveData var18 = new SaveData("EVENTS");
      Iterator var19 = var0.entityManager.getLevelEvents().iterator();

      while(var19.hasNext()) {
         LevelEvent var9 = (LevelEvent)var19.next();
         if (!var9.isOver() && var9.shouldSave) {
            var18.addSaveData(LevelEventSave.getSave(var9));
         }
      }

      var1.addSaveData(var18);
      SaveData var20 = new SaveData("STATS");
      var0.levelStats.addSaveData(var20);
      var1.addSaveData(var20);
   }

   public static void applyLevelBasics(Level var0, LoadData var1) {
      Performance.recordConstant(debugLoadingPerformance, "basic", () -> {
         LevelIdentifier var2 = var0.getIdentifier();
         var0.isCave = var1.getBoolean("isCave", !var2.isIslandPosition() || var2.getIslandDimension() < 0);
         var0.lastWorldTime = var1.getLong("lastWorldTime", 0L, false);
         var0.tileTickX = var1.getInt("tileTickX", 0);
         var0.tileTickY = var1.getInt("tileTickY", 0);
         Biome var3 = getLevelSaveBiome(var1);
         if (var3 != null) {
            var0.biome = var3;
         }

         var0.isProtected = var1.getBoolean("isProtected", false);
      });
      var0.childLevels.addAll(getChildLevels(var1));
      Performance.recordConstant(debugLoadingPerformance, "layers", () -> {
         LevelLayer[] var2 = var0.layers;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LevelLayer var5 = var2[var4];
            Performance.recordConstant(debugLoadingPerformance, var5.getStringID(), () -> {
               var5.loadSaveData(var1);
            });
         }

      });
      LoadData var2 = var1.getFirstLoadDataByName("GNDData");
      if (var2 != null) {
         try {
            var0.gndData.applyLoadData(var2);
         } catch (Exception var4) {
            System.err.println("Error loading GNDData for level " + var0.getIdentifier());
            var4.printStackTrace();
         }
      }

      Performance.recordConstant(debugLoadingPerformance, "regions", () -> {
         var0.regionManager.calculateRegions();
      });
      Performance.recordConstant(debugLoadingPerformance, "levelData", () -> {
         LoadData var2 = var1.getFirstLoadDataByName("LEVELDATA");
         if (var2 != null) {
            var0.levelDataManager.loadSaveData(var2);
         } else {
            System.err.println("Could not load any level data for " + var0.getIdentifier());
         }

      });
      Performance.recordConstant(debugLoadingPerformance, "mobs", () -> {
         try {
            List var2 = var1.getFirstLoadDataByName("MOBS").getLoadData();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               LoadData var4 = (LoadData)var3.next();

               try {
                  Mob var5 = MobSave.loadSave(var4, var0);
                  if (var5 != null) {
                     var0.entityManager.mobs.addHidden(var5);
                  }
               } catch (Exception var6) {
                  var6.printStackTrace();
               }
            }
         } catch (Exception var7) {
            System.err.println("Could not load level mobs, resulting in a wipe.");
         }

      });
      Performance.recordConstant(debugLoadingPerformance, "pickupEntities", () -> {
         try {
            List var2 = var1.getFirstLoadDataByName("PICKUPENTITIES").getLoadData();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               LoadData var4 = (LoadData)var3.next();

               try {
                  PickupEntity var5 = PickupEntitySave.loadSave(var4);
                  if (var5 != null) {
                     var0.entityManager.pickups.addHidden(var5);
                  }
               } catch (Exception var6) {
                  var6.printStackTrace();
               }
            }
         } catch (Exception var7) {
            System.err.println("Could not load level pickup entities, resulting in a wipe.");
         }

      });
      Performance.recordConstant(debugLoadingPerformance, "objectEntities", () -> {
         try {
            List var2 = var1.getFirstLoadDataByName("OBJECTENTITIES").getLoadData();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               LoadData var4 = (LoadData)var3.next();

               try {
                  ObjectEntity var5 = ObjectEntitySave.loadSave(var4, var0);
                  if (var5 != null) {
                     var0.entityManager.objectEntities.addHidden(var5);
                  }
               } catch (Exception var6) {
                  var6.printStackTrace();
               }
            }
         } catch (Exception var7) {
            System.err.println("Could not load level objectEntities, resulting in a wipe.");
         }

      });
      Performance.recordConstant(debugLoadingPerformance, "events", () -> {
         try {
            List var2 = var1.getFirstLoadDataByName("EVENTS").getLoadData();
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               LoadData var4 = (LoadData)var3.next();

               try {
                  LevelEvent var5 = LevelEventSave.loadSaveData(var4, var0);
                  if (var5 != null) {
                     var0.entityManager.addLevelEventHidden(var5);
                  }
               } catch (Exception var6) {
                  var6.printStackTrace();
               }
            }
         } catch (Exception var7) {
            System.err.println("Could not load level events, resulting in a wipe.");
         }

      });
      Performance.recordConstant(debugLoadingPerformance, "stats", () -> {
         try {
            LoadData var2 = var1.getFirstLoadDataByName("STATS");
            if (var2 != null) {
               var0.levelStats.applyLoadData(var2);
            }
         } catch (Exception var3) {
            System.err.println("Error loading level stats for " + var0.getIdentifier());
            var3.printStackTrace();
         }

      });
   }

   public static int getMigratedBiomeID(String var0, boolean var1) {
      int var2 = -1;

      try {
         var2 = BiomeRegistry.getBiomeIDRaw(var0);
      } catch (NoSuchElementException var5) {
         String var4 = VersionMigration.tryFixStringID(var0, VersionMigration.oldBiomeStringIDs);
         if (!var0.equals(var4)) {
            if (var1) {
               System.out.println("Migrated biome from " + var0 + " to " + var4);
            }

            var2 = BiomeRegistry.getBiomeID(var4);
         }
      }

      return var2;
   }

   public static Biome getLevelSaveBiomeRaw(LoadData var0, boolean var1) {
      String var2 = var0.getUnsafeString("biome");
      int var3 = getMigratedBiomeID(var2, var1);
      return BiomeRegistry.getBiome(var3);
   }

   public static Biome getLevelSaveBiome(LoadData var0) {
      try {
         return getLevelSaveBiomeRaw(var0, true);
      } catch (Exception var2) {
         System.err.println("Could not load level biome");
         return null;
      }
   }

   public static HashSet<LevelIdentifier> getChildLevels(LoadData var0) {
      HashSet var1 = new HashSet();
      HashSet var2 = var0.getStringHashSet("childLevels", (HashSet)null, false);
      if (var2 != null) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();

            try {
               var1.add(new LevelIdentifier(var4));
            } catch (Exception var6) {
            }
         }
      }

      return var1;
   }

   /** @deprecated */
   @Deprecated
   public static GameMessage getSettlementDataName(LoadData var0) {
      try {
         LoadData var1 = var0.getFirstLoadDataByName("LEVELDATA");
         if (var1 != null) {
            LoadData var2 = var1.getFirstLoadDataByName("settlement");
            if (var2 != null) {
               return SettlementLevelData.getName(var2);
            }
         }
      } catch (Exception var3) {
      }

      return null;
   }

   /** @deprecated */
   @Deprecated
   public static long getSettlementOwnerAuth(LoadData var0) {
      try {
         LoadData var1 = var0.getFirstLoadDataByName("LEVELDATA");
         if (var1 != null) {
            LoadData var2 = var1.getFirstLoadDataByName("settlement");
            if (var2 != null) {
               return SettlementLevelData.getOwnerAuth(var2);
            }
         }
      } catch (Exception var3) {
      }

      return -1L;
   }

   /** @deprecated */
   @Deprecated
   public static boolean settlementHasObjectEntityPos(LoadData var0) {
      try {
         LoadData var1 = var0.getFirstLoadDataByName("LEVELDATA");
         if (var1 != null) {
            LoadData var2 = var1.getFirstLoadDataByName("settlement");
            if (var2 != null) {
               return SettlementLevelData.hasObjectEntityPos(var2);
            }
         }
      } catch (Exception var3) {
      }

      return false;
   }
}
