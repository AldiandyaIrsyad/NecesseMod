package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.Level;

public class PacketRequestLevelEvent extends Packet {
   public final int eventUniqueID;

   public PacketRequestLevelEvent(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.eventUniqueID = var2.getNextInt();
   }

   public PacketRequestLevelEvent(int var1) {
      this.eventUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.eventUniqueID);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var2.world.getLevel(var3);
      LevelEvent var5 = var4.entityManager.getLevelEvent(this.eventUniqueID, false);
      if (var5 != null) {
         var3.sendPacket(new PacketLevelEvent(var5));
      } else {
         var3.sendPacket(new PacketLevelEventOver(this.eventUniqueID));
      }

   }
}
