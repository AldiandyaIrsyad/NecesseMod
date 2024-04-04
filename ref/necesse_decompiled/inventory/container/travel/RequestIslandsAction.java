package necesse.inventory.container.travel;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;

public class RequestIslandsAction extends ContainerCustomAction {
   public final TravelContainer travelContainer;

   public RequestIslandsAction(TravelContainer var1) {
      this.travelContainer = var1;
   }

   public void runAndSend(int var1, int var2, int var3, int var4) {
      Packet var5 = new Packet();
      PacketWriter var6 = new PacketWriter(var5);
      var6.putNextInt(var1);
      var6.putNextInt(var2);
      var6.putNextInt(var3);
      var6.putNextInt(var4);
      this.runAndSendAction(var5);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      int var3 = var1.getNextInt();
      int var4 = var1.getNextInt();
      int var5 = var1.getNextInt();
      if (this.travelContainer.client.isServer()) {
         ServerClient var6 = this.travelContainer.client.getServerClient();
         (new IslandsResponseEvent(var6.getServer(), var6, this.travelContainer, var2, var3, var4, var5)).applyAndSendToClient(var6);
      }

   }
}
