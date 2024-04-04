package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerInventory extends Packet {
   public final int slot;
   public final Packet inventoryContent;

   public PacketPlayerInventory(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.inventoryContent = var2.getNextContentPacket();
   }

   public PacketPlayerInventory(ServerClient var1) {
      this.slot = var1.slot;
      this.inventoryContent = new Packet();
      var1.playerMob.getInv().setupContentPacket(new PacketWriter(this.inventoryContent));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextByteUnsigned(this.slot);
      var2.putNextContentPacket(this.inventoryContent);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            if (this.slot == var3.slot && var3.checkHasRequestedSelf()) {
               var3.playerMob.applyInventoryPacket(this);
               var2.network.sendToAllClientsExcept(this, var3);
            }
         }
      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 == null) {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      } else {
         var3.applyInventoryPacket(this);
      }

   }
}
