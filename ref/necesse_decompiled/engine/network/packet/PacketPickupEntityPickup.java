package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.entity.pickup.PickupEntity;

public class PacketPickupEntityPickup extends Packet {
   public final int pickupUniqueID;
   public final int targetSlot;
   public final Packet content;

   public PacketPickupEntityPickup(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.pickupUniqueID = var2.getNextInt();
      this.targetSlot = var2.getNextByteUnsigned();
      this.content = var2.getNextContentPacket();
   }

   public PacketPickupEntityPickup(PickupEntity var1, Packet var2) {
      this.pickupUniqueID = var1.getUniqueID();
      this.targetSlot = var1.getTarget().slot;
      this.content = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.pickupUniqueID);
      var3.putNextByteUnsigned(this.targetSlot);
      var3.putNextContentPacket(var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         ClientClient var3 = var2.getClient(this.targetSlot);
         if (var3 != null) {
            PickupEntity var4 = (PickupEntity)var2.getLevel().entityManager.pickups.get(this.pickupUniqueID, true);
            if (var4 != null) {
               var4.onPickup(var3, this.content);
               var4.refreshClientUpdateTime();
            } else {
               var2.network.sendPacket(new PacketRequestPickupEntity(this.pickupUniqueID));
            }
         } else {
            var2.network.sendPacket(new PacketRequestPlayerData(this.targetSlot));
         }

      }
   }
}
