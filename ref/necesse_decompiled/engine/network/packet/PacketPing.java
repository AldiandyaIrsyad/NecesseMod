package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPing extends Packet {
   public int responseKey;

   public PacketPing(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.responseKey = var2.getNextInt();
   }

   public PacketPing(int var1) {
      this.responseKey = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      var3.submitPingPacket(this);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.submitPingPacket(this);
   }
}
