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
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class PacketPlayerBuff extends Packet {
   public final int slot;
   public final Packet buffContent;

   public PacketPlayerBuff(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.buffContent = var2.getNextContentPacket();
   }

   public PacketPlayerBuff(int var1, ActiveBuff var2) {
      this.slot = var1;
      this.buffContent = var2.getContentPacket();
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextContentPacket(this.buffContent);
   }

   public ActiveBuff getBuff(Mob var1) {
      return ActiveBuff.fromContentPacket(this.buffContent, var1);
   }

   public void applyBuff(PlayerMob var1) {
      var1.addBuff(this.getBuff(var1), false);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (this.slot != var3.slot) {
            return;
         }

         if (var2.world.settings.allowCheats) {
            this.applyBuff(var3.playerMob);
            var2.network.sendToClientsAt(this, (ServerClient)var3);
         } else {
            System.out.println(var3.getName() + " tried to set own buff, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to set own buff, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getClient(this.slot) == null) {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      } else {
         this.applyBuff(var2.getClient(this.slot).playerMob);
      }

   }
}
