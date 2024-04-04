package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;

public class PacketOEUseUpdateFull extends Packet {
   public final int x;
   public final int y;
   public final Packet content;

   public PacketOEUseUpdateFull(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.x = var2.getNextShortUnsigned();
      this.y = var2.getNextShortUnsigned();
      this.content = var2.getNextContentPacket();
   }

   public PacketOEUseUpdateFull(OEUsers var1) {
      ObjectEntity var2 = (ObjectEntity)var1;
      this.x = var2.getX();
      this.y = var2.getY();
      this.content = new Packet();
      var1.getUsersObject().writeUsersSpawnPacket(new PacketWriter(this.content));
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextShortUnsigned(this.x);
      var3.putNextShortUnsigned(this.y);
      var3.putNextContentPacket(this.content);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         ObjectEntity var3 = var2.getLevel().entityManager.getObjectEntity(this.x, this.y);
         if (var3 instanceof OEUsers) {
            ((OEUsers)var3).submitUpdatePacket(var3, this);
         }

      }
   }
}
