package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketObjectEntityError extends Packet {
   public final int x;
   public final int y;

   public PacketObjectEntityError(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.x = var2.getNextShortUnsigned();
      this.y = var2.getNextShortUnsigned();
   }

   public PacketObjectEntityError(int var1, int var2) {
      this.x = var1;
      this.y = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextShortUnsigned(var1);
      var3.putNextShortUnsigned(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.loading.objectEntities.submitObjectEntityErrorPacket(this);
   }
}
