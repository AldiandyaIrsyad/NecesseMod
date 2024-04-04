package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobInventory;
import necesse.inventory.InventoryItem;

public class PacketMobInventoryUpdate extends Packet {
   public final int mobUniqueID;
   public final int inventorySlot;
   public final Packet itemContent;

   public PacketMobInventoryUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.mobUniqueID = var2.getNextInt();
      this.inventorySlot = var2.getNextShortUnsigned();
      this.itemContent = var2.getNextContentPacket();
   }

   public PacketMobInventoryUpdate(MobInventory var1, int var2) {
      this.mobUniqueID = ((Mob)var1).getUniqueID();
      this.inventorySlot = var2;
      this.itemContent = InventoryItem.getContentPacket(var1.getInventory().getItem(var2));
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.mobUniqueID);
      var3.putNextShortUnsigned(var2);
      var3.putNextContentPacket(this.itemContent);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         Mob var3 = GameUtils.getLevelMob(this.mobUniqueID, var2.getLevel());
         if (var3 instanceof MobInventory) {
            ((MobInventory)var3).getInventory().setItem(this.inventorySlot, InventoryItem.fromContentPacket(this.itemContent));
         }

      }
   }
}
