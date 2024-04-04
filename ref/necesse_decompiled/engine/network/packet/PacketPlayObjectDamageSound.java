package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ObjectRegistry;

public class PacketPlayObjectDamageSound extends Packet {
   public final int tileX;
   public final int tileY;
   public final int objectID;

   public PacketPlayObjectDamageSound(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      this.objectID = var2.getNextShortUnsigned();
   }

   public PacketPlayObjectDamageSound(int var1, int var2, int var3) {
      this.tileX = var1;
      this.tileY = var2;
      this.objectID = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextShortUnsigned(var1);
      var4.putNextShortUnsigned(var2);
      var4.putNextShortUnsigned(var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         ObjectRegistry.getObject(this.objectID).playDamageSound(var2.getLevel(), this.tileX, this.tileY, true);
      }
   }
}
