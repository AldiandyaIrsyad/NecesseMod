package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.level.maps.Level;

public class PacketTrapTriggered extends Packet {
   public final int x;
   public final int y;

   public PacketTrapTriggered(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.x = var2.getNextShortUnsigned();
      this.y = var2.getNextShortUnsigned();
   }

   public PacketTrapTriggered(int var1, int var2) {
      this.x = var1;
      this.y = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextShortUnsigned(var1);
      var3.putNextShortUnsigned(var2);
   }

   public ObjectEntity getObjectEntity(Level var1) {
      return var1.entityManager.getObjectEntity(this.x, this.y);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         ObjectEntity var3 = var2.getLevel().entityManager.getObjectEntity(this.x, this.y);
         if (var3 instanceof TrapObjectEntity) {
            ((TrapObjectEntity)var3).onClientTrigger();
         }

      }
   }
}
