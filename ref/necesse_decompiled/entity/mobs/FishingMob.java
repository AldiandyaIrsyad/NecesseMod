package necesse.entity.mobs;

import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.biomes.FishingLootTable;
import necesse.level.maps.biomes.FishingSpot;

public interface FishingMob {
   void giveBaitBack(BaitItem var1);

   void stopFishing();

   void showFishingWaitAnimation(FishingRodItem var1, int var2, int var3);

   boolean isFishingSwingDone();

   default FishingLootTable getFishingLootTable(FishingSpot var1) {
      return var1.tile.level.biome.getFishingLootTable(var1);
   }

   default void giveCaughtItem(FishingEvent var1, InventoryItem var2) {
      ItemPickupEntity var3;
      var1.level.entityManager.pickups.add(var3 = var2.getPickupEntity(var1.level, (float)var1.getMob().getX(), (float)var1.getMob().getY()));
      var3.pickupCooldown = 0;
   }
}
