package necesse.entity.pickup;

import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class QuestItemPickupEntity extends ItemPickupEntity {
   public QuestItemPickupEntity() {
   }

   public QuestItemPickupEntity(Level var1, InventoryItem var2, float var3, float var4, float var5, float var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public void onPickup(ServerClient var1) {
      if (var1.playerMob.getInv().getAmount(this.item.item, false, true, true, "questdrop") <= 0) {
         super.onPickup(var1);
      } else {
         this.remove();
      }

   }

   public boolean collidesWith(PickupEntity var1) {
      return var1.getID() != this.getID() && super.collidesWith(var1);
   }
}
