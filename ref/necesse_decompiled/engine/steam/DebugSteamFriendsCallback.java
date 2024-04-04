package necesse.engine.steam;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamResult;

public class DebugSteamFriendsCallback extends DebugSteamCallback implements SteamFriendsCallback {
   public DebugSteamFriendsCallback() {
   }

   public void onSetPersonaNameResponse(boolean var1, boolean var2, SteamResult var3) {
      this.print("onSetPersonaNameResponse", new Object[]{var1, var2, var3});
   }

   public void onPersonaStateChange(SteamID var1, SteamFriends.PersonaChange var2) {
      this.print("onPersonaStateChange", new Object[]{var1, var2});
   }

   public void onGameOverlayActivated(boolean var1, boolean var2, int var3) {
      this.print("onGameOverlayActivated", new Object[]{var1, var2, var3});
   }

   public void onGameLobbyJoinRequested(SteamID var1, SteamID var2) {
      this.print("onGameLobbyJoinRequested", new Object[]{var1, var2});
   }

   public void onAvatarImageLoaded(SteamID var1, int var2, int var3, int var4) {
      this.print("onAvatarImageLoaded", new Object[]{var1, var2, var3, var4});
   }

   public void onFriendRichPresenceUpdate(SteamID var1, int var2) {
      this.print("onFriendRichPresenceUpdate", new Object[]{var1, var2});
   }

   public void onGameRichPresenceJoinRequested(SteamID var1, String var2) {
      this.print("onGameRichPresenceJoinRequested", new Object[]{var1, var2});
   }

   public void onGameServerChangeRequested(String var1, String var2) {
      this.print("onGameServerChangeRequested", new Object[]{var1, var2});
   }
}
