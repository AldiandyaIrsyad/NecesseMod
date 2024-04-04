package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class PacketProjectilePositionUpdate extends Packet {
   public final int projectileUniqueID;
   public final Packet positionContent;

   public PacketProjectilePositionUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.projectileUniqueID = var2.getNextInt();
      this.positionContent = var2.getNextContentPacket();
   }

   public PacketProjectilePositionUpdate(Projectile var1) {
      this.projectileUniqueID = var1.getUniqueID();
      this.positionContent = new Packet();
      var1.setupPositionPacket(new PacketWriter(this.positionContent));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.projectileUniqueID);
      var2.putNextContentPacket(this.positionContent);
   }

   public Projectile getProjectile(Level var1) {
      return var1 == null ? null : (Projectile)var1.entityManager.projectiles.get(this.projectileUniqueID, false);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Projectile var4 = this.getProjectile(var2.world.getLevel(var3));
      if (var4 != null && var4.handlingClient == var3) {
         var4.applyPositionPacket(new PacketReader(this.positionContent));
         var2.network.sendToClientsAtExcept(this, (ServerClient)var3, var3);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      Projectile var3 = this.getProjectile(var2.getLevel());
      if (var3 != null) {
         var3.applyPositionPacket(new PacketReader(this.positionContent));
      } else {
         var2.network.sendPacket(new PacketRequestProjectile(this.projectileUniqueID));
      }

   }
}
