package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.PickupRegistry;
import necesse.entity.pickup.PickupEntity;
import necesse.level.maps.Level;

public class PacketSpawnPickupEntity extends Packet {
   public final int levelIdentifierHashCode;
   public final int pickupID;
   public final Packet spawnContent;

   public PacketSpawnPickupEntity(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.pickupID = var2.getNextShortUnsigned();
      this.spawnContent = var2.getNextContentPacket();
   }

   public PacketSpawnPickupEntity(PickupEntity var1) {
      this.levelIdentifierHashCode = var1.getLevel().getIdentifierHashCode();
      this.pickupID = var1.getID();
      this.spawnContent = new Packet();
      var1.setupSpawnPacket(new PacketWriter(this.spawnContent));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.levelIdentifierHashCode);
      var2.putNextShortUnsigned(this.pickupID);
      var2.putNextContentPacket(this.spawnContent);
   }

   public PickupEntity getPickupEntity(Level var1) {
      PickupEntity var2 = PickupRegistry.getPickup(this.pickupID);
      if (var2 != null) {
         var2.setLevel(var1);
         var2.applySpawnPacket(new PacketReader(this.spawnContent));
      }

      return var2;
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Level var4 = var2.world.getLevel(var3);
            if (var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
               var4.entityManager.pickups.add(this.getPickupEntity(var4));
            } else {
               System.out.println(var3.getName() + " tried to spawn pickup entity on wrong level");
            }
         } else {
            System.out.println(var3.getName() + " tried to spawn pickup entity, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to spawn pickup entity, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
         PickupEntity var3 = this.getPickupEntity(var2.getLevel());
         if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, var3, true)) {
            var2.getLevel().entityManager.pickups.add(var3);
         }
      }
   }
}
