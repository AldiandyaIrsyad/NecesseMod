package necesse.inventory.item.upgradeUtils;

import java.util.Collection;
import necesse.engine.localization.Localization;
import necesse.inventory.InventoryItem;

public interface SalvageableItem {
   default String getCanBeSalvagedError(InventoryItem var1) {
      return var1.item.getUpgradeTier(var1) < 1.0F ? Localization.translate("ui", "itemnotsalvageable") : null;
   }

   Collection<InventoryItem> getSalvageRewards(InventoryItem var1);
}
