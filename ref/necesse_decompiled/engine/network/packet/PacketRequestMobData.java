package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public class PacketRequestMobData extends Packet {
   public final int uniqueID;

   public PacketRequestMobData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.uniqueID = var2.getNextInt();
   }

   public PacketRequestMobData(int var1) {
      this.uniqueID = var1;
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (this.uniqueID >= 0 && this.uniqueID < var2.getSlots()) {
         ServerClient var6 = var2.getClient(this.uniqueID);
         if (var6 != null) {
            var2.network.sendPacket(new PacketPlayerGeneral(var6), (ServerClient)var3);
         }
      } else {
         Mob var4 = (Mob)var2.world.getLevel(var3).entityManager.mobs.get(this.uniqueID, false);
         if (var4 != null) {
            if (var4.shouldSendSpawnPacket()) {
               var2.network.sendPacket(new PacketSpawnMob(var4), (ServerClient)var3);
            } else {
               Mob var5 = var4.getSpawnPacketMaster();
               if (var5 != null) {
                  var2.network.sendPacket(new PacketSpawnMob(var5), (ServerClient)var3);
               }
            }
         } else {
            var2.network.sendPacket(new PacketRemoveMob(this.uniqueID), (ServerClient)var3);
         }
      }

   }
}
