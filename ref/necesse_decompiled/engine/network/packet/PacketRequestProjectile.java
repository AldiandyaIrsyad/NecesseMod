package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.projectile.Projectile;

public class PacketRequestProjectile extends Packet {
   public final int projectileUniqueID;

   public PacketRequestProjectile(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.projectileUniqueID = var2.getNextInt();
   }

   public PacketRequestProjectile(int var1) {
      this.projectileUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.projectileUniqueID);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Projectile var4 = (Projectile)var2.world.getLevel(var3).entityManager.projectiles.get(this.projectileUniqueID, false);
      if (var4 != null) {
         var2.network.sendPacket(new PacketSpawnProjectile(var4), (ServerClient)var3);
      } else {
         var2.network.sendPacket(new PacketRemoveProjectile(this.projectileUniqueID), (ServerClient)var3);
      }

   }
}
