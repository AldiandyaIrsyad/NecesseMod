package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class PacketMobHealth extends Packet {
   public final int mobUniqueID;
   public final boolean isFull;
   private final PacketReader reader;

   public PacketMobHealth(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.isFull = var2.getNextBoolean();
      this.reader = var2;
   }

   public PacketMobHealth(Mob var1, boolean var2) {
      this.mobUniqueID = var1.getUniqueID();
      this.isFull = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.mobUniqueID);
      var3.putNextBoolean(var2);
      this.reader = new PacketReader(var3);
      var1.setupHealthPacket(var3, var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Mob var4 = GameUtils.getLevelMob(this.mobUniqueID, var3.getLevel());
            if (var4 != null) {
               var4.applyHealthPacket(new PacketReader(this.reader), this.isFull);
            }
         } else {
            System.out.println(var3.getName() + " tried to change mob health, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to change mob health, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 == null) {
            var2.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
         } else {
            var3.applyHealthPacket(new PacketReader(this.reader), this.isFull);
         }

      }
   }
}
