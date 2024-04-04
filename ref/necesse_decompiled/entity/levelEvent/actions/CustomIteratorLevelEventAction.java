package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public abstract class CustomIteratorLevelEventAction extends LevelEventAction {
   public CustomIteratorLevelEventAction() {
   }

   public void runAndSend() {
      Packet var1 = new Packet();
      this.write(new PacketWriter(var1));
      this.runAndSendAction(var1);
   }

   public void executePacket(PacketReader var1) {
      this.read(var1);
   }

   protected abstract void write(PacketWriter var1);

   protected abstract void read(PacketReader var1);
}
