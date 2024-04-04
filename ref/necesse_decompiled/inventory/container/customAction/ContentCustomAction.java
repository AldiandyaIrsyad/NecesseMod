package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class ContentCustomAction extends ContainerCustomAction {
   public ContentCustomAction() {
   }

   public void runAndSend(Packet var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextContentPacket(var1);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      this.run(var1.getNextContentPacket());
   }

   protected abstract void run(Packet var1);
}
