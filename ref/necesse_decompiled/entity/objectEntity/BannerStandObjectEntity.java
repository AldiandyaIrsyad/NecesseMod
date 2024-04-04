package necesse.entity.objectEntity;

import necesse.engine.util.GameUtils;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.BannerItem;
import necesse.level.maps.Level;

public class BannerStandObjectEntity extends InventoryObjectEntity {
   public BannerStandObjectEntity(Level var1, int var2, int var3) {
      super(var1, var2, var3, 1);
   }

   public boolean isItemValid(int var1, InventoryItem var2) {
      return var2 != null ? var2.item instanceof BannerItem : true;
   }

   public InventoryRange getSettlementStorage() {
      return null;
   }

   public boolean canQuickStackInventory() {
      return false;
   }

   public boolean canRestockInventory() {
      return false;
   }

   public boolean canSortInventory() {
      return false;
   }

   public boolean canUseForNearbyCrafting() {
      return false;
   }

   public boolean canSetInventoryName() {
      return false;
   }

   public void clientTick() {
      super.clientTick();
      this.tickBuffs();
   }

   public void serverTick() {
      super.serverTick();
      this.tickBuffs();
   }

   public void tickBuffs() {
      if (!this.inventory.isSlotClear(0)) {
         Item var1 = this.inventory.getItemSlot(0);
         if (var1 instanceof BannerItem) {
            BannerItem var2 = (BannerItem)var1;
            int var3 = (int)((float)var2.range * 1.5F);
            GameUtils.streamNetworkClients(this.getLevel()).filter((var2x) -> {
               return var2x.playerMob.getDistance((float)(this.getX() * 32 + 16), (float)(this.getY() * 32 + 16)) <= (float)var3;
            }).forEach((var1x) -> {
               var2.applyBuffs(var1x.playerMob);
            });
         }
      }

   }
}
