package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketAdventurePartyRequestUpdate extends Packet {
   public PacketAdventurePartyRequestUpdate(byte[] var1) {
      super(var1);
   }

   public PacketAdventurePartyRequestUpdate() {
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.sendPacket(new PacketAdventurePartyUpdate(var3));
   }
}
