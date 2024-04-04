package necesse.engine.network.packet;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketUnloadRegions extends Packet {
   public final int levelIdentifierHashCode;
   public final HashSet<Point> regionPositions;

   public PacketUnloadRegions(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      int var3 = var2.getNextShortUnsigned();
      this.regionPositions = new HashSet();

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2.getNextShortUnsigned();
         int var6 = var2.getNextShortUnsigned();
         this.regionPositions.add(new Point(var5, var6));
      }

   }

   public PacketUnloadRegions(Level var1, HashSet<Point> var2) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.regionPositions = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.levelIdentifierHashCode);
      var3.putNextShortUnsigned(var2.size());
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Point var5 = (Point)var4.next();
         var3.putNextShortUnsigned(var5.x);
         var3.putNextShortUnsigned(var5.y);
      }

   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var2.world.getLevel(var3);
      if (var4 != null && var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
         Iterator var5 = this.regionPositions.iterator();

         while(var5.hasNext()) {
            Point var6 = (Point)var5.next();
            var3.removeLoadedRegion(var4, var6.x, var6.y);
         }
      }

   }
}
