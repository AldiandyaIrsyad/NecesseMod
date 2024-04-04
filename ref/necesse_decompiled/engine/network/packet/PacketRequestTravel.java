package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.inventory.container.travel.TravelContainer;
import necesse.inventory.container.travel.TravelDir;

public class PacketRequestTravel extends Packet {
   public final TravelDir travelDir;

   public PacketRequestTravel(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      int var3 = var2.getNextByteUnsigned();
      TravelDir[] var4 = TravelDir.values();
      if (var3 >= 0 && var3 < var4.length) {
         this.travelDir = var4[var3];
      } else {
         this.travelDir = null;
      }

   }

   public PacketRequestTravel(TravelDir var1) {
      this.travelDir = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(var1.ordinal());
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.playerMob != null) {
         boolean var4 = true;
         if (this.travelDir != TravelDir.None) {
            var4 = this.travelDir == TravelContainer.getTravelDir(var3.playerMob);
         }

         if (var4) {
            PacketOpenContainer var5 = new PacketOpenContainer(ContainerRegistry.TRAVEL_CONTAINER, TravelContainer.getContainerContentPacket(var2, var3, this.travelDir));
            ContainerRegistry.openAndSendContainer(var3, var5);
         }

      }
   }
}
