package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.levelEvent.LevelEvent;

public class PacketLevelEventAction extends Packet {
   public final int eventUniqueID;
   public final int actionID;
   public final Packet actionContent;

   public PacketLevelEventAction(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.eventUniqueID = var2.getNextInt();
      this.actionID = var2.getNextShort();
      this.actionContent = var2.getNextContentPacket();
   }

   public PacketLevelEventAction(LevelEvent var1, int var2, Packet var3) {
      this.eventUniqueID = var1.getUniqueID();
      this.actionID = var2;
      this.actionContent = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.eventUniqueID);
      var4.putNextShort((short)var2);
      var4.putNextContentPacket(this.actionContent);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         LevelEvent var3 = var2.getLevel().entityManager.getLevelEvent(this.eventUniqueID, false);
         if (var3 != null) {
            var3.runAction(this.actionID, new PacketReader(this.actionContent));
         } else {
            var2.network.sendPacket(new PacketRequestLevelEvent(this.eventUniqueID));
         }

      }
   }
}
