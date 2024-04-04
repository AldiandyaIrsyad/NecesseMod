package necesse.entity.mobs.ability;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.packet.PacketMobAbility;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public abstract class MobAbility {
   private Mob mob;
   private int id = -1;

   public MobAbility() {
   }

   protected void onRegister(Mob var1, int var2) {
      if (this.mob != null) {
         throw new IllegalStateException("Cannot register same ability twice");
      } else {
         this.mob = var1;
         this.id = var2;
      }
   }

   public Mob getMob() {
      return this.mob;
   }

   public abstract void executePacket(PacketReader var1);

   protected void runAndSendAbility(Packet var1) {
      if (this.mob != null) {
         if (this.mob.isServer()) {
            this.executePacket(new PacketReader(var1));
            this.mob.getLevel().getServer().network.sendToClientsAt(new PacketMobAbility(this.mob, this.id, var1), (Level)this.mob.getLevel());
         } else if (!this.mob.isClient()) {
            this.executePacket(new PacketReader(var1));
         } else {
            System.err.println("Cannot send mob abilities from client. Only server handles when mob abilities are ran");
         }
      } else {
         System.err.println("Cannot run ability that hasn't been registered");
      }

   }
}
