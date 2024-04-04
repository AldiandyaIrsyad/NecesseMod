package necesse.engine.network;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamNetworkingMessages;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.networkInfo.SteamNetworkDeprecatedInfo;
import necesse.engine.network.networkInfo.SteamNetworkMessagesInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.PacketRegistry;

public class NetworkPacket {
   public static final int MAX_PACKET_SIZE = 1024;
   private static final int MAX_CONTENT_SIZE = 992;
   private static final Object identifierLock = new Object();
   private static int identifierIncrement = 0;
   public final int type;
   public final long timestamp;
   private int packetIndex;
   public final int identifier;
   private int complete;
   private final PacketData[] packets;
   private Packet packet;
   public final NetworkInfo networkInfo;

   public NetworkPacket(Packet var1, NetworkInfo var2) {
      this.packet = var1;
      this.networkInfo = var2;
      this.type = PacketRegistry.getPacketID(var1.getClass());
      if (this.type == -1) {
         throw new IllegalStateException(var1.getClass().getSimpleName() + " class is not registered in PacketData.");
      } else {
         this.timestamp = PacketRegistry.hasTimestamp(this.type) ? System.nanoTime() : 0L;
         byte[] var3 = var1.getPacketData();
         int var4 = var3.length / 992;
         this.packetIndex = 0;
         if (var3.length % 992 != 0 || var3.length == 0) {
            ++var4;
         }

         if (var4 > 1) {
            synchronized(identifierLock) {
               ++identifierIncrement;
               if (identifierIncrement == 0) {
                  ++identifierIncrement;
               }

               this.identifier = identifierIncrement;
            }
         } else {
            this.identifier = 0;
         }

         this.packets = new PacketData[var4];
         this.complete = var4;

         for(int var5 = 0; var5 < var4; ++var5) {
            Packet var6 = new Packet();
            PacketWriter var7 = new PacketWriter(var6);
            var7.putNextBoolean(var4 > 1);
            if (var4 > 1) {
               var7.putNextInt(this.identifier);
               var7.putNextShortUnsigned(var5);
               var7.putNextShortUnsigned(var4);
            }

            if (var5 == 0) {
               var7.putNextShortUnsigned(this.type);
               var7.putNextBoolean(this.timestamp != 0L);
               if (this.timestamp != 0L) {
                  var7.putNextLong(this.timestamp);
               }
            }

            byte[] var8 = Arrays.copyOfRange(var3, var5 * 992, Math.min(var3.length, (var5 + 1) * 992));
            var7.putNextBytes(var8);
            this.packets[var5] = new PacketData(var8, var6.getPacketData());
         }

      }
   }

   public NetworkPacket(DatagramSocket var1, DatagramPacket var2) throws UnknownPacketException {
      this((NetworkInfo)(new DatagramNetworkInfo(var1, var2.getAddress(), var2.getPort())), (byte[])Arrays.copyOf(var2.getData(), var2.getLength()));
   }

   public NetworkPacket(SteamNetworking var1, SteamID var2, byte[] var3) throws UnknownPacketException {
      this((NetworkInfo)(new SteamNetworkDeprecatedInfo(var1, var2)), (byte[])var3);
   }

   public NetworkPacket(SteamNetworkingMessages var1, SteamID var2, byte[] var3) throws UnknownPacketException {
      this((NetworkInfo)(new SteamNetworkMessagesInfo(var1, var2)), (byte[])var3);
   }

   public NetworkPacket(NetworkInfo var1, byte[] var2) throws UnknownPacketException {
      this.networkInfo = var1;
      Packet var3 = new Packet(var2);
      PacketReader var4 = new PacketReader(var3);
      var4.throwIfIndexAboveSize();

      try {
         int var5;
         if (var4.getNextBoolean()) {
            this.identifier = var4.getNextInt();
            this.packetIndex = var4.getNextShortUnsigned();
            var5 = var4.getNextShortUnsigned();
         } else {
            this.identifier = 0;
            this.packetIndex = 0;
            var5 = 1;
         }

         if (this.packetIndex == 0) {
            this.type = var4.getNextShortUnsigned();
            if (this.type >= PacketRegistry.getTotalRegistered()) {
               throw new UnknownPacketException("Unknown packet type: " + this.type);
            }

            if (var4.getNextBoolean()) {
               this.timestamp = var4.getNextLong();
            } else {
               this.timestamp = 0L;
            }
         } else {
            this.type = -1;
            this.timestamp = 0L;
         }

         this.packets = new PacketData[var5];
         this.packets[this.packetIndex] = new PacketData(var4.getRemainingBytes(), var2);
         this.complete = 1;
      } catch (IndexOutOfBoundsException var6) {
         throw new UnknownPacketException("Read out of bounds", var6);
      }
   }

