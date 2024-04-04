package necesse.inventory.item.upgradeUtils;

import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTipList;

public interface UpgradableItem {
   default String getCanBeUpgradedError(InventoryItem var1) {
      return null;
   }

   void addUpgradeStatTips(ItemStatTipList var1, InventoryItem var2, InventoryItem var3, Mob var4, Mob var5);

   UpgradedItem getUpgradedItem(InventoryItem var1);
}
