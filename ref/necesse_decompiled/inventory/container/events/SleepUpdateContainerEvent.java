package necesse.inventory.container.events;

import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.BedContainer;

public class SleepUpdateContainerEvent extends ContainerEvent {
   public final int totalSleeping;

   public SleepUpdateContainerEvent(Server var1, Predicate<ServerClient> var2) {
      Stream var3 = var1.streamClients();
      if (var2 != null) {
         var3 = var3.filter(var2);
      }

      this.totalSleeping = (int)var3.filter((var0) -> {
         return var0.getContainer() instanceof BedContainer;
      }).count();
   }

   public SleepUpdateContainerEvent(Server var1) {
      this(var1, (Predicate)null);
   }

   public SleepUpdateContainerEvent(PacketReader var1) {
      super(var1);
      this.totalSleeping = var1.getNextInt();
   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.totalSleeping);
   }
}
