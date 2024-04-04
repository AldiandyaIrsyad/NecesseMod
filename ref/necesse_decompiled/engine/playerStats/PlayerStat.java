package necesse.engine.playerStats;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamGameStats;

public abstract class PlayerStat {
   public final PlayerStats parent;
   public final String stringID;
   private boolean dirty;
   private boolean importantDirty;

   public PlayerStat(PlayerStats var1, String var2) {
      this.parent = var1;
      this.stringID = var2;
   }

   public GameMessage getDisplayName() {
      return new LocalMessage("stats", this.stringID);
   }

   public boolean isDirty() {
      return this.dirty || this.importantDirty;
   }

   public void markDirty() {
      this.dirty = true;
      this.parent.markDirty();
   }

   public void clean() {
      this.dirty = false;
      this.importantDirty = false;
   }

   public boolean isImportantDirty() {
      return this.importantDirty;
   }

   public void markImportantDirty() {
      this.importantDirty = true;
      this.parent.markImportantDirty();
   }

   public abstract void combine(PlayerStat var1);

   public abstract void resetCombine();

   public abstract void loadSteam(SteamGameStats var1);

   public abstract void addSaveData(SaveData var1);

   public abstract void applyLoadData(LoadData var1);

   public abstract void setupContentPacket(PacketWriter var1);

   public abstract void applyContentPacket(PacketReader var1);

   public void setupDirtyPacket(PacketWriter var1) {
      this.setupContentPacket(var1);
   }

   public void applyDirtyPacket(PacketReader var1) {
      this.applyContentPacket(var1);
   }
}
