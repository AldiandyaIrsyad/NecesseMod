package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class PacketObjectEntity extends Packet {
   public final int levelIdentifierHashCode;
   public final int tileX;
   public final int tileY;
   public final int objectID;
   public final Packet content;

   public PacketObjectEntity(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      this.objectID = var2.getNextShortUnsigned();
      this.content = var2.getNextContentPacket();
   }

   public PacketObjectEntity(ObjectEntity var1) {
      this.levelIdentifierHashCode = var1.getLevel().getIdentifierHashCode();
      this.tileX = var1.getTileX();
      this.tileY = var1.getTileY();
      this.objectID = var1.getObject().getID();
      this.content = new Packet();
      var1.setupContentPacket(new PacketWriter(this.content));
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.levelIdentifierHashCode);
      var2.putNextShortUnsigned(this.tileX);
      var2.putNextShortUnsigned(this.tileY);
      var2.putNextShortUnsigned(this.objectID);
      var2.putNextContentPacket(this.content);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Level var4 = var2.world.getLevel(var3);
            if (var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
               if (var4.getObjectID(this.tileX, this.tileY) == this.objectID) {
                  ObjectEntity var5 = var4.entityManager.getObjectEntity(this.tileX, this.tileY);
                  if (var5 != null) {
                     var5.applyContentPacket(new PacketReader(this.content));
                     var2.network.sendToClientsAtExcept(this, (ServerClient)var3, var3);
                  } else {
                     GameLog.warn.println(var3.getName() + " wrongfully attempted to update unknown object entity at (" + this.tileX + ", " + this.tileY + ").");
                  }
               } else {
                  GameLog.warn.println(var3.getName() + " wrongfully attempted to update wrong object entity id at (" + this.tileX + ", " + this.tileY + ").");
               }
            } else {
               GameLog.warn.println(var3.getName() + " tried to update object entity on wrong level");
            }
         } else {
            GameLog.warn.println(var3.getName() + " tried to update object entity, but cheats aren't allowed");
         }
      } else {
         GameLog.warn.println(var3.getName() + " tried to update object entity, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         var2.loading.objectEntities.submitObjectEntityPacket(this);
      }
   }
}
