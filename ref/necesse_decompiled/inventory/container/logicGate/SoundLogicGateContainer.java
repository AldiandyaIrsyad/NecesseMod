package necesse.inventory.container.logicGate;

import java.awt.Point;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.level.gameLogicGate.entities.SoundLogicGateEntity;

public class SoundLogicGateContainer extends Container {
   public SoundLogicGateEntity entity;
   public final IntCustomAction setSound;
   public final IntCustomAction setSemitone;

   public SoundLogicGateContainer(final NetworkClient var1, int var2, final SoundLogicGateEntity var3) {
      super(var1, var2);
      this.entity = var3;
      this.setSound = (IntCustomAction)this.registerAction(new IntCustomAction() {
         protected void run(int var1x) {
            var3.sound = var1x;
            if (var1.isServer()) {
               var3.sendUpdatePacket();
            }

         }
      });
      this.setSemitone = (IntCustomAction)this.registerAction(new IntCustomAction() {
         protected void run(int var1x) {
            var3.semitone = var1x;
            if (var1.isServer()) {
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
