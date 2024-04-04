package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerSync extends Packet {
   public final int slot;
   private final PacketReader reader;

   public PacketPlayerSync(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.reader = var2;
   }

   public PacketPlayerSync(ServerClient var1, ServerClient var2) {
      this.slot = var1.slot;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(this.slot);
      this.reader = new PacketReader(var3);
      var1.setupSyncUpdate(var3, var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 != null) {
         var3.applySyncPacket(this.reader);
      } else {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      }

   }
}
