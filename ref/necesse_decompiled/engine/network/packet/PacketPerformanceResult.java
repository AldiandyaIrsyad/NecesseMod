package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketPerformanceResult extends Packet {
   public final int uniqueID;
   public final String text;

   public PacketPerformanceResult(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.uniqueID = var2.getNextInt();
      this.text = var2.getNextStringLong();
   }

   public PacketPerformanceResult(int var1, String var2) {
      this.uniqueID = var1;
      this.text = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextStringLong(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.performanceDumpCache.submitServerDump(this.uniqueID, this.text);
   }
}
