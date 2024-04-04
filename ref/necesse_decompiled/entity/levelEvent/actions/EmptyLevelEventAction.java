package necesse.entity.levelEvent.actions;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;

public abstract class EmptyLevelEventAction extends LevelEventAction {
   public EmptyLevelEventAction() {
   }

   public void runAndSend() {
      this.runAndSendAction(new Packet());
   }

   public void executePacket(PacketReader var1) {
      this.run();
   }

   protected abstract void run();
}
