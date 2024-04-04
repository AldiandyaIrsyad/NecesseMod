package necesse.engine.playerStats.stats;

import java.util.HashSet;
import java.util.Iterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.playerStats.PlayerStat;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.steam.SteamData;
import necesse.engine.steam.SteamGameStats;

public class ItemsObtainedStat extends PlayerStat {
   protected HashSet<String> dirtyItems = new HashSet();
   protected HashSet<String> statItems = new HashSet();
   protected HashSet<String> allItems = new HashSet();

   public ItemsObtainedStat(PlayerStats var1, String var2) {
      super(var1, var2);
   }

   public void clean() {
      super.clean();
      this.dirtyItems.clear();
   }

   protected void addItem(String var1, boolean var2) {
      if (!this.allItems.contains(var1)) {
         if (ItemRegistry.itemExists(var1)) {
            int var3 = ItemRegistry.getItemID(var1);
            this.allItems.add(var1);
            if (ItemRegistry.countsInStats(var3)) {
               this.statItems.add(var1);
            }

            if (var2) {
               this.updateSteam();
            }

            this.markImportantDirty();
            this.dirtyItems.add(var1);
         }

      }
   }

   public void addItem(String var1) {
      if (this.parent.mode == PlayerStats.Mode.READ_ONLY) {
         throw new IllegalStateException("Cannot set read only stats");
      } else {
         this.addItem(var1, true);
      }
   }

   public boolean isItemObtained(String var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.allItems.contains(var1);
      }
   }

   public boolean isStatItemObtained(String var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.statItems.contains(var1);
      }
   }

   public Iterable<String> getItemsObtained() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.allItems;
      }
   }

   public Iterable<String> getStatItemsObtained() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.statItems;
      }
   }

   public int getTotalItems() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.allItems.size();
      }
   }

   public int getTotalStatItems() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.statItems.size();
      }
   }

   public void combine(PlayerStat var1) {
      if (var1 instanceof ItemsObtainedStat) {
         ItemsObtainedStat var2 = (ItemsObtainedStat)var1;
         Iterator var3 = var2.allItems.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            this.addItem(var4, true);
         }
      }

   }

   public void resetCombine() {
   }

   protected void updateSteam() {
      if (this.parent.controlAchievements) {
         SteamData.setStat("items_obtained", this.statItems.size());
      }
   }

   public void loadSteam(SteamGameStats var1) {
   }

   public void addSaveData(SaveData var1) {
      if (!this.allItems.isEmpty()) {
         var1.addStringHashSet("items", this.allItems);
      }
   }

   public void applyLoadData(LoadData var1) {
      Iterator var2 = var1.getStringHashSet("items", new HashSet()).iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (!var3.isEmpty()) {
            this.addItem(var3, false);
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.allItems.size());
      Iterator var2 = this.allItems.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.putNextShortUnsigned(ItemRegistry.getItemID(var3));
      }

   }

   public void applyContentPacket(PacketReader var1) {
      this.allItems.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = ItemRegistry.getItemStringID(var4);
         this.addItem(var5, true);
      }

   }

   public void setupDirtyPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.dirtyItems.size());
      Iterator var2 = this.dirtyItems.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.putNextShortUnsigned(ItemRegistry.getItemID(var3));
      }

   }

   public void applyDirtyPacket(PacketReader var1) {
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = ItemRegistry.getItemStringID(var4);
         this.addItem(var5, true);
      }

   }
}
