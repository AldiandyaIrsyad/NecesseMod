package necesse.inventory.container.logicGate;

import java.awt.Point;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.level.gameLogicGate.entities.SensorLogicGateEntity;

public class SensorLogicGateContainer extends Container {
   public SensorLogicGateEntity entity;
   public final WireSelectCustomAction setOutputs;
   public final BooleanCustomAction setPlayers;
   public final BooleanCustomAction setPassiveMobs;
   public final BooleanCustomAction setHostileMobs;
   public final IntCustomAction setRange;

   public SensorLogicGateContainer(final NetworkClient var1, int var2, final SensorLogicGateEntity var3) {
      super(var1, var2);
      this.entity = var3;
      this.setOutputs = (WireSelectCustomAction)this.registerAction(new WireSelectCustomAction() {
         protected void run(boolean[] var1x) {
            var3.wireOutputs = var1x;
            if (var1.isServer()) {
               var3.updateOutputs(false);
               var3.sendUpdatePacket();
            }

         }
      });
      this.setPlayers = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1x) {
            var3.players = var1x;
            if (var1.isServer()) {
               var3.sendUpdatePacket();
            }

         }
      });
      this.setPassiveMobs = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1x) {
            var3.passiveMobs = var1x;
            if (var1.isServer()) {
               var3.sendUpdatePacket();
            }

         }
      });
      this.setHostileMobs = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
         protected void run(boolean var1x) {
            var3.hostileMobs = var1x;
            if (var1.isServer()) {
               var3.sendUpdatePacket();
            }

         }
      });
      this.setRange = (IntCustomAction)this.registerAction(new IntCustomAction() {
         protected void run(int var1x) {
            var3.range = GameMath.limit(var1x, 1, 5);
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
