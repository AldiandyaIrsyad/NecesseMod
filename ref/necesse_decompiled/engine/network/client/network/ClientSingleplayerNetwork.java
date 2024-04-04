package necesse.engine.network.client.network;

import com.codedisaster.steamworks.SteamID;
import java.util.function.BiConsumer;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.server.Server;
import necesse.engine.steam.SteamData;

public class ClientSingleplayerNetwork extends ClientNetwork {
   private final Client client;
   private boolean isOpen;

   public ClientSingleplayerNetwork(Client var1) {
      this.client = var1;
   }

   public boolean openConnection() {
      this.isOpen = true;
      return true;
   }

   public String getOpenError() {
      return null;
   }

   public boolean isOpen() {
      return this.isOpen;
   }

   public void sendPacket(Packet var1) {
      NetworkPacket var2 = new NetworkPacket(var1, (NetworkInfo)null);
      this.client.packetManager.submitOutPacket(var2);
      this.client.submitSinglePlayerPacket(this.client.getLocalServer().packetManager, var2);
   }

   public void close() {
      this.isOpen = false;
   }

   public String getDebugString() {
      return "LOCAL";
   }

   public LocalMessage getPlayingMessage() {
      if (!this.client.isSingleplayer()) {
         Server var1 = this.client.getLocalServer();
         if (var1 != null) {
            switch (var1.settings.steamLobbyType) {
               case Open:
                  return new LocalMessage("richpresence", "hostingopen");
               case VisibleToFriends:
                  return new LocalMessage("richpresence", "hostingfriends");
               default:
                  return new LocalMessage("richpresence", "hostinginvite");
            }
         }
      }

      return new LocalMessage("richpresence", "playingsingleplayer");
   }

   public String getRichPresenceGroup() {
      Server var1 = this.client.getLocalServer();
      if (!this.client.isSingleplayer() && var1 != null) {
         SteamID var2 = SteamData.getSteamID();
         return var2 == null ? null : var2.toString();
      } else {
         return super.getRichPresenceGroup();
      }
   }

   public void writeLobbyConnectInfo(BiConsumer<String, String> var1) {
      if (!this.client.isSingleplayer() && this.client.getSteamLobbyType() != null) {
         SteamID var2 = SteamData.getSteamID();
         if (var2 != null) {
            var1.accept("serverHostSteamID", String.valueOf(SteamID.getNativeHandle(var2)));
         }
      }

   }
}
