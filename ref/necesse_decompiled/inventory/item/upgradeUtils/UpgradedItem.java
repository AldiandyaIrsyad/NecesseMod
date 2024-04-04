package necesse.inventory.item.upgradeUtils;

import java.util.Objects;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.Level;

public class UpgradedItem {
   public final InventoryItem lastItem;
   public final InventoryItem upgradedItem;
   public final Ingredient[] cost;

   public UpgradedItem(InventoryItem var1, InventoryItem var2, Ingredient[] var3) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      Objects.requireNonNull(var3);
      this.lastItem = var1;
      this.upgradedItem = var2;
      this.cost = var3;
   }

   public boolean isSameUpgrade(UpgradedItem var1, Level var2) {
      if (var1 == this) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!this.upgradedItem.equals(var2, var1.upgradedItem, false, false, "equals")) {
         return false;
      } else if (this.cost.length != var1.cost.length) {
         return false;
      } else {
         for(int var3 = 0; var3 < this.cost.length; ++var3) {
            if (!this.cost[var3].equals(var1.cost[var3])) {
               return false;
            }
         }

         return true;
      }
   }
}
