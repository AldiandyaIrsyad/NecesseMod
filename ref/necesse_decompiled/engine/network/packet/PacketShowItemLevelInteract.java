package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;

public class PacketShowItemLevelInteract extends Packet {
   public final int slot;
   public final float playerX;
   public final float playerY;
   public final int attackX;
   public final int attackY;
   public final int seed;
   public final Packet itemContent;
   public final Packet interactContent;

   public PacketShowItemLevelInteract(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.playerX = var2.getNextFloat();
      this.playerY = var2.getNextFloat();
      this.attackX = var2.getNextInt();
      this.attackY = var2.getNextInt();
      this.seed = var2.getNextShortUnsigned();
      this.itemContent = var2.getNextContentPacket();
      this.interactContent = var2.getNextContentPacket();
   }

   public PacketShowItemLevelInteract(PlayerMob var1, InventoryItem var2, int var3, int var4, int var5, Packet var6) {
      this.slot = var1.getPlayerSlot();
      this.playerX = var1.x;
      this.playerY = var1.y;
      this.attackX = var3;
      this.attackY = var4;
      this.seed = var5;
      this.itemContent = InventoryItem.getContentPacket(var2);
      this.interactContent = var6;
      PacketWriter var7 = new PacketWriter(this);
      var7.putNextByteUnsigned(this.slot);
      var7.putNextFloat(this.playerX);
      var7.putNextFloat(this.playerY);
      var7.putNextInt(this.attackX);
      var7.putNextInt(this.attackY);
      var7.putNextShortUnsigned(this.seed);
      var7.putNextContentPacket(this.itemContent);
      var7.putNextContentPacket(var6);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      PlayerMob var3 = var2.getPlayer(this.slot);
      if (var3 != null && var3.getLevel() != null) {
         var3.setPos(this.playerX, this.playerY, false);
         InventoryItem var4 = InventoryItem.fromContentPacket(this.itemContent);
         if (var4 != null && var4.item instanceof ItemInteractAction) {
            var3.showItemLevelInteract((ItemInteractAction)var4.item, var4, this.attackX, this.attackY, this.seed, this.interactContent);
         }
      }

   }
}
