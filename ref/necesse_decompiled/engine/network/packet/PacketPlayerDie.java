package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;

public class PacketPlayerDie extends Packet {
   public final int slot;
   public final int respawnTime;

   public PacketPlayerDie(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.respawnTime = var2.getNextInt();
   }

   public PacketPlayerDie(int var1, int var2) {
      this.slot = var1;
      this.respawnTime = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextInt(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 != null) {
         var3.die(this.respawnTime);
      }

   }
}
