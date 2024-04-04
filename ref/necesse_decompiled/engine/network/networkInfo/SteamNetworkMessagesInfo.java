package necesse.engine.network.networkInfo;

import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworkingMessages;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamNetworkingMessages.SendFlag;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Objects;
import necesse.engine.GameLog;

public class SteamNetworkMessagesInfo extends NetworkInfo {
   public static SteamNetworkingMessages.SendFlag sendFlag;
   public static final int defaultChannel = 0;
   public static final HashMap<Long, HashMap<SteamResult, MessageWarnings>> messageWarnings;
   public static int messageWarningCooldown;
   public final SteamNetworkingMessages networking;
   public final SteamID remoteID;

   public SteamNetworkMessagesInfo(SteamNetworkingMessages var1, SteamID var2) {
      this.networking = var1;
      this.remoteID = var2;
   }

   public void send(byte[] var1) throws IOException {
      ByteBuffer var2 = ByteBuffer.allocateDirect(var1.length);
      var2.put(var1);
      var2.flip();

      try {
         SteamResult var3 = this.networking.sendMessageToUser(this.remoteID, var2, sendFlag, 0);
         if (var3 != SteamResult.OK) {
            if (var3 != SteamResult.ConnectFailed && var3 != SteamResult.NoConnection && var3 != SteamResult.InvalidParam && var3 != SteamResult.InvalidState) {
               synchronized(messageWarnings) {
                  HashMap var5 = (HashMap)messageWarnings.compute(SteamID.getNativeHandle(this.remoteID), (var0, var1x) -> {
                     return var1x == null ? new HashMap() : var1x;
                  });
                  MessageWarnings var6 = (MessageWarnings)var5.compute(var3, (var0, var1x) -> {
                     return var1x == null ? new MessageWarnings() : var1x;
                  });
                  var6.submit("Could not send packet to " + SteamID.getNativeHandle(this.remoteID) + " with length " + var1.length + ": " + var3);
               }
            } else {
               this.networking.closeSessionWithUser(this.remoteID);
               GameLog.warn.println("Closed Steam session with " + SteamID.getNativeHandle(this.remoteID) + " because " + var3.name());
            }
         }

      } catch (SteamException var9) {
         throw new IOException(var9);
      }
   }

   public String getDisplayName() {
      return "STEAM:" + SteamID.getNativeHandle(this.remoteID);
   }

   public void closeConnection() {
   }

   public void resetConnection() {
      System.out.println("RESET CONNECTION " + this.remoteID);
      this.networking.closeSessionWithUser(this.remoteID);
      this.networking.acceptSessionWithUser(this.remoteID);
   }

   public boolean equals(Object var1) {
      if (var1 instanceof SteamNetworkMessagesInfo) {
         SteamNetworkMessagesInfo var2 = (SteamNetworkMessagesInfo)var1;
         return Objects.equals(this.remoteID, var2.remoteID);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return (int)SteamID.getNativeHandle(this.remoteID);
   }

   static {
      sendFlag = SendFlag.Unreliable;
      messageWarnings = new HashMap();
      messageWarningCooldown = 10000;
   }

   protected static class MessageWarnings {
      public long lastPrintTime;
      public int warningsSinceLastPrint;

      protected MessageWarnings() {
      }

      public void submit(String var1) {
         long var2 = System.currentTimeMillis() - this.lastPrintTime;
         if (var2 >= (long)SteamNetworkMessagesInfo.messageWarningCooldown) {
            if (this.warningsSinceLastPrint > 1) {
               var1 = var1 + " (" + this.warningsSinceLastPrint + ")";
            }

            GameLog.warn.println(var1);
            this.lastPrintTime = System.currentTimeMillis();
            this.warningsSinceLastPrint = 0;
         }

         ++this.warningsSinceLastPrint;
      }
   }
}
