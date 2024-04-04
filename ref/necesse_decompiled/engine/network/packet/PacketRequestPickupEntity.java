package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.pickup.PickupEntity;

public class PacketRequestPickupEntity extends Packet {
   public final int pickupUniqueID;

   public PacketRequestPickupEntity(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.pickupUniqueID = var2.getNextInt();
   }

   public PacketRequestPickupEntity(int var1) {
      this.pickupUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.pickupUniqueID);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      PickupEntity var4 = (PickupEntity)var2.world.getLevel(var3).entityManager.pickups.get(this.pickupUniqueID, false);
      if (var4 != null) {
         var2.network.sendPacket(new PacketSpawnPickupEntity(var4), (ServerClient)var3);
      } else {
         var2.network.sendPacket(new PacketRemovePickupEntity(this.pickupUniqueID), (ServerClient)var3);
      }

   }
}
