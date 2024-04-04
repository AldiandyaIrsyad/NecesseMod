package necesse.engine.network.packet;

import java.util.ArrayList;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelTile;

public class PacketTileDestroyed extends Packet {
   public final int levelIdentifierHashCode;
   public final int tileX;
   public final int tileY;
   public final int id;
   public final boolean isTile;

   public PacketTileDestroyed(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      this.id = var2.getNextShortUnsigned();
      this.isTile = var2.getNextBoolean();
   }

   public PacketTileDestroyed(Level var1, int var2, int var3, int var4, boolean var5) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.tileX = var2;
      this.tileY = var3;
      this.id = var4;
      this.isTile = var5;
      PacketWriter var6 = new PacketWriter(this);
      var6.putNextInt(this.levelIdentifierHashCode);
      var6.putNextShortUnsigned(var2);
      var6.putNextShortUnsigned(var3);
      var6.putNextShortUnsigned(var4);
      var6.putNextBoolean(var5);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         if (this.isTile) {
            LevelTile var3 = var2.getLevel().getLevelTile(this.tileX, this.tileY);
            if (var3.tile.getID() == this.id) {
               var3.onTileDestroyed((ServerClient)null, (ArrayList)null);
            } else {
               var2.network.sendPacket(new PacketRequestTileChange(this.tileX, this.tileY));
            }
         } else {
            LevelObject var4 = var2.getLevel().getLevelObject(this.tileX, this.tileY);
            if (var4.object.getID() == this.id) {
               var4.onObjectDestroyed((ServerClient)null, (ArrayList)null);
            } else {
               var2.network.sendPacket(new PacketRequestObjectChange(this.tileX, this.tileY));
            }
         }

      }
   }
}
