package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;

public class PacketNeedRequestSelf extends Packet {
   public PacketNeedRequestSelf(byte[] var1) {
      super(var1);
   }

   public PacketNeedRequestSelf() {
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getSlot() != -1) {
         var2.network.sendPacket(new PacketRequestPlayerData(var2.getSlot()));
      }
   }
}
