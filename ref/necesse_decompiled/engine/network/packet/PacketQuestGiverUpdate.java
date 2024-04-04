package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.QuestGiver;

public class PacketQuestGiverUpdate extends Packet {
   public final int mobUniqueID;
   public final int[] questUniqueIDs;

   public PacketQuestGiverUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.questUniqueIDs = new int[var2.getNextShortUnsigned()];

      for(int var3 = 0; var3 < this.questUniqueIDs.length; ++var3) {
         this.questUniqueIDs[var3] = var2.getNextInt();
      }

   }

   public PacketQuestGiverUpdate(int var1, int[] var2) {
      this.mobUniqueID = var1;
      this.questUniqueIDs = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextShortUnsigned(var2.length);
      int[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var4[var6];
         var3.putNextInt(var7);
      }

   }

   public PacketQuestGiverUpdate(QuestGiver var1, ServerClient var2) {
      this(((Mob)var1).getUniqueID(), var1.getQuestGiverObject().getRequestedQuests(var2).stream().mapToInt((var0) -> {
         return var0.questUniqueID;
      }).toArray());
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 instanceof QuestGiver) {
            ((QuestGiver)var3).applyQuestGiverUpdatePacket(this, var2);
         }

      }
   }
}
