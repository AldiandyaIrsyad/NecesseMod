package necesse.engine.network.packet;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;

public class PacketQuestShare extends Packet {
   public final int questUniqueID;

   public PacketQuestShare(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.questUniqueID = var2.getNextInt();
   }

   public PacketQuestShare(int var1) {
      this.questUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Quest var4 = (Quest)var3.quests.stream().filter((var1x) -> {
         return var1x.getUniqueID() == this.questUniqueID;
      }).findFirst().orElse((Object)null);
      if (var4 != null) {
         AtomicInteger var5 = new AtomicInteger();
         if (var4.canShare()) {
            var2.streamClients().filter((var2x) -> {
               return var3 != var2x && !var2x.quests.contains(var4) && var4.canShareWith(var3, var2x);
            }).forEach((var3x) -> {
               var3x.questInvites.put(var4.getUniqueID(), var3);
               var3x.sendPacket(new PacketQuestShareReceive(var3, var4));
               var5.incrementAndGet();
            });
         }

         if (var5.get() == 1) {
            var3.sendChatMessage((GameMessage)(new LocalMessage("quests", "shareresultsingle", new Object[]{"quest", var4.getTitle(), "count", var5.get()})));
         } else {
            var3.sendChatMessage((GameMessage)(new LocalMessage("quests", "shareresultplural", new Object[]{"quest", var4.getTitle(), "count", var5.get()})));
         }
      } else {
         var3.sendPacket(new PacketQuestRemove(this.questUniqueID));
      }

   }
}
