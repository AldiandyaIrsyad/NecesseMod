package necesse.engine.network.server.network;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.packet.PacketRequestSession;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.WarningMessageCooldown;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPosition;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public abstract class ServerNetwork {
   protected final Server server;
   protected WarningMessageCooldown<NetworkInfo> unknownPacketTimeouts = new WarningMessageCooldown(5000, 120000);

   public ServerNetwork(Server var1) {
      this.server = var1;
   }

   public abstract void open() throws IOException;

   public abstract boolean isOpen();

   public abstract String getAddress();

   public abstract void sendPacket(NetworkPacket var1);

   public void sendPacket(Packet var1, ServerClient var2) {
      NetworkPacket var3 = new NetworkPacket(var1, var2.networkInfo);
      this.sendPacket(var3);
      var2.submitOutPacket(var3);
   }

   public void sendPacket(Packet var1, Predicate<ServerClient> var2) {
      this.server.streamClients().filter(var2).forEach((var2x) -> {
         this.sendPacket(var1, var2x);
      });
   }

   public void sendToAllClients(Packet var1) {
      this.sendPacket(var1, (var0) -> {
         return true;
      });
   }

   public void sendToClientsWithAnyRegion(Packet var1, Collection<RegionPosition> var2) {
      if (!var2.isEmpty()) {
         this.sendPacket(var1, (var1x) -> {
            Stream var10000 = var2.stream();
            Objects.requireNonNull(var1x);
            return var10000.anyMatch(var1x::hasRegionLoaded);
         });
      }
   }

   public void sendToClientsWithRegion(Packet var1, LevelIdentifier var2, int var3, int var4) {
      this.sendPacket(var1, (var3x) -> {
         return var3x.hasRegionLoaded(var2, var3, var4);
      });
   }

   public void sendToClientsWithRegion(Packet var1, Level var2, int var3, int var4) {
      this.sendPacket(var1, (var3x) -> {
         return var3x.hasRegionLoaded(var2, var3, var4);
      });
   }

   public void sendToClientsWithRegion(Packet var1, RegionPosition var2) {
      this.sendPacket(var1, (var1x) -> {
         return var1x.hasRegionLoaded(var2);
      });
   }

   public void sendToClientsWithAnyRegionExcept(Packet var1, Collection<RegionPosition> var2, ServerClient var3) {
      if (!var2.isEmpty()) {
         this.sendPacket(var1, (var2x) -> {
            boolean var3x;
            if (var2x != var3) {
               Stream var10000 = var2.stream();
               Objects.requireNonNull(var2x);
               if (var10000.anyMatch(var2x::hasRegionLoaded)) {
                  var3x = true;
                  return var3x;
               }
            }

            var3x = false;
            return var3x;
         });
      }
   }

   public void sendToClientsWithRegionExcept(Packet var1, LevelIdentifier var2, int var3, int var4, ServerClient var5) {
      this.sendPacket(var1, (var4x) -> {
         return var4x != var5 && var4x.hasRegionLoaded(var2, var3, var4);
      });
   }

   public void sendToClientsWithRegionExcept(Packet var1, Level var2, int var3, int var4, ServerClient var5) {
      this.sendPacket(var1, (var4x) -> {
         return var4x != var5 && var4x.hasRegionLoaded(var2, var3, var4);
      });
   }

   public void sendToClientsWithRegionExcept(Packet var1, RegionPosition var2, ServerClient var3) {
      this.sendPacket(var1, (var2x) -> {
         return var2x != var3 && var2x.hasRegionLoaded(var2);
      });
   }

   public void sendToClientsWithEntity(Packet var1, RegionPositionGetter var2) {
      Collection var3 = var2.getRegionPositions();
      if (var3.isEmpty()) {
         this.sendToClientsAt(var1, var2.getLevel());
      } else {
         this.sendToClientsWithAnyRegion(var1, var3);
      }

   }

   public void sendToClientsWithEntityExcept(Packet var1, RegionPositionGetter var2, ServerClient var3) {
      Collection var4 = var2.getRegionPositions();
      if (var4.isEmpty()) {
         this.sendToClientsAtExcept(var1, var2.getLevel(), var3);
      } else {
         this.sendToClientsWithAnyRegionExcept(var1, var4, var3);
      }

   }

   public void sendToClientsWithTile(Packet var1, Level var2, int var3, int var4) {
      RegionPosition var5 = var2.regionManager.getRegionPosByTile(var3, var4);
      this.sendToClientsWithRegion(var1, var2, var5.regionX, var5.regionY);
   }

   public void sendToClientsWithTileExcept(Packet var1, Level var2, int var3, int var4, ServerClient var5) {
      RegionPosition var6 = var2.regionManager.getRegionPosByTile(var3, var4);
      this.sendToClientsWithRegionExcept(var1, var2, var6.regionX, var6.regionY, var5);
   }

   /** @deprecated */
   @Deprecated
   public void sendToClientsAt(Packet var1, ServerClient var2) {
      this.sendPacket(var1, (var1x) -> {
         return var1x.isSamePlace(var2);
      });
   }

   /** @deprecated */
   @Deprecated
   public void sendToClientsAtExcept(Packet var1, ServerClient var2, ServerClient var3) {
      this.sendPacket(var1, (var2x) -> {
         return var2x != var3 && var2x.isSamePlace(var2);
      });
   }

   /** @deprecated */
   @Deprecated
   public void sendToClientsAtExcept(Packet var1, LevelIdentifier var2, ServerClient var3) {
      this.sendPacket(var1, (var2x) -> {
         return var2x != var3 && var2x.isSamePlace(var2);
      });
   }

   /** @deprecated */
   @Deprecated
   public void sendToClientsAtExcept(Packet var1, Level var2, ServerClient var3) {
      if (var2 != null) {
         this.sendToClientsAtExcept(var1, var2.getIdentifier(), var3);
      }
   }

   /** @deprecated */
   @Deprecated
   public void sendToClientsAt(Packet var1, LevelIdentifier var2) {
      this.sendPacket(var1, (var1x) -> {
         return var1x.isSamePlace(var2);
      });
   }

   /** @deprecated */
   @Deprecated
   public void sendToClientsAt(Packet var1, int var2, int var3, int var4) {
      this.sendPacket(var1, (var3x) -> {
         return var3x.isSamePlace(var2, var3, var4);
      });
   }

   /** @deprecated */
   @Deprecated
   public void sendToClientsAt(Packet var1, Level var2) {
      if (var2 != null) {
         this.sendToClientsAt(var1, var2.getIdentifier());
      }
   }

   public void sendToAllClientsExcept(Packet var1, ServerClient var2) {
      this.sendPacket(var1, (var1x) -> {
         return var1x != var2;
      });
   }

   public abstract void close();

   public abstract String getDebugString();

   public void tickUnknownPacketTimeouts() {
      this.unknownPacketTimeouts.tickTimeouts();
   }

   public void submitUnknownPacket(NetworkPacket var1) {
      this.unknownPacketTimeouts.submit(var1.networkInfo, (var2) -> {
         String var3 = var1.getInfoDisplayName();
         System.out.println("Got packet " + PacketRegistry.getPacketSimpleName(var1.type) + " from unknown client \"" + var3 + "\" (" + var2 + "). Requesting session...");
         this.sendPacket(new NetworkPacket(new PacketRequestSession(), var1.networkInfo));
      });
   }
}
