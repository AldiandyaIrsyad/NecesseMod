package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.regionSystem.Region;

public class PacketRegionData extends Packet {
   public final int levelIdentifierHashCode;
   public final int regionX;
   public final int regionY;
   public final Packet regionData;

   public PacketRegionData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.regionX = var2.getNextShortUnsigned();
      this.regionY = var2.getNextShortUnsigned();
      this.regionData = var2.getNextContentPacket();
   }

   public PacketRegionData(Region var1, ServerClient var2) {
      this.levelIdentifierHashCode = var1.level.getIdentifierHashCode();
      this.regionX = var1.regionX;
      this.regionY = var1.regionY;
      this.regionData = var1.getRegionDataPacket(var2);
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.levelIdentifierHashCode);
      var3.putNextShortUnsigned(this.regionX);
      var3.putNextShortUnsigned(this.regionY);
      var3.putNextContentPacket(this.regionData);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
         var2.levelManager.loading().applyRegionData(this);
         var2.loading.levelPreloadPhase.updateLoadingMessage();
      }
   }
}
