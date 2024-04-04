package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketMapData extends Packet {
   public final boolean[] mapData;

   public PacketMapData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mapData = new boolean[var2.getNextInt()];

      for(int var3 = 0; var3 < this.mapData.length; ++var3) {
         this.mapData[var3] = var2.getNextBoolean();
      }

   }

   public PacketMapData(boolean[] var1) {
      this.mapData = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1.length);

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2.putNextBoolean(var1[var3]);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         var2.levelManager.map().setDiscoveredData(this.mapData);
      }
   }
}
