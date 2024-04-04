package necesse.engine.network.client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import necesse.engine.GameLog;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;

public class ClientAddressNetwork extends ClientNetwork {
   public final Client client;
   public final String addressString;
   public final int port;
   private String openError;
   private InetAddress address;
   private DatagramSocket socket;
   private Thread listenThread;

   public ClientAddressNetwork(Client var1, String var2, int var3) {
      this.client = var1;
      this.addressString = var2;
      this.port = var3;
   }

   public boolean openConnection() {
      try {
         this.socket = new DatagramSocket();
         this.address = InetAddress.getByName(this.addressString);
         this.listenThread = new Thread("Client Socket") {
            public void run() {
               while(true) {
                  if (ClientAddressNetwork.this.isOpen()) {
                     byte[] var1 = new byte[1024];
                     DatagramPacket var2 = new DatagramPacket(var1, var1.length);

                     try {
                        ClientAddressNetwork.this.socket.receive(var2);
                        NetworkPacket var3 = new NetworkPacket(ClientAddressNetwork.this.socket, var2);
                        ClientAddressNetwork.this.client.packetManager.submitInPacket(var3);
                        continue;
                     } catch (IOException var4) {
                        if (!ClientAddressNetwork.this.socket.isClosed()) {
                           var4.printStackTrace();
                           continue;
                        }
                     } catch (UnknownPacketException var5) {
                        GameLog.warn.println("Client received unknown packet from server " + var2.getAddress().toString() + ":" + var2.getPort());
                        continue;
                     }
                  }

                  if (ClientAddressNetwork.this.isOpen()) {
                     ClientAddressNetwork.this.socket.close();
                  }

                  return;
               }
            }
         };
         this.listenThread.start();
         return true;
      } catch (SocketException | UnknownHostException var2) {
         var2.printStackTrace();
         this.openError = var2.getMessage();
         return false;
      }
   }

   public String getOpenError() {
      return this.openError;
   }

   public boolean isOpen() {
      return this.socket != null && !this.socket.isClosed();
   }

   public void sendPacket(Packet var1) {
      NetworkPacket var2 = new NetworkPacket(var1, new DatagramNetworkInfo(this.socket, this.address, this.port));
      this.client.packetManager.submitOutPacket(var2);

      try {
         if (!this.socket.isClosed()) {
            var2.sendPacket();
         }
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public void close() {
      if (this.socket != null) {
         this.socket.close();
      }

      if (this.listenThread != null) {
         this.listenThread.interrupt();
      }

   }

   public boolean isLocalAddress() {
      return this.addressString.equals("localhost") || this.address == null || this.address.isSiteLocalAddress();
   }

   public String getDebugString() {
      String var1 = this.address == null ? "null" : this.address.getHostAddress();
      return this.addressString + ":" + this.port + (var1.equals(this.addressString) ? "" : " (" + var1 + ")");
   }

   public LocalMessage getPlayingMessage() {
      return new LocalMessage("richpresence", "playingonserver");
   }

   public void writeLobbyConnectInfo(BiConsumer<String, String> var1) {
      var1.accept("serverPort", String.valueOf(this.port));
      var1.accept("serverAddress", String.valueOf(this.addressString));
   }
}
