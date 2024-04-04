package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.jumping.JumpingMobInterface;

public class PacketMountMobJump extends PacketMobJump {
   public PacketMountMobJump(byte[] var1) {
      super(var1);
   }

   public PacketMountMobJump(Mob var1, float var2, float var3) {
      super(var1, var2, var3);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      Mob var4 = GameUtils.getLevelMob(this.mobUniqueID, var3.getLevel());
      if (var4 instanceof JumpingMobInterface && var4.getRider() == var3.playerMob) {
         var4.updatePosFromServer(this.x, this.y, false);
         ((JumpingMobInterface)var4).runJump(this.dx, this.dy);
         Mob var5 = var4.getRider();
         if (var5 != null && !var5.isAttacking) {
            var5.dir = var4.dir;
         }

         var2.network.sendToAllClientsExcept(this, var3);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 instanceof JumpingMobInterface) {
            var3.setPos(this.x, this.y, false);
            ((JumpingMobInterface)var3).runJump(this.dx, this.dy);
            Mob var4 = var3.getRider();
            if (var4 != null && !var4.isAttacking) {
               var4.dir = var3.dir;
            }
         } else {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         }

      }
   }
}
