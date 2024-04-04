package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;

public class PacketRequestPassword extends Packet {
   public final long worldUniqueID;

   public PacketRequestPassword(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.worldUniqueID = var2.getNextLong();
   }

   public PacketRequestPassword(Server var1) {
      this.worldUniqueID = var1.world.getUniqueID();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextLong(this.worldUniqueID);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.loading.connectingPhase.submitRequestPasswordPacket(this);
   }
}
