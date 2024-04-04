package necesse.inventory.container.events;

import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketContainerEvent;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public abstract class ContainerEvent {
   public ContainerEvent() {
   }

   public ContainerEvent(PacketReader var1) {
   }

   public abstract void write(PacketWriter var1);

   public static void constructApplyAndSendToClients(Server var0, Function<ServerClient, ContainerEvent> var1) {
      var0.streamClients().forEach((var1x) -> {
         ContainerEvent var2 = (ContainerEvent)var1.apply(var1x);
         if (var1x.getContainer().shouldReceiveEvent(var2)) {
            var1x.sendPacket(new PacketContainerEvent(var2));
            var1x.getContainer().handleEvent(var2);
         }

      });
   }

   public void applyAndSendToClients(Server var1, Predicate<ServerClient> var2) {
      PacketContainerEvent var3 = new PacketContainerEvent(this);
      var1.streamClients().filter(var2).filter((var1x) -> {
         return var1x.getContainer().shouldReceiveEvent(this);
      }).forEach((var2x) -> {
         var2x.sendPacket(var3);
         var2x.getContainer().handleEvent(this);
      });
   }

   public void applyAndSendToClient(ServerClient var1) {
      var1.sendPacket(new PacketContainerEvent(this));
      var1.getContainer().handleEvent(this);
   }

   public void applyAndSendToAllClients(Server var1) {
      this.applyAndSendToClients(var1, (var0) -> {
         return true;
      });
   }

   public void applyAndSendToClientsAt(ServerClient var1) {
      this.applyAndSendToClients(var1.getServer(), (var1x) -> {
         return var1x.isSamePlace(var1);
      });
   }

   public void applyAndSendToClientsAt(Level var1) {
      if (var1.isServer()) {
         this.applyAndSendToClients(var1.getServer(), (var1x) -> {
            return var1x.isSamePlace(var1);
         });
      }

   }

   public void applyAndSendToClientsAtExcept(ServerClient var1) {
      this.applyAndSendToClients(var1.getServer(), (var1x) -> {
         return var1x.isSamePlace(var1) && var1x != var1;
      });
   }

   public void applyAndSendToClientsAtExcept(Level var1, ServerClient var2) {
      if (var1.isServer()) {
         this.applyAndSendToClients(var1.getServer(), (var2x) -> {
            return var2x.isSamePlace(var1) && var2x != var2;
         });
      }

   }
}
