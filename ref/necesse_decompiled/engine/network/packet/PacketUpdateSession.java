package necesse.engine.network.packet;

import java.util.Objects;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketUpdateSession extends Packet {
   public final long sessionID;

   public PacketUpdateSession(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.sessionID = var2.getNextLong();
   }

   public PacketUpdateSession(long var1) {
      this.sessionID = var1;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextLong(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      ServerClient var4 = (ServerClient)var2.streamClients().filter(Objects::nonNull).filter((var1x) -> {
         return var1x.getSessionID() == this.sessionID;
      }).findFirst().orElse((Object)null);
      if (var4 != null && !Objects.equals(var4.networkInfo, var1.networkInfo)) {
         System.out.println(var4.getName() + " submitted valid session ID. Updating connection to " + (var1.networkInfo == null ? "LOCAL" : var1.networkInfo.getDisplayName()));
         var4.networkInfo = var1.networkInfo;
      }

   }
}
