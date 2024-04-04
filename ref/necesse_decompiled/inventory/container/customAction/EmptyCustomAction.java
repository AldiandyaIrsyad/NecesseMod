package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;

public abstract class EmptyCustomAction extends ContainerCustomAction {
   public EmptyCustomAction() {
   }

   public void runAndSend() {
      this.runAndSendAction(new Packet());
   }

   public void executePacket(PacketReader var1) {
      this.run();
   }

   protected abstract void run();
}
