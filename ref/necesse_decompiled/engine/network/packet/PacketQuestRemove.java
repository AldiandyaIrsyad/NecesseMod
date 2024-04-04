package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.quest.Quest;

public class PacketQuestRemove extends Packet {
   public final int questUniqueID;

   public PacketQuestRemove(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.questUniqueID = var2.getNextInt();
   }

   public PacketQuestRemove(int var1) {
      this.questUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1);
   }

   public PacketQuestRemove(Quest var1) {
      this(var1.getUniqueID());
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.quests.removeQuest(this.questUniqueID);
   }
}
