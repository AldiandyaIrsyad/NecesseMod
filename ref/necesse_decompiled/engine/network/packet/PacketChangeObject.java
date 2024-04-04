package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketChangeObject extends Packet {
   public final int levelIdentifierHashCode;
   public final int tileX;
   public final int tileY;
   public final int objectID;
   public final int rotation;

   public PacketChangeObject(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.tileX = var2.getNextInt();
      this.tileY = var2.getNextInt();
      this.objectID = var2.getNextShortUnsigned();
      this.rotation = var2.getNextByteUnsigned();
   }

   public PacketChangeObject(Level var1, int var2, int var3, int var4) {
      this(var1, var2, var3, var4, 0);
   }

   public PacketChangeObject(Level var1, int var2, int var3, int var4, int var5) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.tileX = var2;
      this.tileY = var3;
      this.objectID = var4;
      this.rotation = var5;
      PacketWriter var6 = new PacketWriter(this);
      var6.putNextInt(this.levelIdentifierHashCode);
      var6.putNextInt(var2);
      var6.putNextInt(var3);
      var6.putNextShortUnsigned(var4);
      var6.putNextByteUnsigned(var5);
   }

   public void updateLevel(Level var1) {
      var1.setObject(this.tileX, this.tileY, this.objectID, this.rotation);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Level var4 = var2.world.getLevel(var3);
            if (var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
               this.updateLevel(var4);
               var2.network.sendToClientsAt(this, (ServerClient)var3);
            } else {
               System.out.println(var3.getName() + " tried to change object on wrong level");
            }
         } else {
            System.out.println(var3.getName() + " tried to change object, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to change object, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         this.updateLevel(var2.getLevel());
      }
   }
}
