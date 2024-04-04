package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public abstract class TargetedMobAbility extends MobAbility {
   public TargetedMobAbility() {
   }

   public void runAndSend(Mob var1) {
      Packet var2 = new Packet();
      PacketWriter var3 = new PacketWriter(var2);
      var3.putNextInt(var1.getUniqueID());
      this.runAndSendAbility(var2);
   }

   public void executePacket(PacketReader var1) {
      int var2 = var1.getNextInt();
      Mob var3 = GameUtils.getLevelMob(var2, this.getMob().getLevel());
      this.run(var3);
   }

   protected abstract void run(Mob var1);
}
