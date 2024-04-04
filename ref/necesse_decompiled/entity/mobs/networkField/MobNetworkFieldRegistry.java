package necesse.entity.mobs.networkField;

import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketMobNetworkFields;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class MobNetworkFieldRegistry extends NetworkFieldRegistry {
   private final Mob mob;

   public MobNetworkFieldRegistry(Mob var1) {
      this.mob = var1;
   }

   public void sendUpdatePacket(Packet var1) {
      this.mob.getLevel().getServer().network.sendToClientsAt(new PacketMobNetworkFields(this.mob, var1), (Level)this.mob.getLevel());
   }

   public String getDebugIdentifierString() {
      return this.mob.toString();
   }
}
