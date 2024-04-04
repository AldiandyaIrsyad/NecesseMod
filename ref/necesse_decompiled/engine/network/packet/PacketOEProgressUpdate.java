package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.IProgressObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class PacketOEProgressUpdate extends Packet {
   public final int x;
   public final int y;
   public final Packet content;

   public PacketOEProgressUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.x = var2.getNextShortUnsigned();
      this.y = var2.getNextShortUnsigned();
      this.content = var2.getNextContentPacket();
   }

   public PacketOEProgressUpdate(IProgressObjectEntity var1) {
      ObjectEntity var2 = (ObjectEntity)var1;
      this.x = var2.getX();
      this.y = var2.getY();
      this.content = new Packet();
      var1.setupProgressPacket(new PacketWriter(this.content));
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextShortUnsigned(this.x);
      var3.putNextShortUnsigned(this.y);
      var3.putNextContentPacket(this.content);
   }

   public ObjectEntity getObjectEntity(Level var1) {
      return var1.entityManager.getObjectEntity(this.x, this.y);
   }

   public IProgressObjectEntity getProcessingObjectEntity(Level var1) {
      ObjectEntity var2 = this.getObjectEntity(var1);
      return var2 instanceof IProgressObjectEntity ? (IProgressObjectEntity)var2 : null;
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         IProgressObjectEntity var3 = this.getProcessingObjectEntity(var2.getLevel());
         if (var3 != null) {
            var3.applyProgressPacket(new PacketReader(this.content));
         }

      }
   }
}
