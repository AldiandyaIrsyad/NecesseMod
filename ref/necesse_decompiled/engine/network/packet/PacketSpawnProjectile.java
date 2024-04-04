package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class PacketSpawnProjectile extends Packet {
   public final int levelIdentifierHashCode;
   public final int projectileID;
   public final Packet spawnContent;

   public PacketSpawnProjectile(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.projectileID = var2.getNextShortUnsigned();
      this.spawnContent = var2.getNextContentPacket();
   }

   public PacketSpawnProjectile(Projectile var1) {
      this.levelIdentifierHashCode = var1.getLevel().getIdentifierHashCode();
      this.projectileID = var1.getID();
      this.spawnContent = new Packet();
      var1.setupSpawnPacket(new PacketWriter(this.spawnContent));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.levelIdentifierHashCode);
      var2.putNextShortUnsigned(this.projectileID);
      var2.putNextContentPacket(this.spawnContent);
   }

   public Projectile getProjectile(Level var1) {
      Projectile var2 = ProjectileRegistry.getProjectile(this.projectileID, var1);
      var2.applySpawnPacket(new PacketReader(this.spawnContent));
      return var2;
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Level var4 = var2.world.getLevel(var3);
            if (var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
               Projectile var5 = this.getProjectile(var4);
               var5.setOwner(var3.playerMob);
               var4.entityManager.projectiles.addHidden(var5);
               var2.network.sendToClientsAtExcept(new PacketSpawnProjectile(var5), (ServerClient)var3, var3);
            } else {
               System.out.println(var3.getName() + " tried to spawn a projectile on wrong level");
            }
         } else {
            System.out.println(var3.getName() + " tried to spawn a projectile, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to spawn a projectile, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
         Projectile var3 = this.getProjectile(var2.getLevel());
         if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, var3, true)) {
            var2.getLevel().entityManager.projectiles.addHidden(var3);
         }
      }
   }
}
