package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.pickup.PickupEntity;

public class PacketPickupEntityTarget extends Packet {
   public final int pickupUniqueID;
   public final Packet content;

   public PacketPickupEntityTarget(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.pickupUniqueID = var2.getNextInt();
      this.content = var2.getNextContentPacket();
   }

   public PacketPickupEntityTarget(PickupEntity var1) {
      this.pickupUniqueID = var1.getUniqueID();
      this.content = new Packet();
      var1.writeTargetUpdatePacket(new PacketWriter(this.content), true);
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.pickupUniqueID);
      var2.putNextContentPacket(this.content);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         PickupEntity var3 = (PickupEntity)var2.getLevel().entityManager.pickups.get(this.pickupUniqueID, false);
         if (var3 != null) {
            var3.readTargetUpdatePacket(new PacketReader(this.content), true);
         }

      }
   }
}
