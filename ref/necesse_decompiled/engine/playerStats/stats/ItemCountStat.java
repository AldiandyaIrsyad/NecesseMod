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
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamData;
import necesse.engine.steam.SteamGameStats;
import necesse.inventory.item.Item;

public class ItemCountStat extends PlayerStat {
   protected HashSet<String> dirty = new HashSet();
   protected HashMap<String, Integer> counts = new HashMap();
   protected int total = 0;

   public ItemCountStat(PlayerStats var1, String var2) {
      super(var1, var2);
   }

   public void clean() {
      super.clean();
      this.dirty.clear();
   }

   protected void set(String var1, int var2, boolean var3) {
      int var4 = ItemRegistry.getItemID(var1);
      if (var4 != -1) {
         int var5 = (Integer)this.counts.getOrDefault(var1, 0);
         if (var5 == var2) {
            return;
         }

         this.counts.put(var1, var2);
         int var6 = var2 - var5;
         this.total += var6;
         if (var3) {
            this.updateSteam();
         }

         this.dirty.add(var1);
         this.markImportantDirty();
      }

   }

   protected void add(String var1) {
      this.set(var1, (Integer)this.counts.getOrDefault(var1, 0) + 1, true);
   }

   public void add(Item var1) {
      if (this.parent.mode == PlayerStats.Mode.READ_ONLY) {
         throw new IllegalStateException("Cannot set read only stats");
      } else {
         this.add(var1.getStringID());
      }
   }

   public int get(String var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return (Integer)this.counts.getOrDefault(var1, 0);
      }
   }

   public void forEach(BiConsumer<String, Integer> var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         this.counts.forEach(var1);
      }
   }

   public int getTotal() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.total;
      }
   }

   public void combine(PlayerStat var1) {
      if (var1 instanceof ItemCountStat) {
         ItemCountStat var2 = (ItemCountStat)var1;
         var2.counts.forEach((var1x, var2x) -> {
            this.set(var1x, (Integer)this.counts.getOrDefault(var1x, 0) + var2x, true);
         });
      }

   }

   public void resetCombine() {
      this.counts.clear();
      this.total = 0;
   }

   protected void updateSteam() {
      if (this.parent.controlAchievements) {
         SteamData.setStat(this.stringID, this.total);
      }
   }

   public void loadSteam(SteamGameStats var1) {
      this.total = var1.getStatByName(this.stringID, this.total);
   }

   public void addSaveData(SaveData var1) {
      HashMap var10000 = this.counts;
      Objects.requireNonNull(var1);
      var10000.forEach(var1::addInt);
   }

   public void applyLoadData(LoadData var1) {
      this.counts.clear();
      this.total = 0;
      Iterator var2 = var1.getLoadData().iterator();

      while(var2.hasNext()) {
         LoadData var3 = (LoadData)var2.next();
         if (var3.isData()) {
            try {
               int var4 = LoadData.getInt(var3);
               this.set(var3.getName(), var4, false);
            } catch (NumberFormatException var5) {
               GameLog.warn.println("Could not load " + this.stringID + " stat number: " + var3.getData());
            }
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.counts.size());
      this.counts.forEach((var1x, var2) -> {
         var1.putNextShortUnsigned(ItemRegistry.getItemID(var1x));
         var1.putNextInt(var2);
      });
   }

   public void applyContentPacket(PacketReader var1) {
      this.counts.clear();
      this.total = 0;
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = ItemRegistry.getItemStringID(var4);
         this.set(var5, var1.getNextInt(), true);
      }

   }

   public void setupDirtyPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.dirty.size());
      Iterator var2 = this.dirty.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.putNextShortUnsigned(ItemRegistry.getItemID(var3));
         var1.putNextInt((Integer)this.counts.getOrDefault(var3, 0));
      }

   }

   public void applyDirtyPacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = ItemRegistry.getItemStringID(var4);
         this.set(var5, var1.getNextInt(), true);
      }

   }
}
