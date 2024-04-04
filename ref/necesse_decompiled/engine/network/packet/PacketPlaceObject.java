package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectRegistry;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class PacketPlaceObject extends Packet {
   public final int levelIdentifierHashCode;
   public final int slot;
   public final int objectID;
   public final byte objectRotation;
   public final int tileX;
   public final int tileY;

   public PacketPlaceObject(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.slot = var2.getNextByteUnsigned();
      this.objectID = var2.getNextShortUnsigned();
      this.objectRotation = var2.getNextByte();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
   }

   public PacketPlaceObject(Level var1, ServerClient var2, int var3, int var4, int var5, int var6) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.slot = var2 == null ? 255 : var2.slot;
      this.objectID = var3;
      this.objectRotation = (byte)var4;
      this.tileX = var5;
      this.tileY = var6;
      PacketWriter var7 = new PacketWriter(this);
      var7.putNextInt(this.levelIdentifierHashCode);
      var7.putNextByteUnsigned(this.slot);
      var7.putNextShortUnsigned(var3);
      var7.putNextByte(this.objectRotation);
      var7.putNextShortUnsigned(var5);
      var7.putNextShortUnsigned(var6);
   }

   public GameObject getObject() {
      return ObjectRegistry.getObject(this.objectID);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         GameObject var3 = this.getObject();
         if (this.slot == var2.getSlot()) {
            var3.playPlaceSound(this.tileX, this.tileY);
         }

         var3.placeObject(var2.getLevel(), this.tileX, this.tileY, this.objectRotation);
      }
   }
}
