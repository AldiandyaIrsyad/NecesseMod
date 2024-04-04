package necesse.inventory.item.miscItem;

import necesse.engine.GameState;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public interface InternalInventoryItemInterface {
   int getInternalInventorySize();

   default Inventory getInternalInventory(InventoryItem var1) {
      GNDItem var2 = var1.getGndData().getItem("inventory");
      if (var2 instanceof GNDItemInventory) {
         GNDItemInventory var4 = (GNDItemInventory)var2;
         if (var4.inventory.getSize() != this.getInternalInventorySize()) {
            var4.inventory.changeSize(this.getInternalInventorySize());
         }

         return var4.inventory;
      } else {
         Inventory var3 = new Inventory(this.getInternalInventorySize());
         var1.getGndData().setItem("inventory", new GNDItemInventory(var3));
         return var3;
      }
   }

   default void tickInternalInventory(InventoryItem var1, GameClock var2, GameState var3, Entity var4, WorldSettings var5) {
      this.getInternalInventory(var1).tickItems(var2, var3, var4, var5);
   }

   default void saveInternalInventory(InventoryItem var1, Inventory var2) {
      setInternalInventory(var1, var2);
   }

   default boolean isValidItem(InventoryItem var1) {
      return true;
   }

   static void setInternalInventory(InventoryItem var0, Inventory var1) {
      GNDItem var2 = var0.getGndData().getItem("inventory");
      if (var2 instanceof GNDItemInventory) {
         GNDItemInventory var3 = (GNDItemInventory)var2;
         var3.inventory.override(var1, true, true);
      } else {
         var0.getGndData().setItem("inventory", new GNDItemInventory(var1));
      }

   }

   default boolean canQuickStackInventory() {
      return true;
   }

   default boolean canRestockInventory() {
      return true;
   }

   default boolean canSortInventory() {
      return true;
   }

   default boolean canChangePouchName() {
      return true;
   }

   default String getPouchName(InventoryItem var1) {
      return var1.getGndData().getString("pouchName", (String)null);
   }

   default void setPouchName(InventoryItem var1, String var2) {
      if (!var2.isEmpty() && !var2.equals(ItemRegistry.getLocalization(var1.item.getID()).translate())) {
         var1.getGndData().setString("pouchName", var2);
      } else {
         var1.getGndData().setItem("pouchName", (GNDItem)null);
      }

   }
}
