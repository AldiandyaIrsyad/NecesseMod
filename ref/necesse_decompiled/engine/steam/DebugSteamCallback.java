package necesse.engine.steam;

import com.codedisaster.steamworks.SteamID;
import java.util.Objects;
import necesse.engine.GameLog;

public class DebugSteamCallback {
   public DebugSteamCallback() {
   }

   public void print(boolean var1, String var2, Object... var3) {
      StringBuilder var4 = new StringBuilder("Steam: ");
      if (var2 != null) {
         var4.append(var2).append(": ");
      }

      for(int var5 = 0; var5 < var3.length; ++var5) {
         var4.append("Arg ").append(var5).append(": ").append(this.argsToString(var3[var5]));
         if (var5 < var3.length - 1) {
            var4.append(", ");
         }
      }

      GameLog.debug.println(var4.toString());
      if (var1) {
         (new Throwable()).printStackTrace(GameLog.debug);
      }

   }

   public void print(String var1, Object... var2) {
      this.print(false, var1, var2);
   }

   public String argsToString(Object var1) {
      return var1 instanceof SteamID ? "STEAM64-" + SteamID.getNativeHandle((SteamID)var1) : Objects.toString(var1);
   }
}
