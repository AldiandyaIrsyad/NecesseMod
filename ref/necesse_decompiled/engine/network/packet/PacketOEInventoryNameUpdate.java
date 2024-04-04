package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.level.maps.Level;

public class PacketOEInventoryNameUpdate extends Packet {
   public final int levelIdentifierHashCode;
   public final int tileX;
   public final int tileY;
   public final String name;

   public PacketOEInventoryNameUpdate(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      this.name = var2.getNextString();
   }

   public PacketOEInventoryNameUpdate(OEInventory var1, String var2) {
      this.levelIdentifierHashCode = ((ObjectEntity)var1).getLevel().getIdentifierHashCode();
      this.tileX = ((ObjectEntity)var1).getTileX();
      this.tileY = ((ObjectEntity)var1).getTileY();
      this.name = var2;
      PacketWriter var3 = new PacketWriter(this);
      var3.putNextInt(this.levelIdentifierHashCode);
      var3.putNextShortUnsigned(this.tileX);
      var3.putNextShortUnsigned(this.tileY);
      var3.putNextString(var2);
   }

   public ObjectEntity getObjectEntity(Level var1) {
      return var1.entityManager.getObjectEntity(this.tileX, this.tileY);
   }

   public OEInventory getOEInventory(Level var1) {
      ObjectEntity var2 = this.getObjectEntity(var1);
      return var2 instanceof OEInventory ? (OEInventory)var2 : null;
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         OEInventory var3 = this.getOEInventory(var2.getLevel());
         if (var3 != null) {
            var3.setInventoryName(this.name);
         }

      }
   }
}
