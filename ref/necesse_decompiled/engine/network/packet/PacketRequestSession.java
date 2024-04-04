package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;

public class PacketRequestSession extends Packet {
   public PacketRequestSession(byte[] var1) {
      super(var1);
   }

   public PacketRequestSession() {
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.network.sendPacket(new PacketUpdateSession(var2.sessionID));
   }
}
