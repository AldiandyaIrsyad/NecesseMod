package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemInteractAction;

public class PacketShowItemMobInteract extends Packet {
   public final int slot;
   public final float playerX;
   public final float playerY;
   public final int attackX;
   public final int attackY;
   public final int mobUniqueID;
   public final int seed;
   public final Packet itemContent;
   public final Packet interactContent;

   public PacketShowItemMobInteract(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.playerX = var2.getNextFloat();
      this.playerY = var2.getNextFloat();
      this.attackX = var2.getNextInt();
      this.attackY = var2.getNextInt();
      this.mobUniqueID = var2.getNextInt();
      this.seed = var2.getNextShortUnsigned();
      this.itemContent = var2.getNextContentPacket();
      this.interactContent = var2.getNextContentPacket();
   }

   public PacketShowItemMobInteract(PlayerMob var1, InventoryItem var2, int var3, int var4, Mob var5, int var6, Packet var7) {
      this.slot = var1.getPlayerSlot();
      this.playerX = var1.x;
      this.playerY = var1.y;
      this.attackX = var3;
      this.attackY = var4;
      this.mobUniqueID = var5.getUniqueID();
      this.seed = var6;
      this.itemContent = InventoryItem.getContentPacket(var2);
      this.interactContent = var7;
      PacketWriter var8 = new PacketWriter(this);
      var8.putNextByteUnsigned(this.slot);
      var8.putNextFloat(this.playerX);
      var8.putNextFloat(this.playerY);
      var8.putNextInt(this.attackX);
      var8.putNextInt(this.attackY);
      var8.putNextInt(this.mobUniqueID);
      var8.putNextShortUnsigned(this.seed);
      var8.putNextContentPacket(this.itemContent);
      var8.putNextContentPacket(var7);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      PlayerMob var3 = var2.getPlayer(this.slot);
      if (var3 != null && var3.getLevel() != null) {
         var3.setPos(this.playerX, this.playerY, false);
         Mob var4 = (Mob)var2.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
         InventoryItem var5 = InventoryItem.fromContentPacket(this.itemContent);
         if (var5 != null && var5.item instanceof ItemInteractAction) {
            var3.showItemMobInteract((ItemInteractAction)var5.item, var5, this.attackX, this.attackY, var4, this.seed, this.interactContent);
         }
      }

   }
}
