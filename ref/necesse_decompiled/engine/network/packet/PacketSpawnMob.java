package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketSpawnMob extends Packet {
   public final int levelIdentifierHashCode;
   public final int mobID;
   public final Packet spawnContent;

   public PacketSpawnMob(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.mobID = var2.getNextShortUnsigned();
      this.spawnContent = var2.getNextContentPacket();
   }

   public PacketSpawnMob(Mob var1) {
      this.levelIdentifierHashCode = var1.getLevel().getIdentifierHashCode();
      this.mobID = var1.getID();
      this.spawnContent = new Packet();
      var1.setupSpawnPacket(new PacketWriter(this.spawnContent));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.levelIdentifierHashCode);
      var2.putNextShortUnsigned(this.mobID);
      var2.putNextContentPacket(this.spawnContent);
   }

   public Mob getMob(Level var1) {
      Mob var2 = MobRegistry.getMob(this.mobID, var1);
      if (var2 != null) {
         var2.applySpawnPacket(new PacketReader(this.spawnContent));
      }

      return var2;
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Level var4 = var2.world.getLevel(var3);
            if (var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
               var4.entityManager.mobs.add(this.getMob(var4));
            } else {
               System.out.println(var3.getName() + " tried to spawn a mob on wrong level");
            }
         } else {
            System.out.println(var3.getName() + " tried to spawn a mob, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to spawn a mob, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
         Mob var3 = this.getMob(var2.getLevel());
         if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, var3, true)) {
            var2.getLevel().entityManager.mobs.add(var3);
         }
      }
   }
}
