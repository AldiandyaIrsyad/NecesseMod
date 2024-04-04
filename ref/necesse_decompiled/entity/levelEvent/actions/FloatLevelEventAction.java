package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class FloatLevelEventAction extends LevelEventAction {
   public FloatLevelEventAction() {
   }

   public void runAndSend(float var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextFloat(var1);
      this.runAndSendAction(var2);
   }

   public void executePacket(PacketReader var1) {
      float var2 = var1.getNextFloat();
      this.run(var2);
   }

   protected abstract void run(float var1);
}
