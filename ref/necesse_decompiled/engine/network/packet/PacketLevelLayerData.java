package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketIterator;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;
import necesse.level.maps.layers.LevelLayer;

public class PacketLevelLayerData extends Packet {
   public final int levelIdentifierHashCode;
   public final int layerID;
   public final PacketIterator iterator;

   public PacketLevelLayerData(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.layerID = var2.getNextShortUnsigned();
      this.iterator = new PacketReader(var2);
   }

   public PacketLevelLayerData(LevelLayer var1) {
      this.levelIdentifierHashCode = var1.level.getIdentifierHashCode();
      this.layerID = var1.getID();
      PacketWriter var2 = new PacketWriter(this);
      var2.putNextInt(this.levelIdentifierHashCode);
      var2.putNextShortUnsigned(this.layerID);
      this.iterator = new PacketWriter(var2);
      var1.writeLevelDataPacket(var2);
   }

   public void processServer(NetworkPacket var1, Server var2, ServerClient var3) {
      if (var3.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
         if (var2.world.settings.allowCheats) {
            Level var4 = var2.world.getLevel(var3);
            if (var4.getIdentifierHashCode() == this.levelIdentifierHashCode) {
               LevelLayer var5 = var4.getLayer(this.layerID, LevelLayer.class);
               var5.readLevelDataPacket(new PacketReader(this.iterator));
               var2.network.sendToClientsAt(this, (ServerClient)var3);
            } else {
               System.out.println(var3.getName() + " tried to change level layer data on wrong level");
            }
         } else {
            System.out.println(var3.getName() + " tried to change level layer data, but cheats aren't allowed");
         }
      } else {
         System.out.println(var3.getName() + " tried to change level layer data, but isn't admin");
      }

   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
         LevelLayer var3 = var2.getLevel().getLayer(this.layerID, LevelLayer.class);
         var3.readLevelDataPacket(new PacketReader(this.iterator));
      }
   }
}
