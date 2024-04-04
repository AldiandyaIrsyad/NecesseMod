package necesse.engine.steam;

import com.codedisaster.steamworks.SteamUtilsCallback;

public class DebugSteamUtilsCallback extends DebugSteamCallback implements SteamUtilsCallback {
   public DebugSteamUtilsCallback() {
   }

   public void onSteamShutdown() {
      this.print("onSteamShutdown", new Object[0]);
   }
}
