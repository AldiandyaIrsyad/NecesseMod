package necesse.engine.network.packet;

import java.util.Iterator;
import java.util.function.Consumer;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;
import necesse.engine.registries.QuestRegistry;

public class PacketQuests extends Packet {
   public PacketQuests(byte[] var1) {
      super(var1);
   }

   public PacketQuests(ServerClient var1) {
      PacketWriter var2 = new PacketWriter(this);
      Iterator var3 = var1.quests.iterator();

      while(var3.hasNext()) {
         Quest var4 = (Quest)var3.next();
         var2.putNextShortUnsigned(var4.getID());
         var4.setupSpawnPacket(var2);
      }

   }

   public void readQuests(Consumer<Quest> var1) {
      PacketReader var2 = new PacketReader(this);

      while(var2.hasNext()) {
         int var3 = var2.getNextShortUnsigned();
         Quest var4 = QuestRegistry.getNewQuest(var3);
         var4.applySpawnPacket(var2);
         var1.accept(var4);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      var2.loading.questsPhase.submitQuestsPacket(this);
   }
}
