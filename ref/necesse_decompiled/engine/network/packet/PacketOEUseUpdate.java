package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;

public class PacketOEUseUpdate extends Packet {
   public final int x;
   public final int y;
   public final int mobUniqueID;
   public final boolean isUsing;
   public final int totalUsers;

   public PacketOEUseUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.x = var2.getNextShortUnsigned();
      this.y = var2.getNextShortUnsigned();
      this.mobUniqueID = var2.getNextInt();
      this.isUsing = var2.getNextBoolean();
      this.totalUsers = var2.getNextShortUnsigned();
   }

   public PacketOEUseUpdate(OEUsers var1, int var2, boolean var3) {
      ObjectEntity var4 = (ObjectEntity)var1;
      this.x = var4.getX();
      this.y = var4.getY();
      this.mobUniqueID = var2;
      this.isUsing = var3;
      this.totalUsers = var1.getTotalUsers();
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextShortUnsigned(this.x);
      var5.putNextShortUnsigned(this.y);
      var5.putNextInt(var2);
      var5.putNextBoolean(var3);
      var5.putNextShortUnsigned(this.totalUsers);
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
