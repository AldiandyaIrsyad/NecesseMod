package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.QuestGiver;

public class PacketQuestGiverRequest extends Packet {
   public final int mobUniqueID;

   public PacketQuestGiverRequest(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
   }

   public PacketQuestGiverRequest(int var1) {
      this.mobUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Mob var4 = (Mob)var2.world.getLevel(var3).entityManager.mobs.get(this.mobUniqueID, false);
      if (var4 != null) {
         if (var4 instanceof QuestGiver) {
            var3.sendPacket(new PacketQuestGiverUpdate((QuestGiver)var4, var3));
         }
      } else {
         var2.network.sendPacket(new PacketRemoveMob(this.mobUniqueID), (ServerClient)var3);
      }

   }
}
