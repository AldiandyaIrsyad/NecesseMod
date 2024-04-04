package necesse.engine.network.client.network;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworkingMessage;
import com.codedisaster.steamworks.SteamNetworkingMessages;
import com.codedisaster.steamworks.SteamNetworkingMessagesCallback;
import java.io.IOException;
import java.util.function.BiConsumer;
import necesse.engine.GameLog;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.SteamNetworkMessagesInfo;

public class ClientSteamNetworkMessages extends ClientNetwork {
   private final Client client;
   public final SteamID remoteID;
   private SteamNetworkingMessages networking;
   private Thread listenThread;

   public ClientSteamNetworkMessages(Client var1, SteamID var2) {
      this.client = var1;
      this.remoteID = var2;
   }

   public boolean openConnection() {
      this.networking = new SteamNetworkingMessages(new SteamNetworkingMessagesCallback() {
         public void onSteamNetworkingMessagesSessionRequest(SteamID var1) {
            GameLog.debug.println("onSteamNetworkingMessagesSessionRequest: " + SteamID.getNativeHandle(var1));
            if (ClientSteamNetworkMessages.this.remoteID.equals(var1) || ClientSteamNetworkMessages.this.remoteID.getAccountID() == var1.getAccountID()) {
               ClientSteamNetworkMessages.this.networking.acceptSessionWithUser(var1);
               GameLog.debug.println("Accepted client Steam P2P request");
            }

         }

         public void onSteamNetworkingMessagesSessionFailed(SteamID var1) {
            GameLog.debug.println("onSteamNetworkingMessagesSessionFailed: " + SteamID.getNativeHandle(var1));
            ClientSteamNetworkMessages.this.networking.closeSessionWithUser(var1);
         }
      });
      this.networking.closeSessionWithUser(this.remoteID);
      this.listenThread = new Thread("Client Socket") {
         public void run() {
            while(ClientSteamNetworkMessages.this.isOpen()) {
               SteamNetworkingMessage[] var1 = new SteamNetworkingMessage[32];
               int var2 = ClientSteamNetworkMessages.this.networking.receiveMessagesOnChannel(0, var1, var1.length);

               for(int var3 = 0; var3 < var2; ++var3) {
                  SteamNetworkingMessage var4 = var1[var3];

                  try {
                     if (var4.remoteSteamID.equals(ClientSteamNetworkMessages.this.remoteID)) {
                        try {
                           byte[] var5 = new byte[var4.size];
                           var4.data.position(0);
                           var4.data.get(var5);
                           NetworkPacket var6 = new NetworkPacket(ClientSteamNetworkMessages.this.networking, ClientSteamNetworkMessages.this.remoteID, var5);
                           ClientSteamNetworkMessages.this.client.packetManager.submitInPacket(var6);
                        } catch (UnknownPacketException var12) {
                           GameLog.warn.println("Client received unknown Steam P2P packet from ID: " + SteamID.getNativeHandle(var4.remoteSteamID) + " (" + var12.getMessage() + ")");
                        }
                     } else {
                        GameLog.warn.println("Client received Steam P2P packet from unknown ID: " + SteamID.getNativeHandle(var4.remoteSteamID) + ", expected ID: " + SteamID.getNativeHandle(ClientSteamNetworkMessages.this.remoteID));
                     }
                  } finally {
                     var4.free();
                  }
               }

               try {
                  Thread.sleep(2L);
               } catch (InterruptedException var11) {
               }
            }

         }
      };
      this.listenThread.start();
      return true;
   }

   public String getOpenError() {
      return null;
   }

   public boolean isOpen() {
      return this.networking != null;
   }

   public void sendPacket(Packet var1) {
      if (this.networking == null) {
         GameLog.warn.println("Tried to send packet on disposed SteamNetwork");
      } else {
         NetworkPacket var2 = new NetworkPacket(var1, new SteamNetworkMessagesInfo(this.networking, this.remoteID));
         this.client.packetManager.submitOutPacket(var2);

         try {
            var2.sendPacket();
         } catch (IOException var4) {
            var4.printStackTrace();
         }

      }
   }

   public void close() {
      if (this.listenThread != null) {
         this.listenThread.interrupt();
      }

      if (this.networking != null) {
         this.networking.dispose();
      }

      this.networking = null;
   }

   public String getDebugString() {
      return "STEAM:" + SteamID.getNativeHandle(this.remoteID);
   }

   public LocalMessage getPlayingMessage() {
      return new LocalMessage("richpresence", "playingwithfriends");
   }

   public String getRichPresenceGroup() {
      return this.remoteID.toString();
   }

   public void writeLobbyConnectInfo(BiConsumer<String, String> var1) {
      var1.accept("serverHostSteamID", String.valueOf(SteamID.getNativeHandle(this.remoteID)));
   }
}
