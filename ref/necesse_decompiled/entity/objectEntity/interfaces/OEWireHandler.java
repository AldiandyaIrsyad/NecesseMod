package necesse.entity.objectEntity.interfaces;

import necesse.engine.network.packet.PacketWireHandlerUpdate;
import necesse.entity.objectEntity.ObjectEntity;

public interface OEWireHandler {
   ObjectEntity getHandlerParent();

   boolean[] getWireOutputs();

   boolean getWireOutput(int var1);

   default void wireUpdateClients() {
      ObjectEntity var1 = this.getHandlerParent();
      if (var1.isServer()) {
         var1.getLevel().getServer().network.sendToClientsWithTile(new PacketWireHandlerUpdate(this), var1.getLevel(), var1.getTileX(), var1.getTileY());
      }

   }

   default void applyWireUpdate(boolean[] var1) {
      ObjectEntity var2 = this.getHandlerParent();

      for(int var3 = 0; var3 < 4; ++var3) {
         this.getWireOutputs()[var3] = var1[var3];
         var2.getLevel().wireManager.updateWire(var2.getX(), var2.getY(), var3, var1[var3]);
      }

   }

   default void updateWireManager() {
      ObjectEntity var1 = this.getHandlerParent();

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.getLevel().wireManager.updateWire(var1.getX(), var1.getY(), var2, this.getWireOutputs()[var2]);
      }

   }
}
