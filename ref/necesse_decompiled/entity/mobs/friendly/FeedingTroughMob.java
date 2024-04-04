package necesse.entity.mobs.friendly;

import necesse.inventory.InventoryItem;

public interface FeedingTroughMob {
   InventoryItem onFed(InventoryItem var1);

   boolean canFeed(InventoryItem var1);

   boolean isOnFeedCooldown();
}
