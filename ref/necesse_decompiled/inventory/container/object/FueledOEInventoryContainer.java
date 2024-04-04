package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.container.customAction.BooleanCustomAction;

public class FueledOEInventoryContainer extends OEInventoryContainer {
   public final FueledInventoryObjectEntity objectEntity;
   public BooleanCustomAction setKeepRunning;

   public FueledOEInventoryContainer(NetworkClient var1, int var2, OEInventory var3, PacketReader var4) {
      super(var1, var2, var3, var4);
      this.objectEntity = (FueledInventoryObjectEntity)var3;
      this.setKeepRunning = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            FueledOEInventoryContainer.this.objectEntity.keepRunning = var1;
            FueledOEInventoryContainer.this.objectEntity.markFuelDirty();
         }
      });
   }
}
