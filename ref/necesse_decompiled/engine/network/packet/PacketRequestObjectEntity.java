package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class PacketRequestObjectEntity extends Packet {
   public final int levelIdentifierHashCode;
   public final int x;
   public final int y;

   public PacketRequestObjectEntity(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.x = var2.getNextShortUnsigned();
      this.y = var2.getNextShortUnsigned();
   }

   public PacketRequestObjectEntity(Level var1, int var2, int var3) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.x = var2;
      this.y = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.levelIdentifierHashCode);
      var4.putNextShortUnsigned(var2);
      var4.putNextShortUnsigned(var3);
   }

   public Packet getRequestedPacket(Level var1) {
      ObjectEntity var2 = var1.entityManager.getObjectEntity(this.x, this.y);
      return (Packet)(var2 != null ? new PacketObjectEntity(var2) : new PacketObjectEntityError(this.x, this.y));
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var2.world.getLevel(var3);
      if (var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
         var2.network.sendPacket(this.getRequestedPacket(var4), var3);
      } else {
         GameLog.warn.println(var3.getName() + " requested object entity on wrong level");
      }

   }
}
