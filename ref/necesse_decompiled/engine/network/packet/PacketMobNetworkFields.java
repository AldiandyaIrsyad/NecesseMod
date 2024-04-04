package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;

public class PacketMobNetworkFields extends Packet {
   public final int mobUniqueID;
   public final Packet content;

   public PacketMobNetworkFields(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.content = var2.getNextContentPacket();
   }

   public PacketMobNetworkFields(Mob var1, Packet var2) {
      this.mobUniqueID = var1.getUniqueID();
      this.content = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.mobUniqueID);
      var3.putNextContentPacket(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = (Mob)var2.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
         if (var3 != null) {
            var3.runNetworkFieldUpdate(new PacketReader(this.content));
            var3.refreshClientUpdateTime();
         } else {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         }

      }
   }
}
