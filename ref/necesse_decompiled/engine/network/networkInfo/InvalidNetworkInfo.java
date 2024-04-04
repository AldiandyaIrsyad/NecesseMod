package necesse.engine.network.networkInfo;

import java.io.IOException;

public class InvalidNetworkInfo extends NetworkInfo {
   public InvalidNetworkInfo() {
   }

   public void send(byte[] var1) throws IOException {
   }

   public String getDisplayName() {
      return "INVALID";
   }

   public void closeConnection() {
   }

   public void resetConnection() {
   }

   public boolean equals(Object var1) {
      return false;
   }

   public int hashCode() {
      return -1;
   }
}
