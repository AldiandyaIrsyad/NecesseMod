package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;

public class PacketSpawnItem extends Packet {
   public final boolean inHand;
   public final Packet itemContent;

   public PacketSpawnItem(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.inHand = var2.getNextBoolean();
      this.itemContent = var2.getNextContentPacket();
   }

   public PacketSpawnItem(InventoryItem var1, boolean var2) {
      this.inHand = var2;
      this.itemContent = InventoryItem.getContentPacket(var1);
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextBoolean(var2);
      var3.putNextContentPacket(this.itemContent);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            InventoryItem var4 = InventoryItem.fromContentPacket(this.itemContent);
            int var5 = var4.getAmount();
            if (this.inHand) {
               if (var3.playerMob.getDraggingItem() == null) {
                  var3.playerMob.setDraggingItem(var4);
                  System.out.println(var3.getName() + " spawned an item: " + var4.getItemDisplayName() + " (" + var4.getAmount() + ")");
               } else if (!var3.playerMob.getDraggingItem().combine(var3.playerMob.getLevel(), var3.playerMob, var3.playerMob.getInv().drag, 0, var4, "spawnitem", (InventoryAddConsumer)null).success) {
                  var3.playerMob.setDraggingItem((InventoryItem)null);
               }
            } else {
               var3.playerMob.getInv().addItem(var4, true, "give", (InventoryAddConsumer)null);
               if (var4.getAmount() != var5) {
                  System.out.println(var3.getName() + " spawned an item: " + var4.getItemDisplayName() + " (" + Math.abs(var4.getAmount() - var5) + ")");
               }
            }
         } else {
            System.out.println(var3.getName() + " tried to spawn an item, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to spawn an item, but isn't admin");
      }

   }
}
