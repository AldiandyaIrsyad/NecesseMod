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
import necesse.entity.mobs.buffs.ActiveBuff;

public class PacketMobBuff extends Packet {
   public final int mobUniqueID;
   public final Packet buffContent;

   public PacketMobBuff(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.buffContent = var2.getNextContentPacket();
   }

   public PacketMobBuff(int var1, ActiveBuff var2) {
      this.mobUniqueID = var1;
      this.buffContent = var2.getContentPacket();
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.mobUniqueID);
      var3.putNextContentPacket(this.buffContent);
   }

   public ActiveBuff getBuff(Mob var1) {
      return ActiveBuff.fromContentPacket(this.buffContent, var1);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            if (!var3.checkHasRequestedSelf()) {
               return;
            }

            Mob var4 = GameUtils.getLevelMob(this.mobUniqueID, var2.world.getLevel(var3));
            if (var4 != null) {
               var4.buffManager.addBuff(this.getBuff(var4), true, true);
            }
         } else {
            System.out.println(var3.getName() + " tried to set mob buff, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to set mob buff, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 != null) {
            var3.buffManager.addBuff(this.getBuff(var3), false, true);
         }
      }

   }
}
