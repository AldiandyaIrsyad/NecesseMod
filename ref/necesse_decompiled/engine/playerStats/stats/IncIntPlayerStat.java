package necesse.engine.playerStats.stats;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.PlayerStat;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamData;
import necesse.engine.steam.SteamGameStats;

public class IncIntPlayerStat extends PlayerStat {
   protected final int defaultValue;
   protected int value;
   protected boolean isImportant;

   public IncIntPlayerStat(PlayerStats var1, String var2, int var3, boolean var4) {
      super(var1, var2);
      this.defaultValue = var3;
      this.value = var3;
      this.isImportant = var4;
   }

   public IncIntPlayerStat(PlayerStats var1, String var2, boolean var3) {
      this(var1, var2, 0, var3);
   }

   public int get() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.value;
      }
   }

   public void increment(int var1) {
      if (this.parent.mode == PlayerStats.Mode.READ_ONLY) {
         throw new IllegalStateException("Cannot set read only stats");
      } else if (var1 != 0) {
         this.value += var1;
         if (this.isImportant) {
            this.markImportantDirty();
         } else {
            this.markDirty();
         }

         this.updateSteam();
      }
   }

   public void combine(PlayerStat var1) {
      if (var1 instanceof IncIntPlayerStat) {
         IncIntPlayerStat var2 = (IncIntPlayerStat)var1;
         if (var2.value != 0) {
            this.value += var2.value;
            if (this.isImportant) {
               this.markImportantDirty();
            } else {
               this.markDirty();
            }

            this.updateSteam();
         }
      }

   }

   public void resetCombine() {
      this.value = this.defaultValue;
   }

   protected void updateSteam() {
      if (this.parent.controlAchievements) {
         SteamData.setStat(this.stringID, this.value);
      }
   }

   public void loadSteam(SteamGameStats var1) {
      this.value = var1.getStatByName(this.stringID, this.value);
   }

   public void addSaveData(SaveData var1) {
      if (this.value != this.defaultValue) {
         var1.addInt("value", this.value);
      }
   }

   public void applyLoadData(LoadData var1) {
      this.value = var1.getInt("value", this.value);
   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextInt(this.value);
   }

   public void applyContentPacket(PacketReader var1) {
      this.value = var1.getNextInt();
      this.updateSteam();
   }
}
