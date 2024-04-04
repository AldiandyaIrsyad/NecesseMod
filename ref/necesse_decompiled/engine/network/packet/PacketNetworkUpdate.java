package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;

public class PacketNetworkUpdate extends Packet {
   public final long totalInPackets;
   public final long totalOutPackets;

   public PacketNetworkUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.totalInPackets = var2.getNextLong();
      this.totalOutPackets = var2.getNextLong();
   }

   public PacketNetworkUpdate(ServerClient var1) {
      this.totalInPackets = var1.getPacketsInTotal();
      this.totalOutPackets = var1.getPacketsOutTotal();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextLong(this.totalInPackets);
      var2.putNextLong(this.totalOutPackets);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.packetManager.applyNetworkUpdate(this);
   }
}
