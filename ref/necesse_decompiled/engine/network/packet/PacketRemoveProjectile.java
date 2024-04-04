package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.projectile.Projectile;

public class PacketRemoveProjectile extends Packet {
   public final int projectileUniqueID;

   public PacketRemoveProjectile(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.projectileUniqueID = var2.getNextInt();
   }

   public PacketRemoveProjectile(int var1) {
      this.projectileUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.projectileUniqueID);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Projectile var3 = (Projectile)var2.getLevel().entityManager.projectiles.get(this.projectileUniqueID, false);
         if (var3 != null) {
            var3.remove();
         }
      }

   }
}
