package necesse.engine.network.client;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamMatchmaking.ChatMemberStateChange;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.network.packet.PacketAddSteamInvite;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.steam.SteamData;

public class ClientSteamLobby {
   private final Client client;
   private final SteamMatchmaking matchmaking;
   private ServerSettings.SteamLobbyType lobbyType;
   private long waitingForLobbyCreate;
   private SteamID lobbySteamID;

   public ClientSteamLobby(final Client var1) {
      this.client = var1;
      this.matchmaking = new SteamMatchmaking(new SteamMatchmakingCallback() {
         public void onFavoritesListChanged(int var1x, int var2, int var3, int var4, int var5, boolean var6, int var7) {
         }

         public void onLobbyInvite(SteamID var1x, SteamID var2, long var3) {
            GameLog.debug.println("onLobbyInvite " + var1x + ", " + var2 + ", " + var3);
         }

         public void onLobbyEnter(SteamID var1x, int var2, boolean var3, SteamMatchmaking.ChatRoomEnterResponse var4) {
            GameLog.debug.println("onLobbyEnter " + var1x + ", " + var2 + ", " + var3 + ", " + var4);
         }

         public void onLobbyDataUpdate(SteamID var1x, SteamID var2, boolean var3) {
            GameLog.debug.println("onLobbyDataUpdate " + var1x + ", " + var2 + ", " + var3);
         }

         public void onLobbyChatUpdate(SteamID var1x, SteamID var2, SteamID var3, SteamMatchmaking.ChatMemberStateChange var4) {
            if (var4 == ChatMemberStateChange.Entered) {
               GameLog.debug.println("User " + var2 + " entered lobby " + var1x);
               if (ClientSteamLobby.this.lobbyType == ServerSettings.SteamLobbyType.InviteOnly || ClientSteamLobby.this.lobbyType == ServerSettings.SteamLobbyType.VisibleToFriends) {
                  var1.network.sendPacket(new PacketAddSteamInvite(var2));
               }
            } else {
               GameLog.debug.println("User " + var2 + " " + var4 + " lobby " + var1x);
            }

         }

         public void onLobbyChatMessage(SteamID var1x, SteamID var2, SteamMatchmaking.ChatEntryType var3, int var4) {
         }

         public void onLobbyGameCreated(SteamID var1x, SteamID var2, int var3, short var4) {
         }

         public void onLobbyMatchList(int var1x) {
         }

         public void onLobbyKicked(SteamID var1x, SteamID var2, boolean var3) {
            GameLog.debug.println("onLobbyKicked " + var1x + ", " + var2 + ", " + var3);
            if (ClientSteamLobby.this.lobbySteamID != null && ClientSteamLobby.this.lobbySteamID.equals(var1x)) {
               ClientSteamLobby.this.lobbySteamID = null;
            }

         }

         public void onLobbyCreated(SteamResult var1x, SteamID var2) {
            if (var1x == SteamResult.OK) {
               ClientSteamLobby.this.lobbySteamID = var2;
               GameLog.debug.println("Created Steam lobby " + var2);
               if (var1.getLocalServer() == null) {
                  var1.network.writeLobbyConnectInfo((var1xx, var2x) -> {
                     ClientSteamLobby.this.matchmaking.setLobbyData(ClientSteamLobby.this.lobbySteamID, var1xx, var2x);
                  });
               } else {
                  GameLog.debug.println("Local game is hosting server");
                  SteamID var3 = SteamData.getSteamID();
                  if (var3 != null) {
                     ClientSteamLobby.this.matchmaking.setLobbyData(ClientSteamLobby.this.lobbySteamID, "serverHostSteamID", String.valueOf(SteamID.getNativeHandle(var3)));
                  }
               }
            } else {
               GameLog.warn.println("Failed to create Steam lobby: " + var1x);
               ClientSteamLobby.this.waitingForLobbyCreate = System.currentTimeMillis() + 5000L;
            }

         }

         public void onFavoritesListAccountsUpdated(SteamResult var1x) {
         }
      });
   }

   public void createLobby(ServerSettings.SteamLobbyType var1) {
      if (!this.isWaitingForLobbyCreate()) {
         this.lobbyType = var1;
         if (this.matchmaking.createLobby(var1.steamLobbyType, 10).isValid()) {
            this.waitingForLobbyCreate = System.currentTimeMillis() + 60000L;
         } else {
            this.waitingForLobbyCreate = System.currentTimeMillis() + 5000L;
         }

      }
   }

   public boolean isLobbyCreated() {
      return this.lobbySteamID != null;
   }

   public boolean isWaitingForLobbyCreate() {
      return System.currentTimeMillis() < this.waitingForLobbyCreate;
   }

   protected void leaveLobby() {
      if (this.isLobbyCreated()) {
         this.matchmaking.leaveLobby(this.lobbySteamID);
         GameLog.debug.println("Left lobby " + this.lobbySteamID);
      }

      this.lobbySteamID = null;
   }

   public boolean inviteUser(SteamID var1) {
      return this.isLobbyCreated() ? this.matchmaking.inviteUserToLobby(this.lobbySteamID, var1) : false;
   }

   public static void requestPublicIP(Consumer<String> var0) {
      Thread var1 = new Thread(() -> {
         try {
            Scanner var1 = (new Scanner((new URL("https://api.ipify.org")).openStream(), "UTF-8")).useDelimiter("\\A");

            try {
               var0.accept(var1.next());
            } catch (Throwable var5) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (var1 != null) {
               var1.close();
            }
         } catch (IOException var6) {
            var0.accept((Object)null);
         }

      }, "Public IP Request");
      var1.start();
   }

   public void dispose() {
      this.leaveLobby();
      this.matchmaking.dispose();
   }
}
