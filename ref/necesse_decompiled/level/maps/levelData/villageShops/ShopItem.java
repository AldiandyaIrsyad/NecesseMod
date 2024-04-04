package necesse.level.maps.levelData.villageShops;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class ShopItem {
   public final int stringIDHash;
   public final InventoryItem item;
   public final int price;
   public int currentStock;

   private ShopItem(int var1, InventoryItem var2, int var3, int var4) {
      this.stringIDHash = var1;
      this.item = var2;
      if (var3 > 0 && (float)var3 < var2.getBrokerValue()) {
         GameLog.warn.println("Added shop " + var2.getItemDisplayName() + " that's below broker value, setting it to broker value.");
         this.price = Math.max(1, (int)Math.ceil((double)var2.getBrokerValue()));
      } else {
         this.price = var3;
      }

      this.currentStock = var4;
   }

   public static void addStockedItem(List<ShopItem> var0, VillageShopsData var1, String var2, int var3) {
      ShopItem var4 = stockedItem(var1, var2, var3);
      if (var4 != null) {
         var0.add(var4);
      } else {
         int var5 = ItemRegistry.getItemID(var2);
         if (var5 != -1) {
            var0.add(item(var2, var3));
            if (GlobalData.isDevMode()) {
               GameLog.warn.println("Could not find stocked item with stringID " + var2 + ", adding generic item instead");
            }
         } else if (GlobalData.isDevMode()) {
            GameLog.warn.println("Could not find stocked item with stringID " + var2);
         }
      }

   }

   public static ShopItem stockedItem(VillageShopsData var0, String var1, int var2) {
      ShopItemStock var3 = var0.getShopItem(var1);
      return var3 != null ? new ShopItem(var1.hashCode(), var3.getItem(), var2, var3.getCurrentStock()) : null;
   }

   public static ShopItem item(InventoryItem var0, int var1) {
      return new ShopItem(-1, var0, var1, -1);
   }

   public static ShopItem item(String var0, int var1) {
      return item(new InventoryItem(var0), var1);
   }

   public void addPacketContent(PacketWriter var1) {
      var1.putNextInt(this.stringIDHash);
      this.item.addPacketContent(var1);
      var1.putNextInt(this.price);
      if (this.stringIDHash != -1) {
         var1.putNextInt(this.currentStock);
      }

   }

   public ShopItem(PacketReader var1) {
      this.stringIDHash = var1.getNextInt();
      this.item = InventoryItem.fromContentPacket(var1);
      this.price = var1.getNextInt();
      if (this.stringIDHash != -1) {
         this.currentStock = var1.getNextInt();
      } else {
         this.currentStock = -1;
      }

   }

   public GameTooltips getTooltips(PlayerMob var1, GameBlackboard var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      if (this.price < 0) {
         var3.add(Localization.translate("ui", "shopselltip", "coins", Math.abs(this.price), "amount", this.item.getAmount()));
         var3.add((Object)this.item.getTooltip(var1, var2));
      } else {
         var3.add(Localization.translate("ui", "shopbuytip", "coins", Math.abs(this.price), "amount", this.item.getAmount()));
         var3.add((Object)this.item.getTooltip(var1, var2));
      }

      return var3;
   }

   public boolean canTrade(NetworkClient var1, Collection<Inventory> var2) {
      return this.canAfford(var1, var2);
   }

   public boolean canAfford(NetworkClient var1, Collection<Inventory> var2) {
      if (this.price == 0) {
         return true;
      } else {
         int var3 = this.price < 0 ? this.item.getAmount() : this.price;
         int var4 = 0;
         Item var5 = this.price < 0 ? this.item.item : ItemRegistry.getItem("coin");
         Iterator var6 = var2.iterator();

         while(var6.hasNext()) {
            Inventory var7 = (Inventory)var6.next();
            if (var7.canBeUsedForCrafting()) {
               var4 += var7.getAmount(var1.playerMob.getLevel(), var1.playerMob, var5, this.price < 0 ? "sell" : "buy");
               if (var4 >= var3) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public void consumePrice(NetworkClient var1, Collection<Inventory> var2) {
      if (this.price != 0) {
         int var3 = this.price < 0 ? this.item.getAmount() : Math.abs(this.price);
         Item var4 = this.price < 0 ? this.item.item : ItemRegistry.getItem("coin");
         Iterator var5 = var2.iterator();

         while(var5.hasNext()) {
            Inventory var6 = (Inventory)var5.next();
            if (var6.canBeUsedForCrafting()) {
               var3 -= var6.removeItems(var1.playerMob.getLevel(), var1.playerMob, var4, var3, this.price < 0 ? "sell" : "buy");
               if (var3 <= 0) {
                  break;
               }
            }
         }

      }
   }

   public void onTrade(NetworkClient var1) {
      if (var1.isServer()) {
         if (this.price > 0) {
            var1.getServerClient().newStats.items_bought.increment(this.item.getAmount());
            var1.getServerClient().newStats.money_spent.increment(this.price);
         } else {
            var1.getServerClient().newStats.items_sold.increment(this.item.getAmount());
            var1.getServerClient().newStats.money_earned.increment(Math.abs(this.price));
         }
      }

   }
}
