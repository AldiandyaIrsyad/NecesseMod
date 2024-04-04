package necesse.inventory.container.customAction;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class BooleanCustomAction extends ContainerCustomAction {
   public BooleanCustomAction() {
   }

   public void runAndSend(boolean var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextBoolean(var1);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      this.run(var1.getNextBoolean());
   }

   protected abstract void run(boolean var1);
}
