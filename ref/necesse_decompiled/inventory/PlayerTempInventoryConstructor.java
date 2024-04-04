package necesse.inventory;

import necesse.entity.mobs.PlayerMob;

@FunctionalInterface
public interface PlayerTempInventoryConstructor {
   PlayerTempInventory create(PlayerMob var1, int var2, int var3);
}
