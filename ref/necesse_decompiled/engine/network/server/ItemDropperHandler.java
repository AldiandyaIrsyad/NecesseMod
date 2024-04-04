package necesse.engine.network.server;

import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public interface ItemDropperHandler {
   void dropItem(InventoryItem var1, PlayerInventorySlot var2, boolean var3);
}
