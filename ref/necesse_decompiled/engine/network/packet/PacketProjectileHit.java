package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class PacketProjectileHit extends Packet {
   public final int projectileUniqueID;
   public final int mobUniqueID;
   public final float fromX;
   public final float fromY;

   public PacketProjectileHit(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.projectileUniqueID = var2.getNextInt();
      this.mobUniqueID = var2.getNextInt();
      this.fromX = var2.getNextFloat();
      this.fromY = var2.getNextFloat();
   }

   public PacketProjectileHit(Projectile var1, float var2, float var3, Mob var4) {
      this.projectileUniqueID = var1.getUniqueID();
      this.mobUniqueID = var4.getUniqueID();
      this.fromX = var2;
      this.fromY = var3;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextInt(this.projectileUniqueID);
      var5.putNextInt(this.mobUniqueID);
      var5.putNextFloat(var2);
      var5.putNextFloat(var3);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Projectile var3 = (Projectile)var2.getLevel().entityManager.projectiles.get(this.projectileUniqueID, true);
         if (var3 != null) {
            if (this.mobUniqueID == -1) {
               var3.onHit((Mob)null, (LevelObjectHit)null, this.fromX, this.fromY, true, (ServerClient)null);
            } else {
               Mob var4 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
               if (var4 != null) {
                  var3.onHit(var4, (LevelObjectHit)null, this.fromX, this.fromY, true, (ServerClient)null);
               }
            }
         }
      }

   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Level var4 = var3.getLevel();
      Projectile var5 = (Projectile)var4.entityManager.projectiles.get(this.projectileUniqueID, true);
      Mob var6 = GameUtils.getLevelMob(this.mobUniqueID, var4);
      if (var5 != null && var6 != null) {
         this.serverHit(var3, var5, var6);
      } else {
         var4.entityManager.submittedHits.submitProjectileHit(var3, this.projectileUniqueID, this.mobUniqueID, this::serverHit, (var0, var1x, var2x, var3x, var4x) -> {
            if (var2x == null) {
               var0.sendPacket(new PacketRemoveProjectile(var1x));
            }

            if (var4x == null) {
               var0.sendPacket(new PacketRemoveMob(var3x));
            }

         });
      }

   }

   private void serverHit(ServerClient var1, Projectile var2, Mob var3) {
      if (var3 == var1.playerMob || var2.handlingClient == var1 && !var3.isPlayer) {
         var2.onHit(var3, (LevelObjectHit)null, this.fromX, this.fromY, true, var1);
      }

   }
}
