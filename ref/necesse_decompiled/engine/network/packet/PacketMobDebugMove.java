package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketMobDebugMove extends Packet {
   public final int mobUniqueID;
   public final int x;
   public final int y;
   public final int dir;

   public PacketMobDebugMove(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.x = var2.getNextInt();
      this.y = var2.getNextInt();
      this.dir = var2.getNextInt();
   }

   public PacketMobDebugMove(Mob var1, int var2, int var3, int var4) {
      this.mobUniqueID = var1.getUniqueID();
      this.x = var2;
      this.y = var3;
      this.dir = var4;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextInt(this.mobUniqueID);
      var5.putNextInt(var2);
      var5.putNextInt(var3);
      var5.putNextInt(var4);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Mob var4 = GameUtils.getLevelMob(this.mobUniqueID, var3.getLevel());
            if (var4 != null) {
               var4.setPos((float)this.x, (float)this.y, true);
               var4.dir = this.dir;
               var4.sendMovementPacket(true);
            } else {
               System.out.println(var3.getName() + " tried to move mob, but couldn't find mob with uniqueID: " + this.mobUniqueID);
            }
         } else {
            System.out.println(var3.getName() + " tried to move a mob, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to move a mob, but isn't admin");
      }

   }
}
