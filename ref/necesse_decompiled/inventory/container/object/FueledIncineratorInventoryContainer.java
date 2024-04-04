package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.FueledIncineratorObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.slots.OEInventoryContainerSlot;

public class FueledIncineratorInventoryContainer extends OEInventoryContainer {
   public final FueledIncineratorObjectEntity incineratorObjectEntity;
   public int FUEL_START = -1;
   public int FUEL_END = -1;

   public FueledIncineratorInventoryContainer(NetworkClient var1, int var2, FueledIncineratorObjectEntity var3, PacketReader var4) {
      super(var1, var2, var3, var4);
      this.incineratorObjectEntity = var3;

      for(int var5 = 0; var5 < var3.fuelSlots; ++var5) {
         int var6 = this.addSlot(new OEInventoryContainerSlot(var3, var5));
         if (this.FUEL_START == -1) {
            this.FUEL_START = var6;
         }

         if (this.FUEL_END == -1) {
            this.FUEL_END = var6;
         }

         this.FUEL_START = Math.min(this.FUEL_START, var6);
         this.FUEL_END = Math.max(this.FUEL_END, var6);
      }

      this.addInventoryQuickTransfer(this.FUEL_START, this.FUEL_END);
   }

   public InventoryRange getOEInventoryRange() {
      Inventory var1 = this.oeInventory.getInventory();
      return new InventoryRange(var1, ((FueledIncineratorObjectEntity)this.objectEntity).fuelSlots, var1.getSize() - 1);
   }
}
