package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketMobFollowUpdate extends Packet {
   public final int mobUniqueID;
   public final int followingSlot;

   public PacketMobFollowUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      int var3 = var2.getNextByteUnsigned();
      if (var3 > 250) {
         var3 = (byte)var3;
      }

      this.followingSlot = var3;
   }

   public PacketMobFollowUpdate(int var1, int var2) {
      this.mobUniqueID = var1;
      this.followingSlot = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(var1);
      var3.putNextByteUnsigned(var2);
   }

   public Mob getMob(Level var1) {
      return (Mob)var1.entityManager.mobs.get(this.mobUniqueID, false);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = this.getMob(var2.getLevel());
         if (var3 != null) {
            var3.applyFollowUpdatePacket(this);
         } else {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         }

      }
   }
}
