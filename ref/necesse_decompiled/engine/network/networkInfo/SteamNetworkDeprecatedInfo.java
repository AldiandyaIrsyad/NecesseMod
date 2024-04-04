package necesse.engine.network.networkInfo;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworking.P2PSend;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import necesse.engine.GameLog;

public class SteamNetworkDeprecatedInfo extends NetworkInfo {
   public static final int defaultChannel = 0;
   public final SteamNetworking networking;
   public final SteamID remoteID;

   public SteamNetworkDeprecatedInfo(SteamNetworking var1, SteamID var2) {
      this.networking = var1;
      this.remoteID = var2;
   }

   public void send(byte[] var1) throws IOException {
      ByteBuffer var2 = ByteBuffer.allocateDirect(var1.length);
      var2.put(var1);
      var2.flip();

      try {
         if (!this.networking.sendP2PPacket(this.remoteID, var2, P2PSend.Unreliable, 0)) {
            GameLog.warn.println("Could not send packet to " + SteamID.getNativeHandle(this.remoteID) + " with length " + var1.length);
         }

      } catch (SteamException var4) {
         throw new IOException(var4);
      }
   }

   public String getDisplayName() {
      return "STEAM:" + SteamID.getNativeHandle(this.remoteID);
   }

   public void closeConnection() {
   }

   public void resetConnection() {
      this.networking.closeP2PSessionWithUser(this.remoteID);
      this.networking.acceptP2PSessionWithUser(this.remoteID);
   }

   public boolean equals(Object var1) {
      if (var1 instanceof SteamNetworkDeprecatedInfo) {
         SteamNetworkDeprecatedInfo var2 = (SteamNetworkDeprecatedInfo)var1;
         return Objects.equals(this.remoteID, var2.remoteID);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (int)SteamID.getNativeHandle(this.remoteID);
   }
}
