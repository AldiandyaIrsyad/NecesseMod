package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketPlayerCollisionHit extends Packet {
   public final int mobUniqueID;

   public PacketPlayerCollisionHit(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
   }

   public PacketPlayerCollisionHit(Mob var1) {
      this.mobUniqueID = var1.getUniqueID();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.mobUniqueID);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (Settings.giveClientsPower) {
         Level var4 = var3.getLevel();
         Mob var5 = GameUtils.getLevelMob(this.mobUniqueID, var4);
         if (var5 != null) {
            var5.handleCollisionHit(var3.playerMob);
         } else {
            GameLog.warn.println(var3.getName() + " tried to submit collision hit from unknown mob " + this.mobUniqueID);
            var3.sendPacket(new PacketRemoveMob(this.mobUniqueID));
         }
      } else {
         GameLog.warn.println(var3.getName() + " tried to submit collision hit while not allowed");
      }

   }
}
