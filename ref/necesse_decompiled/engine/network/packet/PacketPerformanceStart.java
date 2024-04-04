package necesse.engine.network.packet;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import necesse.engine.GameSystemInfo;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.PerformanceTimerUtils;
import necesse.engine.tickManager.PerformanceTotal;

public class PacketPerformanceStart extends Packet {
   public final int uniqueID;
   public final int seconds;
   public final boolean waitForServer;

   public PacketPerformanceStart(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.uniqueID = var2.getNextInt();
      this.seconds = var2.getNextInt();
      this.waitForServer = var2.getNextBoolean();
   }

   public PacketPerformanceStart(int var1, int var2, boolean var3) {
      this.uniqueID = var1;
      this.seconds = var2;
      this.waitForServer = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(var1);
      var4.putNextInt(var2);
      var4.putNextBoolean(var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      String var3 = (new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'")).format(new Date());
      var2.performanceDumpCache.submitIncomingDump(this.uniqueID, System.currentTimeMillis() + (long)(this.seconds + 30) * 1000L, this.seconds, "performance " + var3);
      var2.tickManager().runPerformanceDump(this.seconds, (var3x) -> {
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         PrintStream var5 = new PrintStream(var4);
         var5.println("Client performance recording ran for " + this.seconds + " seconds starting at " + var3);
         PerformanceTotal var6 = PerformanceTimerUtils.combineTimers(var3x);
         if (var6 != null) {
            var5.println("A total of " + var6.getTotalFrames() + " frames were recorded.");
            var5.println();
            var6.print(var5);
            var5.println();
            var5.println();
            GameSystemInfo.printSystemInfo(var5);
         }

         var2.performanceDumpCache.submitClientDump(this.uniqueID, var4.toString(), this.waitForServer);
      });
      if (this.waitForServer) {
         var2.chat.addMessage("Recording server and client performance for the next " + this.seconds + " seconds...");
      } else {
         var2.chat.addMessage("Recording client performance for the next " + this.seconds + " seconds");
      }

   }
}
