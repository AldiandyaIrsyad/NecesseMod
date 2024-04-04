package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class PacketOEInventoryUpdate extends Packet {
   public final int x;
   public final int y;
   public final int inventorySlot;
   public final Packet itemContent;

   public PacketOEInventoryUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.x = var2.getNextShortUnsigned();
      this.y = var2.getNextShortUnsigned();
      this.inventorySlot = var2.getNextShortUnsigned();
      this.itemContent = var2.getNextContentPacket();
   }

   public PacketOEInventoryUpdate(OEInventory var1, int var2) {
      this.x = ((ObjectEntity)var1).getX();
      this.y = ((ObjectEntity)var1).getY();
      this.inventorySlot = var2;
      this.itemContent = InventoryItem.getContentPacket(var1.getInventory().getItem(var2));
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextShortUnsigned(this.x);
      var3.putNextShortUnsigned(this.y);
      var3.putNextShortUnsigned(var2);
      var3.putNextContentPacket(this.itemContent);
   }

   public ObjectEntity getObjectEntity(Level var1) {
      return var1.entityManager.getObjectEntity(this.x, this.y);
   }

   public OEInventory getOEInventory(Level var1) {
      ObjectEntity var2 = this.getObjectEntity(var1);
      return var2 instanceof OEInventory ? (OEInventory)var2 : null;
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         OEInventory var3 = this.getOEInventory(var2.getLevel());
         if (var3 != null) {
            var3.getInventory().setItem(this.inventorySlot, InventoryItem.fromContentPacket(this.itemContent));
         }

      }
   }
}
