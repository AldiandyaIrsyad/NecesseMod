package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.pickup.PickupEntity;

public class PacketRemovePickupEntity extends Packet {
   public final int pickupUniqueID;

   public PacketRemovePickupEntity(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.pickupUniqueID = var2.getNextInt();
   }

   public PacketRemovePickupEntity(int var1) {
      this.pickupUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.pickupUniqueID);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         PickupEntity var3 = (PickupEntity)var2.getLevel().entityManager.pickups.get(this.pickupUniqueID, false);
         if (var3 != null) {
            var3.remove();
         }
      }

   }
}
