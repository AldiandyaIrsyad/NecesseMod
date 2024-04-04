package necesse.engine.network.server.network;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamID;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ServerOpenNetwork extends ServerNetwork {
   public static final int[] lanPorts = new int[]{55169, 51868, 49745, 54198, 60631, 58737, 61410};
   public static String bindIP = null;
   final int port;
   private Thread listenThread;
   private Thread lanListenThread;
   private ServerSteamMessagesListenThread steamMessagesListenThread;
   private ServerSteamDeprecatedListenThread steamDeprecatedListenThread;
   private DatagramSocket socket;
   private DatagramSocket lanSocket;
   private boolean isOpen;

   public ServerOpenNetwork(Server var1, int var2) {
      super(var1);
      this.port = var2;
   }

   public void open() throws IOException {
      if (this.server.settings.allowConnectByIP) {
         int var1 = 0;

         while(var1 < lanPorts.length) {
            try {
               this.lanSocket = new DatagramSocket(lanPorts[var1]);
               System.out.println("Started lan socket at port " + lanPorts[var1]);
               break;
            } catch (SocketException var5) {
               if (var1 == lanPorts.length - 1) {
                  System.err.println("Could not start server LAN socket.");
               }

               ++var1;
            }
         }
      }

      try {
         if (this.server.settings.allowConnectByIP) {
            InetAddress var6;
            if (bindIP != null) {
               try {
                  var6 = InetAddress.getByName(bindIP);
                  System.out.println("Binding on address " + var6.getHostAddress());
               } catch (UnknownHostException var3) {
                  GameLog.warn.println("Could not parse bind IP " + bindIP);
                  var6 = null;
               }
            } else {
               var6 = null;
            }

            this.socket = new DatagramSocket(this.port, var6);
            this.listenThread = new ServerListenThread("Server Socket", this.socket);
            this.listenThread.start();
            if (this.lanSocket != null) {
               this.lanListenThread = new ServerListenThread("Server LAN Socket", this.lanSocket);
               this.lanListenThread.start();
            }
         }

         if (SteamAPI.isSteamRunning()) {
            this.steamDeprecatedListenThread = new ServerSteamDeprecatedListenThread("Steam P2P Socket", this);
            this.steamDeprecatedListenThread.start();
            this.steamMessagesListenThread = new ServerSteamMessagesListenThread("Steam P2P Socket", this);
            this.steamMessagesListenThread.start();
         }

         this.isOpen = true;
      } catch (SocketException var4) {
         if (this.listenThread != null) {
            this.listenThread.interrupt();
         }

         if (this.lanListenThread != null) {
            this.lanListenThread.interrupt();
         }

         var4.printStackTrace();
         this.isOpen = false;
         throw var4;
      }
   }

   public boolean isOpen() {
      return this.isOpen;
   }

   public String getAddress() {
      if (this.socket == null) {
         if (this.steamDeprecatedListenThread != null) {
            return "Steam P2P Deprecated";
         } else {
            return this.steamMessagesListenThread != null ? "Steam P2P Messages" : null;
         }
      } else {
         InetAddress var1 = this.socket.getLocalAddress();
         if (var1 == null || var1.isAnyLocalAddress()) {
            var1 = this.socket.getInetAddress();
         }

         if (var1 == null) {
            try {
               var1 = InetAddress.getLocalHost();
            } catch (UnknownHostException var3) {
            }
         }

         return var1 == null ? "Unknown:" + this.port : var1.getHostAddress() + ":" + this.port;
      }
   }

   public void close() {
      if (this.lanSocket != null) {
         this.lanSocket.close();
      }

      if (this.lanListenThread != null) {
         this.lanListenThread.interrupt();
      }

      if (this.socket != null) {
         this.socket.close();
      }

      if (this.listenThread != null) {
         this.listenThread.interrupt();
      }

      if (this.steamDeprecatedListenThread != null) {
         this.steamDeprecatedListenThread.interrupt();
      }

      if (this.steamMessagesListenThread != null) {
         this.steamMessagesListenThread.interrupt();
      }

      this.isOpen = false;
   }

   public void sendPacket(NetworkPacket var1) {
      this.server.packetManager.submitOutPacket(var1);
      if (var1.networkInfo == null) {
         this.server.getLocalClient().submitSinglePlayerPacket(this.server.getLocalClient().packetManager, var1);
      } else {
         try {
            var1.sendPacket();
         } catch (IOException var3) {
            var3.printStackTrace();
         }

      }
   }

   public int getPort() {
      return this.port;
   }

   public void addInvitedUser(SteamID var1) {
      if (this.steamDeprecatedListenThread != null) {
         this.steamDeprecatedListenThread.addInvitedUser(var1);
      }

      if (this.steamMessagesListenThread != null) {
         this.steamMessagesListenThread.addInvitedUser(var1);
      }

      GameLog.debug.println("Added invited user : " + var1);
   }

   public String getDebugString() {
      return "port " + this.port;
   }

   private class ServerListenThread extends Thread {
      private DatagramSocket socket;

      public ServerListenThread(String var2, DatagramSocket var3) {
         super(var2);
         this.socket = var3;
         this.setPriority(Math.min(10, this.getPriority() + 1));
      }

      public boolean isSocketOpen() {
         return this.socket != null && !this.socket.isClosed();
      }

      public void run() {
         while(true) {
            if (this.isSocketOpen()) {
               byte[] var1 = new byte[1024];
               DatagramPacket var2 = new DatagramPacket(var1, var1.length);

               try {
                  this.socket.receive(var2);
                  NetworkPacket var3 = new NetworkPacket(this.socket, var2);
                  ServerOpenNetwork.this.server.packetManager.submitInPacket(var3);
                  continue;
               } catch (IOException var7) {
                  if (!this.socket.isClosed()) {
                     var7.printStackTrace();
                     continue;
                  }
               } catch (UnknownPacketException var8) {
                  DatagramNetworkInfo var4 = new DatagramNetworkInfo(this.socket, var2.getAddress(), var2.getPort());
                  ServerClient var5 = ServerOpenNetwork.this.server.getPacketClient(var4);
                  String var6 = var5 == null ? "N/A" : var5.getName();
                  GameLog.warn.println("Server received unknown packet from client " + var6 + ": " + var8.getMessage());
                  continue;
               }
            }

            if (this.isSocketOpen()) {
               this.socket.close();
            }

            return;
         }
      }
   }
}
