package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketContainerCustomAction;
import necesse.inventory.container.Container;

public abstract class ContainerCustomAction {
   private Container container;
   private int id = -1;

   public ContainerCustomAction() {
   }

   public void onRegister(Container var1, int var2) {
      if (this.container != null) {
         throw new IllegalStateException("Cannot register same custom action twice");
      } else {
         this.container = var1;
         this.id = var2;
      }
   }

   protected Container getContainer() {
      return this.container;
   }

   public abstract void executePacket(PacketReader var1);

   protected void runAndSendAction(Packet var1) {
      if (this.container != null) {
         if (this.container.client.isServer()) {
            this.container.client.getServerClient().sendPacket(new PacketContainerCustomAction(this.container, this.id, var1));
         } else if (this.container.client.isClient()) {
            this.container.client.getClientClient().getClient().network.sendPacket(new PacketContainerCustomAction(this.container, this.id, var1));
         }

         this.executePacket(new PacketReader(var1));
      } else {
         System.err.println("Cannot run custom action that hasn't been registered");
      }

   }
}
