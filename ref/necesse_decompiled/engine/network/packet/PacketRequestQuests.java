package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketRequestQuests extends Packet {
   public PacketRequestQuests(byte[] var1) {
      super(var1);
   }

   public PacketRequestQuests() {
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var2.network.sendPacket(new PacketQuests(var3), (ServerClient)var3);
   }
}
