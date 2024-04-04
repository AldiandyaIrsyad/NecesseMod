package necesse.engine.playerStats.stats;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.PlayerStat;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamData;
import necesse.engine.steam.SteamGameStats;
import necesse.engine.world.worldData.incursions.IncursionBiomeStats;
import necesse.engine.world.worldData.incursions.IncursionDataStats;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;

public class IncursionBiomePlayerStat extends PlayerStat {
   protected IncursionBiomeStats biomes = new IncursionBiomeStats();

   public IncursionBiomePlayerStat(PlayerStats var1, String var2) {
      super(var1, var2);
   }

   public void combine(PlayerStat var1) {
      if (var1 instanceof IncursionBiomePlayerStat) {
         IncursionBiomePlayerStat var2 = (IncursionBiomePlayerStat)var1;
         this.biomes.combine(var2.biomes);
         if (var2.biomes.getTotal() > 0) {
            this.updateSteam();
            this.markDirty();
         }
      }

   }

   public void resetCombine() {
      this.biomes.reset();
   }

   protected void updateSteam() {
      if (this.parent.controlAchievements) {
         SteamData.setStat(this.stringID, this.biomes.getTotal());
      }
   }

   public void loadSteam(SteamGameStats var1) {
      this.biomes.loadSteamTotal(var1.opened_incursions);
   }

   public void addSaveData(SaveData var1) {
      this.biomes.addSaveData(var1);
   }

   public void applyLoadData(LoadData var1) {
      this.biomes.applyLoadData(var1);
   }

   public void setupContentPacket(PacketWriter var1) {
      this.biomes.setupContentPacket(var1);
   }

   public void applyContentPacket(PacketReader var1) {
      this.biomes.applyContentPacket(var1);
   }

   public int getOpened() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomes.getTotal();
      }
   }

   public int getOpened(IncursionBiome var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomes.getTotal(var1);
      }
   }

   public int getOpened(String var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomes.getTotal(var1);
      }
   }

   public int getOpened(int var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomes.getTotal(var1);
      }
   }

   public IncursionDataStats getData(IncursionBiome var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomes.getData(var1);
      }
   }

   public IncursionDataStats getData(String var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomes.getData(var1);
      }
   }

   public IncursionDataStats getData(int var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.biomes.getData(var1);
      }
   }

   public void add(BiomeMissionIncursionData var1) {
      if (this.parent.mode == PlayerStats.Mode.READ_ONLY) {
         throw new IllegalStateException("Cannot set read only stats");
      } else {
         this.biomes.add(var1);
         this.updateSteam();
         this.markDirty();
      }
   }
}