   public NetworkPacket(NetworkPacket var1) {
      this.networkInfo = var1.networkInfo;
      this.type = var1.type;
      this.timestamp = var1.timestamp;
      this.identifier = var1.identifier;
      this.packetIndex = var1.packetIndex;
      this.packets = var1.packets;
      this.complete = var1.complete;
      this.packet = null;
   }

   public boolean canMerge(NetworkPacket var1) {
      return this.identifier != 0 && this.identifier == var1.identifier && Objects.equals(this.networkInfo, var1.networkInfo);
   }

   public NetworkPacket mergePackets(NetworkPacket var1) {
      if (!this.canMerge(var1)) {
         throw new IllegalArgumentException("Merge packet have different identifier");
      } else {
         if (this.packets[var1.packetIndex] == null && var1.packets[var1.packetIndex] != null) {
            ++this.complete;
            this.packets[var1.packetIndex] = var1.packets[var1.packetIndex];
         }

         if (var1.type != -1) {
            System.arraycopy(this.packets, 0, var1.packets, 0, this.packets.length);
            return var1;
         } else {
            return this;
         }
      }
   }

   public void sendPacket() throws IOException {
      if (!this.isComplete()) {
         throw new IllegalStateException("Cannot send incomplete packet");
      } else {
         PacketData[] var1 = this.packets;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            PacketData var4 = var1[var3];
            var4.send();
         }

      }
   }

   public boolean isComplete() {
      return this.complete == this.packets.length;
   }

   public Packet getTypePacket() {
      if (this.packet != null) {
         return this.packet;
      } else if (this.type != -1 && this.isComplete()) {
         byte[] var1 = new byte[0];
         PacketData[] var2 = this.packets;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            PacketData var5 = var2[var4];
            int var6 = var1.length;
            var1 = Arrays.copyOf(var1, var1.length + var5.content.length);
            System.arraycopy(var5.content, 0, var1, var6, var5.content.length);
         }

         try {
            this.packet = PacketRegistry.createPacket(this.type, var1);
         } catch (IllegalAccessException var7) {
            var7.printStackTrace();
         } catch (NoSuchElementException var8) {
            System.err.println("Could not find received packet with type 0x" + Integer.toHexString(this.type));
         } catch (InstantiationException var9) {
            System.err.println("Could not instantiate packet " + PacketRegistry.getPacketSimpleName(this.type));
            if (var9.getCause() != null) {
               var9.getCause().printStackTrace();
            } else {
               var9.printStackTrace();
            }
         } catch (InvocationTargetException var10) {
            System.err.println("Could not construct packet " + PacketRegistry.getPacketSimpleName(this.type));
            if (var10.getCause() != null) {
               var10.getCause().printStackTrace();
            } else {
               var10.printStackTrace();
            }
         } catch (Exception var11) {
            System.err.println("Unknown error creating packet " + PacketRegistry.getPacketSimpleName(this.type));
            var11.printStackTrace();
         }

         return this.packet;
      } else {
         throw new IllegalStateException("Packet is part of a bigger one. Wait for remaining content");
      }
   }

   public int getCurrentByteSize() {
      int var1 = 0;
      PacketData[] var2 = this.packets;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PacketData var5 = var2[var4];
         if (var5 != null) {
            var1 += var5.packet.length;
         }
      }

      return var1;
   }

   public int getByteSize() {
      if (!this.isComplete()) {
         throw new IllegalStateException("Packet is not complete, use getCurrentByteSize");
      } else {
         return this.getCurrentByteSize();
      }
   }

   public void processServer(Server var1, ServerClient var2) {
      Packet var3 = this.getTypePacket();
      if (var3 != null) {
         var3.processServer(this, var1, var2);
      }

   }

   public void processClient(Client var1) {
      Packet var2 = this.getTypePacket();
      if (var2 != null) {
         var2.processClient(this, var1);
      }

   }

   public String getInfoDisplayName() {
      return this.networkInfo == null ? "LOCAL" : this.networkInfo.getDisplayName();
   }

   private class PacketData {
      private final byte[] content;
      private final byte[] packet;

      public PacketData(byte[] var2, byte[] var3) {
         this.content = var2;
         this.packet = var3;
      }

      public void send() throws IOException {
         NetworkPacket.this.networkInfo.send(this.packet);
      }
   }
}
