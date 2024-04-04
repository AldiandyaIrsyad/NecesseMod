package necesse.inventory.container.logicGate;

import java.awt.Point;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.container.Container;
import necesse.level.gameLogicGate.entities.TFlipFlopLogicGateEntity;

public class TFlipFlopLogicGateContainer extends Container {
   public TFlipFlopLogicGateEntity entity;
   public final WireSelectCustomAction setInputs;
   public final WireSelectCustomAction setOutputs1;
   public final WireSelectCustomAction setOutputs2;

   public TFlipFlopLogicGateContainer(final NetworkClient var1, int var2, final TFlipFlopLogicGateEntity var3) {
      super(var1, var2);
      this.entity = var3;
      this.setInputs = (WireSelectCustomAction)this.registerAction(new WireSelectCustomAction() {
         protected void run(boolean[] var1x) {
            var3.wireInputs = var1x;
            if (var1.isServer()) {
               var3.updateOutputs(false);
               var3.sendUpdatePacket();
            }

         }
      });
      this.setOutputs1 = (WireSelectCustomAction)this.registerAction(new WireSelectCustomAction() {
         protected void run(boolean[] var1x) {
            var3.wireOutputs1 = var1x;
            if (var1.isServer()) {
               var3.updateOutputs(false);
               var3.sendUpdatePacket();
            }

         }
      });
      this.setOutputs2 = (WireSelectCustomAction)this.registerAction(new WireSelectCustomAction() {
         protected void run(boolean[] var1x) {
            var3.wireOutputs2 = var1x;
            if (var1.isServer()) {
               var3.updateOutputs(false);
               var3.sendUpdatePacket();
            }

         }
      });
   }

   public boolean isValid(ServerClient var1) {
      if (!super.isValid(var1)) {
         return false;
      } else {
         return !this.entity.isRemoved() && (new Point(this.entity.tileX * 32 + 16, this.entity.tileY * 32 + 16)).distance((double)var1.playerMob.getX(), (double)var1.playerMob.getY()) <= (double)PlaceableItemInterface.getPlaceRange(var1.playerMob);
      }
   }
}
