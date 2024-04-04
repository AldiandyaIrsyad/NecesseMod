package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketRemoveMob extends Packet {
   public final int mobUniqueID;

   public PacketRemoveMob(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
   }

   public PacketRemoveMob(int var1) {
      this.mobUniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.mobUniqueID);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Level var4 = var2.world.getLevel(var3);
            Mob var5 = (Mob)var4.entityManager.mobs.get(this.mobUniqueID, false);
            if (var5 != null) {
               var5.remove();
            }
         } else {
            System.out.println(var3.getName() + " tried to remove a mob, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to remove a mob, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = (Mob)var2.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
         if (var3 != null) {
            var3.remove();
         }
      }

   }
}
