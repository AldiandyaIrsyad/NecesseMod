package necesse.engine.network.packet;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class PacketRegionsData extends Packet {
   public final int levelIdentifierHashCode;
   public HashMap<Point, Packet> regionData;

   public PacketRegionsData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.regionData = readRegionsDataPacket(var2);
   }

   public PacketRegionsData(ServerClient var1, Level var2, Collection<Region> var3) {
      this.levelIdentifierHashCode = var2.getIdentifierHashCode();
      this.regionData = toRegionData(var1, var3);
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.levelIdentifierHashCode);
      writeRegionsDataPacket(var4, this.regionData);
   }

   public static HashMap<Point, Packet> toRegionData(ServerClient var0, Collection<Region> var1) {
      HashMap var2 = new HashMap(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Region var4 = (Region)var3.next();
         var2.put(new Point(var4.regionX, var4.regionY), var4.getRegionDataPacket(var0));
      }

      return var2;
   }

   public static void writeRegionsDataPacket(PacketWriter var0, HashMap<Point, Packet> var1) {
      var0.putNextShortUnsigned(var1.size());
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Point var4 = (Point)var3.getKey();
         var0.putNextShortUnsigned(var4.x);
         var0.putNextShortUnsigned(var4.y);
         var0.putNextContentPacket((Packet)var3.getValue());
      }

   }

   public static HashMap<Point, Packet> readRegionsDataPacket(PacketReader var0) {
      int var1 = var0.getNextShortUnsigned();
      HashMap var2 = new HashMap(var1);

      for(int var3 = 0; var3 < var1; ++var3) {
         int var4 = var0.getNextShortUnsigned();
         int var5 = var0.getNextShortUnsigned();
         Packet var6 = var0.getNextContentPacket();
         var2.put(new Point(var4, var5), var6);
      }

      return var2;
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
         var2.levelManager.loading().applyRegionData(this.regionData);
         var2.loading.levelPreloadPhase.updateLoadingMessage();
      }
   }
}
