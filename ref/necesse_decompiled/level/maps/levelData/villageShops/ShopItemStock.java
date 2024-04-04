package necesse.level.maps.levelData.villageShops;

import java.util.Objects;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryItem;

public class ShopItemStock {
   private InventoryItem item;
   private int defaultStock;
   private int currentStock;

   public ShopItemStock(String var1, int var2) {
      this(new InventoryItem(var1), var2);
   }

   public ShopItemStock(InventoryItem var1, int var2) {
      this.item = var1;
      this.defaultStock = var2;
      this.currentStock = var2;
   }

   public ShopItemStock(LoadData var1) {
      this.applyLoadData(var1);
   }

   public void addSaveData(SaveData var1) {
      SaveData var2 = new SaveData("item");
      this.item.addSaveData(var2);
      var1.addSaveData(var2);
      var1.addInt("defaultStock", this.defaultStock);
      var1.addInt("currentStock", this.currentStock);
   }

   public void applyLoadData(LoadData var1) {
      LoadData var2 = var1.getFirstLoadDataByName("item");
      this.item = InventoryItem.fromLoadData(var2);
      Objects.requireNonNull(this.item);
      this.defaultStock = var1.getInt("defaultStock", this.defaultStock);
      this.currentStock = var1.getInt("currentStock", this.defaultStock);
   }

   public InventoryItem getItem() {
      return this.item;
   }

   public int getCurrentStock() {
      return this.currentStock;
   }
}
