package necesse.entity.objectEntity;

import necesse.engine.network.packet.PacketSpawnFirework;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;
import necesse.level.maps.Level;

public class FireworkDispenserObjectEntity extends InventoryObjectEntity {
   public boolean onCooldown = false;

   public FireworkDispenserObjectEntity(Level var1, int var2, int var3) {
      super(var1, var2, var3, 10);
   }

   public void fire() {
      if (this.isServer() && !this.onCooldown) {
         for(int var1 = 0; var1 < this.inventory.getSize(); ++var1) {
            if (!this.inventory.isSlotClear(var1)) {
               InventoryItem var2 = this.inventory.getItem(var1);
               if (var2.item instanceof FireworkPlaceableItem) {
                  if (this.isServer()) {
                     this.getLevel().getServer().network.sendToClientsWithTile(new PacketSpawnFirework(this.getLevel(), (float)(this.getX() * 32 + 16), (float)(this.getY() * 32 + 16), GameRandom.globalRandom.getIntBetween(600, 700), (float)GameRandom.globalRandom.getIntBetween(150, 250), var2.getGndData(), GameRandom.globalRandom.nextInt()), this.getLevel(), this.getTileX(), this.getTileY());
                     this.inventory.setAmount(var1, this.inventory.getAmount(var1) - 1);
                     if (this.inventory.getAmount(var1) <= 0) {
                        this.inventory.clearSlot(var1);
                     }
                  }

                  this.onCooldown = true;
                  break;
               }
            }
         }

      }
   }

   public void serverTick() {
      super.serverTick();
      this.onCooldown = false;
   }

   public void clientTick() {
      super.clientTick();
      this.onCooldown = false;
   }

   public boolean isItemValid(int var1, InventoryItem var2) {
      return var2 != null ? var2.item instanceof FireworkPlaceableItem : true;
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
}
