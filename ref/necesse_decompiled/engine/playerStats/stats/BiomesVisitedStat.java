package necesse.engine.playerStats.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.PlayerStat;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamData;
import necesse.engine.steam.SteamGameStats;
import necesse.level.maps.biomes.Biome;

public class BiomesVisitedStat extends PlayerStat {
   protected HashSet<String> dirtyBiomes = new HashSet();
   protected HashMap<String, Integer> biomes = new HashMap();
   protected HashSet<String> biomeTypes = new HashSet();
   protected int islandsDiscovered;
   protected int totalBiomes = 0;

   public BiomesVisitedStat(PlayerStats var1, String var2) {
      super(var1, var2);
   }

   public void clean() {
      super.clean();
      this.dirtyBiomes.clear();
   }

   protected void setBiomesKnown(String var1, int var2, boolean var3) {
      int var4 = LevelSave.getMigratedBiomeID(var1, false);
      if (var4 != -1) {
         if (!BiomeRegistry.isDiscoverable(var4)) {
            return;
         }

         String var5 = BiomeRegistry.getDiscoverType(var4);
         int var6 = (Integer)this.biomes.getOrDefault(var1, 0);
         if (var6 == var2) {
            return;
         }

         this.biomes.put(var1, var2);
         int var7 = var2 - var6;
         this.totalBiomes += var7;
         if (var2 == 0) {
            boolean var8 = this.biomes.entrySet().stream().filter((var0) -> {
               return (Integer)var0.getValue() > 0;
            }).map((var0) -> {
               return BiomeRegistry.getDiscoverType(BiomeRegistry.getBiomeID((String)var0.getKey()));
            }).filter(Objects::nonNull).noneMatch((var1x) -> {
               return var1x.equals(var5);
            });
            if (var8) {
               this.biomeTypes.remove(var5);
            }
         } else {
            this.biomeTypes.add(var5);
         }

         if (var3) {
            this.updateSteam();
         }

         this.dirtyBiomes.add(var1);
         this.markImportantDirty();
      }

   }

   protected void addBiomeVisited(String var1) {
      this.setBiomesKnown(var1, (Integer)this.biomes.getOrDefault(var1, 0) + 1, true);
   }

   public void addBiomeVisited(Biome var1) {
      if (this.parent.mode == PlayerStats.Mode.READ_ONLY) {
         throw new IllegalStateException("Cannot set read only stats");
      } else {
         this.addBiomeVisited(var1.getStringID());
      }
   }

   public int getBiomesKnown(Biome var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return (Integer)this.biomes.getOrDefault(var1.getStringID(), 0);
      }
   }

   public Stream<Map.Entry<String, Integer>> stream() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomes.entrySet().stream();
      }
   }

   public void forEach(BiConsumer<String, Integer> var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         this.biomes.forEach(var1);
      }
   }

   public int getTotalBiomes() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.islandsDiscovered + this.totalBiomes;
      }
   }

   public int getTotalUniqueBiomes() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomes.size();
      }
   }

   public int getTotalBiomeTypes() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomeTypes.size();
      }
   }

   public void combine(PlayerStat var1) {
      if (var1 instanceof BiomesVisitedStat) {
         BiomesVisitedStat var2 = (BiomesVisitedStat)var1;
         var2.biomes.forEach((var1x, var2x) -> {
            this.setBiomesKnown(var1x, (Integer)this.biomes.getOrDefault(var1x, 0) + var2x, true);
         });
      }

   }

   public void resetCombine() {
      this.biomes.clear();
      this.totalBiomes = 0;
   }

   protected void updateSteam() {
      if (this.parent.controlAchievements) {
         SteamData.setStat("islands_visited", this.totalBiomes);
      }
   }

   public void loadSteam(SteamGameStats var1) {
      this.islandsDiscovered = var1.islands_discovered;
      this.totalBiomes = var1.islands_visited;
   }

   public void addSaveData(SaveData var1) {
      HashMap var10000 = this.biomes;
      Objects.requireNonNull(var1);
      var10000.forEach(var1::addInt);
   }

   public void applyLoadData(LoadData var1) {
      this.biomes.clear();
      this.biomeTypes.clear();
      this.totalBiomes = 0;
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();
         if (var3.isData()) {
            try {
               int var4 = LevelSave.getMigratedBiomeID(var3.getName(), false);
               if (var4 != -1) {
                  int var5 = Integer.parseInt(var3.getData());
                  this.setBiomesKnown(BiomeRegistry.getBiomeStringID(var4), var5, false);
               } else {
                  GameLog.warn.println("Could not load biomes known stat stringID: " + var3.getName());
               }
            } catch (NumberFormatException var6) {
               GameLog.warn.println("Could not load biomes known stat number: " + var3.getData());
            }
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.biomes.size());
      this.biomes.forEach((var1x, var2) -> {
         var1.putNextShortUnsigned(BiomeRegistry.getBiomeID(var1x));
         var1.putNextInt(var2);
      });
   }

   public void applyContentPacket(PacketReader var1) {
      this.biomes.clear();
      this.biomeTypes.clear();
      this.totalBiomes = 0;
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = BiomeRegistry.getBiomeStringID(var4);
         this.setBiomesKnown(var5, var1.getNextInt(), true);
      }

   }

   public void setupDirtyPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.dirtyBiomes.size());
      Iterator var2 = this.dirtyBiomes.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.putNextShortUnsigned(BiomeRegistry.getBiomeID(var3));
         var1.putNextInt((Integer)this.biomes.getOrDefault(var3, 0));
      }

   }

   public void applyDirtyPacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = BiomeRegistry.getBiomeStringID(var4);
         this.setBiomesKnown(var5, var1.getNextInt(), true);
      }

   }
}
