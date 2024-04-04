package necesse.engine.network.networkInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Objects;
import necesse.engine.util.GameRandom;

public class DatagramNetworkInfo extends NetworkInfo {
   public final DatagramSocket socket;
   public final InetAddress address;
   public final int port;

   public DatagramNetworkInfo(DatagramSocket var1, InetAddress var2, int var3) {
      this.socket = var1;
      this.address = var2;
      this.port = var3;
   }

   public void send(byte[] var1) throws IOException {
      if (this.socket != null && !this.socket.isClosed()) {
         this.socket.send(new DatagramPacket(var1, var1.length, this.address, this.port));
      }
   }

   public String getDisplayName() {
      return this.address.getHostName() + ":" + this.port;
   }

   public void closeConnection() {
   }

   public void resetConnection() {
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DatagramNetworkInfo)) {
         return false;
      } else {
         DatagramNetworkInfo var2 = (DatagramNetworkInfo)var1;
         return Objects.equals(this.address, var2.address) && this.port == var2.port;
      }
   }

   public int hashCode() {
      int var1 = this.address == null ? 1337 : this.address.hashCode();
      var1 = var1 * GameRandom.prime(76) + this.port;
      return var1;
   }
}
