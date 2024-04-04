package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketPermissionUpdate extends Packet {
   public final PermissionLevel permissionLevel;

   public PacketPermissionUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.permissionLevel = PermissionLevel.getLevel(var2.getNextByteUnsigned());
   }

   public PacketPermissionUpdate(PermissionLevel var1) {
      this.permissionLevel = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(var1.getLevel());
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.permissionUpdate(this);
   }
}
