package necesse.engine.network.server.network;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.InvalidNetworkInfo;
import necesse.engine.network.server.Server;

public class ServerSingleplayerNetwork extends ServerNetwork {
   private boolean isOpen;

   public ServerSingleplayerNetwork(Server var1) {
      super(var1);
   }

   public void open() {
      this.isOpen = true;
   }

   public boolean isOpen() {
      return this.isOpen;
   }

   public String getAddress() {
      return null;
   }

   public void sendPacket(NetworkPacket var1) {
      this.server.packetManager.submitOutPacket(var1);
      Client var2 = this.server.getLocalClient();
      if (var2 != null && var1.networkInfo == null) {
         var2.submitSinglePlayerPacket(var2.packetManager, var1);
      } else if (!(var1.networkInfo instanceof InvalidNetworkInfo)) {
         System.err.println("Tried to send singleplayer packet to invalid network: " + var1.networkInfo.getDisplayName());
      }

   }

   public void close() {
      this.isOpen = false;
   }

   public String getDebugString() {
      return "LOCAL";
   }
}
