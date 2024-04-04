package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.quest.Quest;

public class PacketQuestUpdate extends Packet {
   public final int questUniqueID;
   public final Packet questContent;

   public PacketQuestUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.questUniqueID = var2.getNextInt();
      this.questContent = var2.getNextContentPacket();
   }

   public PacketQuestUpdate(Quest var1) {
      this.questUniqueID = var1.getUniqueID();
      this.questContent = new Packet();
      var1.setupPacket(new PacketWriter(this.questContent));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.questUniqueID);
      var2.putNextContentPacket(this.questContent);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      Quest var3 = var2.quests.getQuest(this.questUniqueID);
      if (var3 != null) {
         var3.applyPacket(new PacketReader(this.questContent));
      } else {
         var2.network.sendPacket(new PacketQuestRequest(this.questUniqueID));
      }

   }
}
