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
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class PacketPlayerInventorySlot extends Packet {
   public final int slot;
   public final PlayerInventorySlot inventorySlot;
   public final Packet itemContent;

   public PacketPlayerInventorySlot(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      int var3 = var2.getNextShortUnsigned();
      int var4 = var2.getNextShortUnsigned();
      this.inventorySlot = new PlayerInventorySlot(var3, var4);
      this.itemContent = var2.getNextContentPacket();
   }

   public PacketPlayerInventorySlot(ServerClient var1, PlayerInventorySlot var2) {
      this(var1.slot, var1.playerMob, var2);
   }

   public PacketPlayerInventorySlot(int var1, PlayerMob var2, PlayerInventorySlot var3) {
      this.slot = var1;
      this.inventorySlot = var3;
      this.itemContent = InventoryItem.getContentPacket(var2.getInv().getItem(var3));
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextByteUnsigned(var1);
      var4.putNextShortUnsigned(this.inventorySlot.inventoryID);
      var4.putNextShortUnsigned(this.inventorySlot.slot);
      var4.putNextContentPacket(this.itemContent);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            if (this.slot == var3.slot && var3.checkHasRequestedSelf()) {
               var3.playerMob.getInv().setItem(this.inventorySlot, InventoryItem.fromContentPacket(this.itemContent));
            }
         }
      }
   }

   public void processClient(NetworkPacket var1, Client var2) {
      ClientClient var3 = var2.getClient(this.slot);
      if (var3 == null) {
         var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
      } else {
         var3.playerMob.getInv().setItem(this.inventorySlot, InventoryItem.fromContentPacket(this.itemContent), false);
      }

   }
}
