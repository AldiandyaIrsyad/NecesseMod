package necesse.engine.network.server.network;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworkingMessage;
import com.codedisaster.steamworks.SteamNetworkingMessages;
import com.codedisaster.steamworks.SteamNetworkingMessagesCallback;
import com.codedisaster.steamworks.SteamFriends.FriendFlags;
import java.util.LinkedList;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.networkInfo.SteamNetworkMessagesInfo;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.steam.SteamData;
import necesse.engine.steam.SteamFriend;

public class ServerSteamMessagesListenThread extends Thread {
   private static final int requestDeclineTimeout = 5000;
   private final SteamNetworkingMessages steamNetwork;
   private final ServerNetwork serverNetwork;
   private final LinkedList<ConnectRequest> connectRequests = new LinkedList();
   private final ServerSteamDeniedConnections deniedConnections = new ServerSteamDeniedConnections();
   private final ServerSteamInvitedUsers invitedUsers = new ServerSteamInvitedUsers();

   public ServerSteamMessagesListenThread(String var1, final ServerNetwork var2) {
      super(var1);
      this.serverNetwork = var2;
      this.steamNetwork = new SteamNetworkingMessages(new SteamNetworkingMessagesCallback() {
         public void onSteamNetworkingMessagesSessionRequest(SteamID var1) {
            PacketDisconnect var2x;
            switch (var2.server.settings.steamLobbyType) {
               case Open:
                  var2x = null;
                  break;
               case InviteOnly:
                  if (ServerSteamMessagesListenThread.this.invitedUsers.isInvited(var1)) {
                     var2x = null;
                  } else {
                     var2x = new PacketDisconnect(-1, PacketDisconnect.Code.INVITE_ONLY);
                  }
                  break;
               case VisibleToFriends:
                  boolean var3 = false;
                  SteamFriend[] var4 = SteamData.getFriends(FriendFlags.Immediate);
                  int var5 = var4.length;

                  for(int var6 = 0; var6 < var5; ++var6) {
                     SteamFriend var7 = var4[var6];
                     if (var7.steamID.equals(var1) || var7.steamID.getAccountID() == var1.getAccountID()) {
                        var3 = true;
                        break;
                     }
                  }

                  if (var3) {
                     var2x = null;
                  } else {
                     var2x = new PacketDisconnect(-1, PacketDisconnect.Code.FRIENDS_ONLY);
                  }
                  break;
               default:
                  var2x = new PacketDisconnect(-1, new StaticMessage("Unknown Steam connection"));
            }

            if (ServerSteamMessagesListenThread.this.invitedUsers.isInvited(var1)) {
               var2x = null;
            }

            if (var2x == null) {
               ServerSteamMessagesListenThread.this.deniedConnections.removeDeniedUser(var1);
               ServerSteamMessagesListenThread.this.steamNetwork.acceptSessionWithUser(var1);
               GameLog.out.println("Accepted Steam client P2P session request from " + SteamID.getNativeHandle(var1));
            } else {
               var2.sendPacket(new NetworkPacket(var2x, new SteamNetworkMessagesInfo(ServerSteamMessagesListenThread.this.steamNetwork, var1)));
               ServerSteamMessagesListenThread.this.deniedConnections.addDeniedUser(var1);
               ServerSteamMessagesListenThread.this.connectRequests.add(new ConnectRequest(var1));
            }

         }

         public void onSteamNetworkingMessagesSessionFailed(SteamID var1) {
            GameLog.warn.println("onSteamNetworkingMessagesSessionFailed: " + SteamID.getNativeHandle(var1));
            ServerSteamMessagesListenThread.this.steamNetwork.closeSessionWithUser(var1);
         }
      });
   }

   public void run() {
      while(this.serverNetwork.isOpen()) {
         while(!this.connectRequests.isEmpty()) {
            ConnectRequest var1 = (ConnectRequest)this.connectRequests.getFirst();
            if (this.invitedUsers.isInvited(var1.steamIDRemote)) {
               this.deniedConnections.removeDeniedUser(var1.steamIDRemote);
               this.steamNetwork.acceptSessionWithUser(var1.steamIDRemote);
               this.connectRequests.removeFirst();
               GameLog.debug.println("Accepted new Steam client P2P session request from " + SteamID.getNativeHandle(var1.steamIDRemote));
            } else if (var1.time + 5000L < System.currentTimeMillis()) {
               this.connectRequests.removeFirst();
               GameLog.debug.println("Timed out Steam client P2P session request from " + SteamID.getNativeHandle(var1.steamIDRemote));
            }
         }

         ServerSteamDeniedConnections var10000 = this.deniedConnections;
         SteamNetworkingMessages var10001 = this.steamNetwork;
         Objects.requireNonNull(var10001);
         var10000.runCleanup(var10001::closeSessionWithUser);
         SteamNetworkingMessage[] var14 = new SteamNetworkingMessage[32];
         int var2 = this.steamNetwork.receiveMessagesOnChannel(0, var14, var14.length);

         for(int var3 = 0; var3 < var2; ++var3) {
            SteamNetworkingMessage var4 = var14[var3];

            try {
               if (!this.deniedConnections.isDenied(var4.remoteSteamID)) {
                  byte[] var5 = new byte[var4.size];
                  var4.data.position(0);
                  var4.data.get(var5);
                  NetworkPacket var6 = new NetworkPacket(this.steamNetwork, var4.remoteSteamID, var5);
                  this.serverNetwork.server.packetManager.submitInPacket(var6);
               }
            } catch (UnknownPacketException var12) {
               GameLog.warn.println("Server received unknown Steam P2P packet from ID: " + SteamID.getNativeHandle(var4.remoteSteamID) + " (" + var12.getMessage() + ")");
            } finally {
               var4.free();
            }
         }

         try {
            if (var2 <= 0) {
               Thread.sleep(2L);
            }
         } catch (InterruptedException var11) {
         }
      }

   }

   public void interrupt() {
      super.interrupt();
      this.steamNetwork.dispose();
   }

   public void addInvitedUser(SteamID var1) {
      this.invitedUsers.addInvitedUser(var1);
      this.steamNetwork.closeSessionWithUser(var1);
      this.steamNetwork.acceptSessionWithUser(var1);
   }

   private static class ConnectRequest {
      public SteamID steamIDRemote;
      public final long time;

      public ConnectRequest(SteamID var1) {
         this.steamIDRemote = var1;
         this.time = System.currentTimeMillis();
      }
   }
}
