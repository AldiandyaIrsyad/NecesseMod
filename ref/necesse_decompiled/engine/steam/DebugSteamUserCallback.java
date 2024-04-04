package necesse.engine.steam;

import com.codedisaster.steamworks.SteamAuth;
import com.codedisaster.steamworks.SteamAuthTicket;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUserCallback;

public class DebugSteamUserCallback extends DebugSteamCallback implements SteamUserCallback {
   public DebugSteamUserCallback() {
   }

   public void onValidateAuthTicket(SteamID var1, SteamAuth.AuthSessionResponse var2, SteamID var3) {
      this.print("onValidateAuthTicket", new Object[]{var1, var2, var3});
   }

   public void onMicroTxnAuthorization(int var1, long var2, boolean var4) {
      this.print("onMicroTxnAuthorization", new Object[]{var1, var2, var4});
   }

   public void onEncryptedAppTicket(SteamResult var1) {
      this.print("onEncryptedAppTicket", new Object[]{var1});
   }

   public void onAuthSessionTicket(SteamAuthTicket var1, SteamResult var2) {
      this.print("onAuthSessionTicket", new Object[]{var1, var2});
   }
}
