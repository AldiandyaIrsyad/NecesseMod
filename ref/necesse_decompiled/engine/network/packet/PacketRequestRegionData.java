package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.RegionPosition;

public class PacketRequestRegionData extends Packet {
   public final int levelIdentifierHashCode;
   public final int regionX;
   public final int regionY;

   public PacketRequestRegionData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.regionX = var2.getNextShortUnsigned();
      this.regionY = var2.getNextShortUnsigned();
   }

   public PacketRequestRegionData(Level var1, int var2, int var3) {
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
         RegionPosition var5 = new RegionPosition(var4, this.regionX, this.regionY);
         Region var6 = var5.getRegion();
         if (var6 != null) {
            var2.network.sendPacket(new PacketRegionData(var6, var3), (ServerClient)var3);
            var3.addLoadedRegion(var5, true);
         } else {
            if (!var3.checkHasRequestedSelf()) {
               return;
            }

            GameLog.warn.println("Client requested invalid region data resulting in a kick.");
            var2.disconnectClient(var3, PacketDisconnect.Code.INTERNAL_ERROR);
         }
      }

   }
}
