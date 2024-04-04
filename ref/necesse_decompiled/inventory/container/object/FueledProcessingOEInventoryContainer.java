package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.objectEntity.FueledProcessingInventoryObjectEntity;
import necesse.inventory.container.customAction.BooleanCustomAction;

public class FueledProcessingOEInventoryContainer extends OEInventoryContainer {
   public final FueledProcessingInventoryObjectEntity fueledProcessingObjectEntity;
   public BooleanCustomAction setKeepRunning;

   public FueledProcessingOEInventoryContainer(NetworkClient var1, int var2, FueledProcessingInventoryObjectEntity var3, PacketReader var4) {
      super(var1, var2, var3, var4);
      this.fueledProcessingObjectEntity = var3;
      this.setKeepRunning = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1) {
            if (FueledProcessingOEInventoryContainer.this.fueledProcessingObjectEntity.shouldBeAbleToChangeKeepFuelRunning()) {
               FueledProcessingOEInventoryContainer.this.fueledProcessingObjectEntity.setKeepFuelRunning(var1);
            }

         }
      });
   }
}
