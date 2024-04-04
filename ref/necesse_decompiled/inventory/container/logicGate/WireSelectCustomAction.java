package necesse.inventory.container.logicGate;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.customAction.ContainerCustomAction;

public abstract class WireSelectCustomAction extends ContainerCustomAction {
   public WireSelectCustomAction() {
   }

   public void runAndSend(boolean[] var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      boolean[] var4 = var1;
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         boolean var7 = var4[var6];
         var3.putNextBoolean(var7);
      }

      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      boolean[] var2 = new boolean[4];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         var2[var3] = var1.getNextBoolean();
      }

      this.run(var2);
   }

   protected abstract void run(boolean[] var1);
}
