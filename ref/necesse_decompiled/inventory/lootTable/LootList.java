package necesse.inventory.lootTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class LootList {
   protected TreeSet<Integer> itemIDs = new TreeSet();
   protected ArrayList<InventoryItem> customItems = new ArrayList();

   public LootList() {
   }

   public LootList add(int var1) {
      if (var1 != -1) {
         this.itemIDs.add(var1);
      }

      return this;
   }

   public LootList add(Item var1) {
      return var1 == null ? this : this.add(var1.getID());
   }

   public LootList add(String var1) {
      return this.add(ItemRegistry.getItemID(var1));
   }

   public LootList addCustom(InventoryItem var1) {
      if (var1 == null) {
         return this;
      } else {
         var1.combineOrAddToList((Level)null, (PlayerMob)null, this.customItems, "add");
         return this;
      }
   }

   public Iterable<Integer> getItemIDs() {
      return this.itemIDs;
   }

   public Iterable<Item> getItems() {
      return GameUtils.mapIterable(this.itemIDs.iterator(), ItemRegistry::getItem);
   }

   public IntStream streamItemIDs() {
      return this.itemIDs.stream().mapToInt((var0) -> {
         return var0;
      });
   }

   public Stream<Item> streamItems() {
      return this.itemIDs.stream().map(ItemRegistry::getItem);
   }

   public Iterable<InventoryItem> getCustomItems() {
      return GameUtils.mapIterable(this.customItems.iterator(), InventoryItem::copy);
   }

   public Stream<InventoryItem> streamCustomItems() {
      return this.customItems.stream().map(InventoryItem::copy);
   }

   public Stream<InventoryItem> streamItemsAndCustomItems() {
      return Stream.concat(this.streamItems().map((var0) -> {
         return var0.getDefaultItem((PlayerMob)null, 1);
      }), this.streamCustomItems());
   }

   public Iterable<InventoryItem> getCombinedItemsAndCustomItems() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.getItems().iterator();

      while(var2.hasNext()) {
         Item var3 = (Item)var2.next();
         var3.getDefaultItem((PlayerMob)null, 1).combineOrAddToList((Level)null, (PlayerMob)null, var1, "add");
      }

      var2 = this.getCustomItems().iterator();

      while(var2.hasNext()) {
         InventoryItem var4 = (InventoryItem)var2.next();
         var4.combineOrAddToList((Level)null, (PlayerMob)null, var1, "add");
      }

      return var1;
   }
}
