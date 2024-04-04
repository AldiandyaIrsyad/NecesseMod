package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;
import necesse.level.maps.Level;

public class PacketSpawnFirework extends Packet {
   public final int levelIdentifierHashCode;
   public final float x;
   public final float y;
   public final int height;
   public final float size;
   public final GNDItemMap gndData;
   public final int seed;

   public PacketSpawnFirework(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.x = var2.getNextFloat();
      this.y = var2.getNextFloat();
      this.height = var2.getNextInt();
      this.size = var2.getNextFloat();
      this.gndData = new GNDItemMap(var2);
      this.seed = var2.getNextInt();
   }

   public PacketSpawnFirework(Level var1, float var2, float var3, int var4, float var5, GNDItemMap var6, int var7) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.x = var2;
      this.y = var3;
      this.height = var4;
      this.size = var5;
      this.gndData = var6;
      this.seed = var7;
      PacketWriter var8 = new PacketWriter(this);
      var8.putNextInt(this.levelIdentifierHashCode);
      var8.putNextFloat(var2);
      var8.putNextFloat(var3);
      var8.putNextInt(var4);
      var8.putNextFloat(var5);
      var6.writePacket(var8);
      var8.putNextInt(var7);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, (int)(this.x / 32.0F), (int)(this.y / 32.0F), true)) {
         FireworkPlaceableItem.spawnFireworks(this.gndData, var2.getLevel(), this.x, this.y, this.height, this.size, this.seed);
      }
   }
}
