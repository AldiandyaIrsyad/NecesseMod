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

public class PacketChangeTile extends Packet {
   public final int levelIdentifierHashCode;
   public final int tileX;
   public final int tileY;
   public final int tileID;

   public PacketChangeTile(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.tileX = var2.getNextInt();
      this.tileY = var2.getNextInt();
      this.tileID = var2.getNextShortUnsigned();
   }

   public PacketChangeTile(Level var1, int var2, int var3, int var4) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.tileX = var2;
      this.tileY = var3;
      this.tileID = var4;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextInt(this.levelIdentifierHashCode);
      var5.putNextInt(var2);
      var5.putNextInt(var3);
      var5.putNextShortUnsigned(var4);
   }

   public void updateLevel(Level var1) {
      var1.setTile(this.tileX, this.tileY, this.tileID);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            this.updateLevel(var2.world.getLevel(var3));
            var2.network.sendToClientsAt(this, (ServerClient)var3);
         } else {
            System.out.println(var3.getName() + " tried to change tile, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to change tile, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         this.updateLevel(var2.getLevel());
      }
   }
}
