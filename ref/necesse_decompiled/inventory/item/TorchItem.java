package necesse.inventory.item;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public interface TorchItem {
   boolean canPlaceTorch(Level var1, int var2, int var3, InventoryItem var4, PlayerMob var5);

   int getTorchPlaceRange(Level var1, InventoryItem var2, PlayerMob var3);
}
