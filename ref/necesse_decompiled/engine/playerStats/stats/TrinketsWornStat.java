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
import necesse.engine.steam.SteamGameStats;
import necesse.inventory.item.Item;

public class TrinketsWornStat extends PlayerStat {
   protected HashSet<String> dirtyTrinkets = new HashSet();
   protected HashSet<String> trinkets = new HashSet();

   public TrinketsWornStat(PlayerStats var1, String var2) {
      super(var1, var2);
   }

   public void clean() {
      super.clean();
      this.dirtyTrinkets.clear();
   }

   protected void addTrinketWorn(String var1, boolean var2) {
      if (!this.trinkets.contains(var1)) {
         if (ItemRegistry.itemExists(var1)) {
            Item var3 = ItemRegistry.getItem(var1);
            if (var3.isTrinketItem()) {
               this.trinkets.add(var1);
               if (var2) {
                  this.updateSteam();
               }

               this.dirtyTrinkets.add(var1);
               this.markImportantDirty();
            }
         }

      }
   }

   public void addTrinketWorn(Item var1) {
      if (this.parent.mode == PlayerStats.Mode.READ_ONLY) {
         throw new IllegalStateException("Cannot set read only stats");
      } else if (var1.isTrinketItem()) {
         this.addTrinketWorn(var1.getStringID(), true);
      }
   }

   public boolean isTrinketWorn(String var1) {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.trinkets.contains(var1);
      }
   }

   public Iterable<String> getTrinketsWorn() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.trinkets;
      }
   }

   public int getTotalTrinketsWorn() {
      if (this.parent.mode == PlayerStats.Mode.WRITE_ONLY) {
         throw new IllegalStateException("Cannot get write only stats");
      } else {
         return this.trinkets.size();
      }
   }

   public void combine(PlayerStat var1) {
      if (var1 instanceof TrinketsWornStat) {
         TrinketsWornStat var2 = (TrinketsWornStat)var1;
         Iterator var3 = var2.trinkets.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            this.addTrinketWorn(var4, true);
         }
      }

   }

   public void resetCombine() {
   }

   protected void updateSteam() {
      if (this.parent.controlAchievements) {
         ;
      }
   }

   public void loadSteam(SteamGameStats var1) {
   }

   public void addSaveData(SaveData var1) {
      if (!this.trinkets.isEmpty()) {
         var1.addStringHashSet("trinkets", this.trinkets);
      }
   }

   public void applyLoadData(LoadData var1) {
      Iterator var2 = var1.getStringHashSet("trinkets", new HashSet()).iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (!var3.isEmpty()) {
            this.addTrinketWorn(var3, false);
         }
      }

   }

   public void setupContentPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.trinkets.size());
      Iterator var2 = this.trinkets.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.putNextShortUnsigned(ItemRegistry.getItemID(var3));
      }

   }

   public void applyContentPacket(PacketReader var1) {
      this.trinkets.clear();
      int var2 = var1.getNextShortUnsigned();

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         String var5 = ItemRegistry.getItemStringID(var4);
         this.addTrinketWorn(var5, true);
      }

   }

   public void setupDirtyPacket(PacketWriter var1) {
      var1.putNextShortUnsigned(this.dirtyTrinkets.size());
      Iterator var2 = this.dirtyTrinkets.iterator();

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
         this.addTrinketWorn(var5, true);
      }

   }
}
