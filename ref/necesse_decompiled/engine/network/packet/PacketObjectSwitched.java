package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ObjectRegistry;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SwitchObject;
import necesse.level.maps.Level;

public class PacketObjectSwitched extends Packet {
   public final int levelIdentifierHashCode;
   public final int tileX;
   public final int tileY;
   public final int objectID;

   public PacketObjectSwitched(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      this.objectID = var2.getNextShortUnsigned();
   }

   public PacketObjectSwitched(Level var1, int var2, int var3, int var4) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.tileX = var2;
      this.tileY = var3;
      this.objectID = var4;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextInt(this.levelIdentifierHashCode);
      var5.putNextShortUnsigned(var2);
      var5.putNextShortUnsigned(var3);
      var5.putNextShortUnsigned(var4);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         if (var2.getLevel().getObjectID(this.tileX, this.tileY) == this.objectID) {
            GameObject var3 = ObjectRegistry.getObject(this.objectID);
            if (var3 instanceof SwitchObject) {
               SwitchObject var4 = (SwitchObject)var3;
               var4.onSwitched(var2.getLevel(), this.tileX, this.tileY);
            } else {
               var2.network.sendPacket(new PacketRequestObjectChange(this.tileX, this.tileY));
            }
         } else {
            var2.network.sendPacket(new PacketRequestObjectChange(this.tileX, this.tileY));
         }

      }
   }
}
