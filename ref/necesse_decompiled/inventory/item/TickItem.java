package necesse.inventory.item;

import java.util.function.Consumer;
import necesse.engine.GameState;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public interface TickItem {
   void tick(Inventory var1, int var2, InventoryItem var3, GameClock var4, GameState var5, Entity var6, WorldSettings var7, Consumer<InventoryItem> var8);
}
