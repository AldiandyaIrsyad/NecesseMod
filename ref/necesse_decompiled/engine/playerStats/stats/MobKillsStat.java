package necesse.engine.playerStats.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.PlayerStat;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamData;
import necesse.engine.steam.SteamGameStats;
import necesse.entity.mobs.Mob;

public class MobKillsStat extends PlayerStat {
   protected HashSet<String> dirtyKills = new HashSet();
   protected HashMap<String, Integer> kills = new HashMap();
   protected int totalKills = 0;
   protected int bossKills = 0;

   public MobKillsStat(PlayerStats var1, String var2) {
      super(var1, var2);
   }

   public void clean() {
      super.clean();
      this.dirtyKills.clear();
   }

   protected void setKills(String var1, int var2, boolean var3) {
      int var4 = MobRegistry.getMobID(var1);
      if (var4 != -1 && MobRegistry.countMobKillStat(var4)) {
         int var5 = (Integer)this.kills.getOrDefault(var1, 0);
         if (var5 == var2) {
            return;
         }

         this.kills.put(var1, var2);
         int var6 = var2 - var5;
         this.totalKills += var6;
         if (MobRegistry.isBossMob(var4)) {
            this.bossKills += var6;
         }

         if (var3) {
            this.updateSteam();
         }

         this.dirtyKills.add(var1);
         this.markImportantDirty();
      }

   }

   public void addKill(String var1) {
      if (this.parent.mode == PlayerStats.Mode.READ_ONLY) {
         throw new IllegalStateException("Cannot set read only stats");
      } else {
         this.setKills(var1, (Integer)this.kills.getOrDefault(var1, 0) + 1, true);
      }
   }

   public void addKill(Mob var1) {
      this.addKill(var1.getStringID());
   }

   public int getKills(String var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return (Integer)this.kills.getOrDefault(var1, 0);
      }
   }

   public void forEach(BiConsumer<String, Integer> var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         this.kills.forEach(var1);
      }
   }

   public int getTotalKills() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.totalKills;
      }
   }

   public int getBossKills() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.bossKills;
      }
   }

   public void combine(PlayerStat var1) {
      if (var1 instanceof MobKillsStat) {
         MobKillsStat var2 = (MobKillsStat)var1;
         var2.kills.forEach((var1x, var2x) -> {
            this.setKills(var1x, (Integer)this.kills.getOrDefault(var1x, 0) + var2x, true);
         });
      }

   }

   public void resetCombine() {
      this.kills.clear();
      this.totalKills = 0;
      this.bossKills = 0;
   }

   protected void updateSteam() {
      if (this.parent.controlAchievements) {
         SteamData.setStat("mob_kills", this.totalKills);
         SteamData.setStat("boss_kills", this.bossKills);
      }
   }

   public void loadSteam(SteamGameStats var1) {
      this.totalKills = var1.mob_kills;
      this.bossKills = var1.boss_kills;
   }

   public void addSaveData(SaveData var1) {
      HashMap var10000 = this.kills;
      Objects.requireNonNull(var1);
      var10000.forEach(var1::addInt);
   }

   public void applyLoadData(LoadData var1) {
      this.kills.clear();
      this.totalKills = 0;
      this.bossKills = 0;
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();
         if (var3.isData()) {
            try {
               int var4 = LoadData.getInt(var3);
               this.setKills(var3.getName(), var4, false);
            } catch (NumberFormatException var5) {
               GameLog.warn.println("Could not load mob kills stat number: " + var3.getData());
            }
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.kills.size());
      this.kills.forEach((var1x, var2) -> {
         var1.putNextShortUnsigned(MobRegistry.getMobID(var1x));
         var1.putNextInt(var2);
      });
   }

   public void applyContentPacket(PacketReader var1) {
      this.kills.clear();
      this.totalKills = 0;
      this.bossKills = 0;
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = MobRegistry.getMobStringID(var4);
         this.setKills(var5, var1.getNextInt(), true);
      }

   }

   public void setupDirtyPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.dirtyKills.size());
      Iterator var2 = this.dirtyKills.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.putNextShortUnsigned(MobRegistry.getMobID(var3));
         var1.putNextInt((Integer)this.kills.getOrDefault(var3, 0));
      }

   }

   public void applyDirtyPacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = MobRegistry.getMobStringID(var4);
         this.setKills(var5, var1.getNextInt(), true);
      }

   }
}
