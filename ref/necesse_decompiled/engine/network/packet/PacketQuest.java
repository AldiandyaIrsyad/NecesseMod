package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.quest.Quest;
import necesse.engine.registries.QuestRegistry;

public class PacketQuest extends Packet {
   public final int questID;
   public final boolean isNew;
   public final Quest quest;

   public PacketQuest(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.questID = var2.getNextShortUnsigned();
      this.isNew = var2.getNextBoolean();
      this.quest = QuestRegistry.getNewQuest(this.questID);
      if (this.quest != null) {
         this.quest.applySpawnPacket(var2);
      }

   }

   public PacketQuest(Quest var1, boolean var2) {
      this.questID = var1.getID();
      this.isNew = var2;
      this.quest = var1;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextShortUnsigned(this.questID);
      var3.putNextBoolean(var2);
      var1.setupSpawnPacket(var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.quests.addQuest(this.quest, this.isNew);
   }
}
