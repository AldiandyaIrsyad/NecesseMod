package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketRequestTileChange extends Packet {
   public final int tileX;
   public final int tileY;

   public PacketRequestTileChange(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
   }

   public PacketRequestTileChange(int var1, int var2) {
      this.tileX = var1;
      this.tileY = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextShortUnsigned(var1);
      var3.putNextShortUnsigned(var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var3.getLevel();
      if (var4 != null) {
         var3.sendPacket(new PacketChangeTile(var4, this.tileX, this.tileY, var4.getTileID(this.tileX, this.tileY)));
      }
   }
}
