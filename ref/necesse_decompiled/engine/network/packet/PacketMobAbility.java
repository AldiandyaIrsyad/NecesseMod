package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;

public class PacketMobAbility extends Packet {
   public final int mobUniqueID;
   public final int abilityID;
   public final Packet abilityContent;

   public PacketMobAbility(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.abilityID = var2.getNextShort();
      this.abilityContent = var2.getNextContentPacket();
   }

   public PacketMobAbility(Mob var1, int var2, Packet var3) {
      this.mobUniqueID = var1.getUniqueID();
      this.abilityID = var2;
      this.abilityContent = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextInt(this.mobUniqueID);
      var4.putNextShort((short)var2);
      var4.putNextContentPacket(this.abilityContent);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = (Mob)var2.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
         if (var3 != null) {
            var3.runAbility(this.abilityID, new PacketReader(this.abilityContent));
            var3.refreshClientUpdateTime();
         } else {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         }

      }
   }
}
