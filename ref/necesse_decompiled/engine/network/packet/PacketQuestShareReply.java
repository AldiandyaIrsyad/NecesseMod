package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.quest.Quest;

public class PacketQuestShareReply extends Packet {
   public final int questUniqueID;
   public final boolean accepted;

   public PacketQuestShareReply(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.questUniqueID = var2.getNextInt();
      this.accepted = var2.getNextBoolean();
   }

   public PacketQuestShareReply(int var1, boolean var2) {
      this.questUniqueID = var1;
      this.accepted = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextBoolean(var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Quest var4 = var2.world.getQuests().getQuest(this.questUniqueID);
      if (var4 != null) {
         if (var3.questInvites.containsKey(this.questUniqueID)) {
            ServerClient var5 = (ServerClient)var3.questInvites.remove(this.questUniqueID);
            if (this.accepted) {
               var4.onShared(var2, var5, var3);
            }
         }
      } else {
         var3.sendPacket(new PacketQuestRemove(this.questUniqueID));
      }

   }
}
