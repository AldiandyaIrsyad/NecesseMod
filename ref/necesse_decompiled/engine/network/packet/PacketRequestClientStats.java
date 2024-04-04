package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;

public class PacketRequestClientStats extends Packet {
   public PacketRequestClientStats(byte[] var1) {
      super(var1);
   }

   public PacketRequestClientStats() {
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.network.sendPacket(new PacketClientStats(GlobalData.stats(), GlobalData.achievements()));
   }
}
