package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.Level;

public class SettlementInventory extends SettlementOEInventory {
   public ItemCategoriesFilter filter;
   public int priority;
   public SettlementRequestInventory fuelInventory;

   public static GameMessage getPriorityText(int var0) {
      Priority[] var1 = SettlementInventory.Priority.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Priority var4 = var1[var3];
         if (var4.priorityValue == var0) {
            return var4.displayName;
         }
      }

      return new StaticMessage("" + var0);
   }

   private SettlementInventory(Level var1, int var2, int var3, boolean var4) {
      super(var1, var2, var3, var4);
      this.filter = new ItemCategoriesFilter(true) {
         public boolean isItemDisabled(Item var1) {
            if (super.isItemDisabled(var1)) {
               return true;
            } else {
               return SettlementInventory.this.oeInventory != null && SettlementInventory.this.oeInventory.isSettlementStorageItemDisabled(var1);
            }
         }
      };
      this.priority = 0;
      if (var4 && this.oeInventory != null) {
         this.oeInventory.setupDefaultSettlementStorage(this);
      }

   }

   public SettlementInventory(Level var1, int var2, int var3) {
      this(var1, var2, var3, true);
   }

   public void addSaveData(SaveData var1) {
      var1.addPoint("tile", new Point(this.tileX, this.tileY));
      SaveData var2 = new SaveData("filter");
      this.filter.addSaveData(var2);
      if (!var2.isEmpty()) {
         var1.addSaveData(var2);
      }

      var1.addInt("priority", this.priority);
   }

   public static SettlementInventory fromLoadData(Level var0, LoadData var1) throws LoadDataException {
      Point var2 = var1.getPoint("tile", (Point)null);
      if (var2 == null) {
         throw new LoadDataException("Missing position");
      } else {
         SettlementInventory var3 = new SettlementInventory(var0, var2.x, var2.y, false);
         LoadData var4 = var1.getFirstLoadDataByName("filters");
         if (var4 != null) {
            var3.filter.applyLoadData(var4);
         }

         LoadData var5 = var1.getFirstLoadDataByName("filter");
         if (var5 != null) {
            var3.filter.applyLoadData(var5);
         }

         var3.priority = var1.getInt("priority", 0, false);
         return var3;
      }
   }

   public InventoryRange getInventoryRange() {
      this.refreshOEInventory();
      return this.oeInventory == null ? null : this.oeInventory.getSettlementStorage();
   }

   public SettlementRequestInventory getFuelInventory() {
      this.refreshOEInventory();
      if (this.oeInventory == null) {
         return null;
      } else {
         SettlementRequestOptions var1 = this.oeInventory.getSettlementFuelRequestOptions();
         if (var1 != null) {
            if (this.fuelInventory == null) {
               this.fuelInventory = new SettlementRequestInventory(this.level, this.tileX, this.tileY, var1) {
                  public InventoryRange getInventoryRange() {
                     this.refreshOEInventory();
                     return this.oeInventory == null ? null : this.oeInventory.getSettlementFuelInventoryRange();
                  }
               };
            }

            this.fuelInventory.filter = new ItemCategoriesFilter(var1.minAmount, var1.maxAmount, true);
            if (!this.fuelInventory.isValid()) {
               this.fuelInventory = null;
            }

            return this.fuelInventory;
         } else {
            this.fuelInventory = null;
            return null;
         }
      }
   }

   public ItemCategoriesFilter getFilter() {
      return this.filter;
   }

   public static enum Priority {
      TOP(300, new LocalMessage("ui", "prioritytop")),
      HIGH(200, new LocalMessage("ui", "priorityhigh")),
      HIGHER(100, new LocalMessage("ui", "priorityhigher")),
      NORMAL(0, new LocalMessage("ui", "prioritynormal")),
      LOWER(-100, new LocalMessage("ui", "prioritylower")),
      LOW(-200, new LocalMessage("ui", "prioritylow")),
      LAST(-300, new LocalMessage("ui", "prioritylast"));

      public final int priorityValue;
      public final GameMessage displayName;

      private Priority(int var3, GameMessage var4) {
         this.priorityValue = var3;
         this.displayName = var4;
      }

      // $FF: synthetic method
      private static Priority[] $values() {
         return new Priority[]{TOP, HIGH, HIGHER, NORMAL, LOWER, LOW, LAST};
      }
   }
}
