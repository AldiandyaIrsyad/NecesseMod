package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.level.maps.Level;

public class PacketOEUseUpdateFullRequest extends Packet {
   public final int x;
   public final int y;

   public PacketOEUseUpdateFullRequest(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.x = var2.getNextShortUnsigned();
      this.y = var2.getNextShortUnsigned();
   }

   public PacketOEUseUpdateFullRequest(OEUsers var1) {
      ObjectEntity var2 = (ObjectEntity)var1;
      this.x = var2.getX();
      this.y = var2.getY();
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextShortUnsigned(this.x);
      var3.putNextShortUnsigned(this.y);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var3.getLevel();
      ObjectEntity var5 = var4.entityManager.getObjectEntity(this.x, this.y);
      if (var5 instanceof OEUsers) {
         var3.sendPacket(new PacketOEUseUpdateFull((OEUsers)var5));
      } else {
         var3.sendPacket(new PacketChangeObject(var4, this.x, this.y, var4.getObjectID(this.x, this.y), var4.getObjectRotation(this.x, this.y)));
      }

   }
}
