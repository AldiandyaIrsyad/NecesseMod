package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;

public abstract class EmptyMobAbility extends MobAbility {
   public EmptyMobAbility() {
   }

   public void runAndSend() {
      this.runAndSendAbility(new Packet());
   }

   public void executePacket(PacketReader var1) {
      this.run();
   }

   protected abstract void run();
}
