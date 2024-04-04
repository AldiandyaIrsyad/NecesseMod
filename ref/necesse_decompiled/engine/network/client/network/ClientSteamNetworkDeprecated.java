package necesse.engine.network.client.network;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworkingCallback;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import necesse.engine.GameLog;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.SteamNetworkDeprecatedInfo;

public class ClientSteamNetworkDeprecated extends ClientNetwork {
   private final Client client;
   public final SteamID remoteID;
   private SteamNetworking networking;
   private Thread listenThread;

   public ClientSteamNetworkDeprecated(Client var1, SteamID var2) {
      this.client = var1;
      this.remoteID = var2;
   }

   public boolean openConnection() {
      this.networking = new SteamNetworking(new SteamNetworkingCallback() {
         public void onP2PSessionConnectFail(SteamID var1, SteamNetworking.P2PSessionError var2) {
            GameLog.debug.println("onP2PSessionConnectFail: " + SteamID.getNativeHandle(var1) + ", " + var2);
            ClientSteamNetworkDeprecated.this.networking.closeP2PSessionWithUser(var1);
         }

         public void onP2PSessionRequest(SteamID var1) {
            GameLog.debug.println("onP2PSessionRequest: " + SteamID.getNativeHandle(var1));
            if (ClientSteamNetworkDeprecated.this.remoteID.equals(var1)) {
               ClientSteamNetworkDeprecated.this.networking.acceptP2PSessionWithUser(var1);
               GameLog.debug.println("Accepted client Steam P2P request");
            }

         }
      });
      this.networking.closeP2PSessionWithUser(this.remoteID);
      this.listenThread = new Thread("Client Socket") {
         public void run() {
            while(ClientSteamNetworkDeprecated.this.isOpen()) {
               int[] var1 = new int[1];
               if (ClientSteamNetworkDeprecated.this.networking.isP2PPacketAvailable(0, var1)) {
                  int var2 = var1[0];
                  ByteBuffer var3 = ByteBuffer.allocateDirect(var2);

                  try {
                     SteamID var4 = new SteamID();
                     if (ClientSteamNetworkDeprecated.this.networking.readP2PPacket(var4, var3, 0) > 0) {
                        if (var4.equals(ClientSteamNetworkDeprecated.this.remoteID)) {
                           try {
                              byte[] var5 = new byte[var2];
                              var3.get(var5);
                              NetworkPacket var6 = new NetworkPacket(ClientSteamNetworkDeprecated.this.networking, ClientSteamNetworkDeprecated.this.remoteID, var5);
                              ClientSteamNetworkDeprecated.this.client.packetManager.submitInPacket(var6);
                           } catch (UnknownPacketException var7) {
                              GameLog.warn.println("Client received unknown Steam P2P packet from ID " + SteamID.getNativeHandle(var4));
                           }
                        } else {
                           GameLog.warn.println("Client received Steam P2P packet from unknown ID " + SteamID.getNativeHandle(var4));
                        }
                     } else {
                        System.err.println("Could not receive Steam packet from " + SteamID.getNativeHandle(var4));
                     }
                  } catch (SteamException var8) {
                     var8.printStackTrace();
                  }
               } else {
                  try {
                     Thread.sleep(2L);
                  } catch (InterruptedException var9) {
                  }
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
         NetworkPacket var2 = new NetworkPacket(var1, new SteamNetworkDeprecatedInfo(this.networking, this.remoteID));
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
