package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.level.maps.Level;

public class PacketProjectileTargetUpdate extends Packet {
   public final int projectileUniqueID;
   public final float x;
   public final float y;
   public final float dx;
   public final float dy;
   public final float travelledDistance;
   public final int distance;
   public final Packet content;

   public PacketProjectileTargetUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.projectileUniqueID = var2.getNextInt();
      this.x = var2.getNextFloat();
      this.y = var2.getNextFloat();
      this.dx = var2.getNextFloat();
      this.dy = var2.getNextFloat();
      this.travelledDistance = var2.getNextFloat();
      this.distance = var2.getNextInt();
      this.content = var2.getNextContentPacket();
   }

   public PacketProjectileTargetUpdate(FollowingProjectile var1) {
      this.projectileUniqueID = var1.getUniqueID();
      this.x = var1.x;
      this.y = var1.y;
      this.dx = var1.dx;
      this.dy = var1.dy;
      this.travelledDistance = var1.traveledDistance;
      this.distance = var1.distance;
      this.content = new Packet();
      var1.addTargetData(new PacketWriter(this.content));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.projectileUniqueID);
      var2.putNextFloat(this.x);
      var2.putNextFloat(this.y);
      var2.putNextFloat(this.dx);
      var2.putNextFloat(this.dy);
      var2.putNextFloat(this.travelledDistance);
      var2.putNextInt(this.distance);
      var2.putNextContentPacket(this.content);
   }

   public Projectile getProjectile(Level var1) {
      return var1 == null ? null : (Projectile)var1.entityManager.projectiles.get(this.projectileUniqueID, false);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Projectile var4 = this.getProjectile(var2.world.getLevel(var3));
      if (var4 instanceof FollowingProjectile && var4.handlingClient == var3) {
         var4.changePosition(this.x, this.y);
         var4.dx = this.dx;
         var4.dy = this.dy;
         var4.traveledDistance = this.travelledDistance;
         var4.setDistance(this.distance);
         var4.updateAngle();
         ((FollowingProjectile)var4).applyTargetData(new PacketReader(this.content));
         var2.network.sendToClientsAtExcept(this, (ServerClient)var3, var3);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      Projectile var3 = this.getProjectile(var2.getLevel());
      if (var3 instanceof FollowingProjectile) {
         var3.changePosition(this.x, this.y);
         var3.dx = this.dx;
         var3.dy = this.dy;
         var3.traveledDistance = this.travelledDistance;
         var3.setDistance(this.distance);
         var3.updateAngle();
         ((FollowingProjectile)var3).applyTargetData(new PacketReader(this.content));
      } else {
         var2.network.sendPacket(new PacketRequestProjectile(this.projectileUniqueID));
      }

   }
}
