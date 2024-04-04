package necesse.engine.steam;

import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamID;

public abstract class SteamFriend {
   public final SteamID steamID;

   public SteamFriend(SteamID var1) {
      this.steamID = var1;
   }

   public abstract String getName();

   public abstract SteamFriends.PersonaState getState();

   public abstract SteamFriends.FriendRelationship getRelationship();
}
