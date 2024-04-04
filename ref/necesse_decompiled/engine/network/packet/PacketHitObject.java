package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class PacketHitObject extends Packet {
   public final int levelIdentifierHashCode;
   public final int tileX;
   public final int tileY;
   public final GameObject object;
   public final GameDamage damage;

   public PacketHitObject(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      int var3 = var2.getNextShortUnsigned();
      this.object = ObjectRegistry.getObject(var3);
      this.damage = GameDamage.fromPacket(var2.getNextContentPacket());
   }

   public PacketHitObject(Level var1, int var2, int var3, GameObject var4, GameDamage var5) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.tileX = var2;
      this.tileY = var3;
      this.object = var4;
      this.damage = var5;
      PacketWriter var6 = new PacketWriter(this);
      var6.putNextInt(this.levelIdentifierHashCode);
      var6.putNextShortUnsigned(var2);
      var6.putNextShortUnsigned(var3);
      var6.putNextShortUnsigned(var4.getID());
      var6.putNextContentPacket(var5.getPacket());
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         this.object.attackThrough(var2.getLevel(), this.tileX, this.tileY, this.damage);
      }
   }
}
