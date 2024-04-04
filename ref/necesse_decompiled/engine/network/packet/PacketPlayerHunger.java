package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerHunger extends Packet {
   public final int slot;
   public final float hunger;

   public PacketPlayerHunger(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.hunger = var2.getNextFloat();
   }

   public PacketPlayerHunger(int var1, float var2) {
      this.slot = var1;
      this.hunger = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextByteUnsigned(var1);
      var3.putNextFloat(var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var3.slot != this.slot) {
            return;
         }

         if (var2.world.settings.allowCheats) {
            var3.playerMob.hungerLevel = this.hunger;
         } else {
            System.out.println(var3.getName() + " tried to change hunger, but cheats aren't allowed");
            var2.network.sendPacket(new PacketPlayerHunger(var3.slot, var3.playerMob.hungerLevel), (ServerClient)var3);
         }
      } else {
         System.out.println(var3.getName() + " tried to change hunger, but isn't admin");
         var2.network.sendPacket(new PacketPlayerHunger(var3.slot, var3.playerMob.hungerLevel), (ServerClient)var3);
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      PlayerMob var3 = var2.getPlayer(this.slot);
      if (var3 != null) {
         var3.hungerLevel = this.hunger;
      }

   }
}
