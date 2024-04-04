package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;

public class PacketHumanWorkUpdate extends Packet {
   public final int mobUniqueID;
   private final PacketReader reader;

   public PacketHumanWorkUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.reader = var2;
   }

   public PacketHumanWorkUpdate(HumanMob var1) {
      this.mobUniqueID = var1.getUniqueID();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.mobUniqueID);
      this.reader = new PacketReader(var2);
      var1.setupWorkPacket(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 instanceof HumanMob) {
            ((HumanMob)var3).applyWorkPacket(new PacketReader(this.reader));
            var3.refreshClientUpdateTime();
         } else {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         }

      }
   }
}
