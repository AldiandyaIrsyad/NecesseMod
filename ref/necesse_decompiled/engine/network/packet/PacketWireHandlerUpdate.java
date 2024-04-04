package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEWireHandler;

public class PacketWireHandlerUpdate extends Packet {
   public final int tileX;
   public final int tileY;
   public final boolean[] outputs;

   public PacketWireHandlerUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      this.outputs = new boolean[4];

      for(int var3 = 0; var3 < this.outputs.length; ++var3) {
         this.outputs[var3] = var2.getNextBoolean();
      }

   }

   public PacketWireHandlerUpdate(OEWireHandler var1) {
      ObjectEntity var2 = var1.getHandlerParent();
      this.tileX = var2.getX();
      this.tileY = var2.getY();
      this.outputs = new boolean[4];

      for(int var3 = 0; var3 < this.outputs.length; ++var3) {
         this.outputs[var3] = var1.getWireOutput(var3);
      }

      PacketWriter var8 = new PacketWriter(this);
      var8.putNextShortUnsigned(this.tileX);
      var8.putNextShortUnsigned(this.tileY);
      boolean[] var4 = this.outputs;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         boolean var7 = var4[var6];
         var8.putNextBoolean(var7);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         ObjectEntity var3 = var2.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
         if (var3 instanceof OEWireHandler) {
            ((OEWireHandler)var3).applyWireUpdate(this.outputs);
         }

      }
   }
}
