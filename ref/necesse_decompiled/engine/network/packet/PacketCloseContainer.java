package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketCloseContainer extends Packet {
   public PacketCloseContainer(byte[] var1) {
      super(var1);
   }

   public PacketCloseContainer() {
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.closeContainer(false);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.closeContainer(false);
   }
}
