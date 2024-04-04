package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketRefreshCombat extends Packet {
   public final int mobUniqueID;
   public final long lastCombatTime;

   public PacketRefreshCombat(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.lastCombatTime = var2.getNextLong();
   }

   public PacketRefreshCombat(Mob var1) {
      this.mobUniqueID = var1.getUniqueID();
      this.lastCombatTime = var1.lastCombatTime;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.mobUniqueID);
      var2.putNextLong(this.lastCombatTime);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 == null) {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         } else {
            var3.lastCombatTime = this.lastCombatTime;
            var3.refreshClientUpdateTime();
         }

      }
   }
}
