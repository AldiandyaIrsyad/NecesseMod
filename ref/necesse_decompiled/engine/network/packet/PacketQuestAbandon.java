package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;

public class PacketQuestAbandon extends Packet {
   public final int questUniqueID;

   public PacketQuestAbandon(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.questUniqueID = var2.getNextInt();
   }

   public PacketQuestAbandon(int var1) {
      this.questUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Quest var4 = (Quest)var3.quests.stream().filter((var1x) -> {
         return var1x.getUniqueID() == this.questUniqueID;
      }).findFirst().orElse((Object)null);
      if (var4 != null) {
         var4.abandonFor(var2, var3);
      } else {
         var3.sendPacket(new PacketQuestRemove(this.questUniqueID));
      }

   }
}
