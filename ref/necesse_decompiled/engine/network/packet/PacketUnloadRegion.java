package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketUnloadRegion extends Packet {
   public final int levelIdentifierHashCode;
   public final int regionX;
   public final int regionY;

   public PacketUnloadRegion(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.regionX = var2.getNextShortUnsigned();
      this.regionY = var2.getNextShortUnsigned();
   }

   public PacketUnloadRegion(Level var1, int var2, int var3) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.regionX = var2;
      this.regionY = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.levelIdentifierHashCode);
      var4.putNextShortUnsigned(var2);
      var4.putNextShortUnsigned(var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var2.world.getLevel(var3);
      if (var4 != null && var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
         var3.removeLoadedRegion(var4, this.regionX, this.regionY);
      }

   }
}
