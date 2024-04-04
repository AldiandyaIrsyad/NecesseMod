package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.levelEvent.LevelEvent;

public class PacketLevelEventOver extends Packet {
   public final int eventUniqueID;

   public PacketLevelEventOver(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.eventUniqueID = var2.getNextInt();
   }

   public PacketLevelEventOver(int var1) {
      this.eventUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.eventUniqueID);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         LevelEvent var3 = var2.getLevel().entityManager.getLevelEvent(this.eventUniqueID, false);
         if (var3 != null) {
            var3.over();
         }
      }

   }
}
