package necesse.engine.network.server.network;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworkingCallback;
import com.codedisaster.steamworks.SteamFriends.FriendFlags;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.networkInfo.SteamNetworkDeprecatedInfo;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.steam.SteamData;
import necesse.engine.steam.SteamFriend;

public class ServerSteamDeprecatedListenThread extends Thread {
   private static final int requestDeclineTimeout = 5000;
   private final SteamNetworking steamNetwork;
   private final ServerNetwork serverNetwork;
   private final LinkedList<ConnectRequest> connectRequests = new LinkedList();
   private final ServerSteamDeniedConnections deniedConnections = new ServerSteamDeniedConnections();
   private final ServerSteamInvitedUsers invitedUsers = new ServerSteamInvitedUsers();

   public ServerSteamDeprecatedListenThread(String var1, final ServerNetwork var2) {
      super(var1);
      this.serverNetwork = var2;
      this.steamNetwork = new SteamNetworking(new SteamNetworkingCallback() {
         public void onP2PSessionConnectFail(SteamID var1, SteamNetworking.P2PSessionError var2x) {
            GameLog.debug.println("onP2PSessionConnectFail: " + SteamID.getNativeHandle(var1) + ", " + var2x);
            ServerSteamDeprecatedListenThread.this.steamNetwork.closeP2PSessionWithUser(var1);
         }

         public void onP2PSessionRequest(SteamID var1) {
            PacketDisconnect var2x;
            switch (var2.server.settings.steamLobbyType) {
               case Open:
                  var2x = null;
                  break;
               case InviteOnly:
                  if (ServerSteamDeprecatedListenThread.this.invitedUsers.isInvited(var1)) {
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

            if (ServerSteamDeprecatedListenThread.this.invitedUsers.isInvited(var1)) {
               var2x = null;
            }

            if (var2x == null) {
               ServerSteamDeprecatedListenThread.this.deniedConnections.removeDeniedUser(var1);
               ServerSteamDeprecatedListenThread.this.steamNetwork.acceptP2PSessionWithUser(var1);
               GameLog.debug.println("Accepted Steam client P2P session request from " + SteamID.getNativeHandle(var1));
            } else {
               var2.sendPacket(new NetworkPacket(var2x, new SteamNetworkDeprecatedInfo(ServerSteamDeprecatedListenThread.this.steamNetwork, var1)));
               ServerSteamDeprecatedListenThread.this.deniedConnections.addDeniedUser(var1);
               ServerSteamDeprecatedListenThread.this.connectRequests.add(new ConnectRequest(var1));
            }

         }
      });
      this.steamNetwork.allowP2PPacketRelay(true);
   }

   public void run() {
      while(this.serverNetwork.isOpen()) {
         while(!this.connectRequests.isEmpty()) {
            ConnectRequest var1 = (ConnectRequest)this.connectRequests.getFirst();
            if (this.invitedUsers.isInvited(var1.steamIDRemote)) {
               this.deniedConnections.removeDeniedUser(var1.steamIDRemote);
               this.steamNetwork.acceptP2PSessionWithUser(var1.steamIDRemote);
               this.connectRequests.removeFirst();
               GameLog.debug.println("Accepted new Steam client P2P session request from " + SteamID.getNativeHandle(var1.steamIDRemote));
            } else if (var1.requestTime + 5000L < System.currentTimeMillis()) {
               this.connectRequests.removeFirst();
               GameLog.debug.println("Timed out Steam client P2P session request from " + SteamID.getNativeHandle(var1.steamIDRemote));
            }
         }

         ServerSteamDeniedConnections var10000 = this.deniedConnections;
         SteamNetworking var10001 = this.steamNetwork;
         Objects.requireNonNull(var10001);
         var10000.runCleanup(var10001::closeP2PSessionWithUser);
         int[] var10 = new int[1];
         if (this.steamNetwork.isP2PPacketAvailable(0, var10)) {
            int var2 = var10[0];
            ByteBuffer var3 = ByteBuffer.allocateDirect(var2);

            try {
               SteamID var4 = new SteamID();
               if (this.steamNetwork.readP2PPacket(var4, var3, 0) > 0) {
                  if (!this.deniedConnections.isDenied(var4)) {
                     try {
                        byte[] var5 = new byte[var2];
                        var3.get(var5);
                        NetworkPacket var6 = new NetworkPacket(this.steamNetwork, var4, var5);
                        this.serverNetwork.server.packetManager.submitInPacket(var6);
                     } catch (UnknownPacketException var7) {
                        GameLog.warn.println("Server received unknown Steam P2P packet from ID " + SteamID.getNativeHandle(var4));
                     }
                  }
               } else {
                  GameLog.warn.println("Server received Steam P2P packet from unknown ID " + SteamID.getNativeHandle(var4));
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

   public void interrupt() {
      super.interrupt();
      this.steamNetwork.dispose();
   }

   public void addInvitedUser(SteamID var1) {
      this.invitedUsers.addInvitedUser(var1);
      this.steamNetwork.closeP2PSessionWithUser(var1);
      this.steamNetwork.acceptP2PSessionWithUser(var1);
   }

   private static class ConnectRequest {
      public SteamID steamIDRemote;
      public final long requestTime;

      public ConnectRequest(SteamID var1) {
         this.steamIDRemote = var1;
         this.requestTime = System.currentTimeMillis();
      }
   }
}
