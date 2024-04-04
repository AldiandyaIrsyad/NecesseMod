package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketServerStatusRequest extends Packet {
   public final int respondPort;
   public final int state;

   public PacketServerStatusRequest(byte[] var1) {
      super(var1);
      this.respondPort = this.getShortUnsigned(0);
      this.state = this.getInt(2);
   }

   public PacketServerStatusRequest(int var1, int var2) {
      this.respondPort = var1;
      this.state = var2;
      this.putShortUnsigned(0, var1);
      this.putInt(2, var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var1.networkInfo instanceof DatagramNetworkInfo) {
         DatagramNetworkInfo var4 = (DatagramNetworkInfo)var1.networkInfo;
         int var5 = this.respondPort;
         if (var5 == 0) {
            var5 = var4.port;
         }

         var2.network.sendPacket(new NetworkPacket(new PacketServerStatus(var2, this.state), new DatagramNetworkInfo(var4.socket, var4.address, var5)));
      } else {
         GameLog.warn.println("Received status request packet from unknown connection: " + (var1.networkInfo == null ? "LOCAL" : var1.networkInfo.getDisplayName()));
      }

   }
}
