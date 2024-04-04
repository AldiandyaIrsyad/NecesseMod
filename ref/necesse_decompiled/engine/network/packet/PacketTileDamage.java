package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.entity.TileDamageType;
import necesse.level.maps.Level;

public class PacketTileDamage extends Packet {
   public final int levelIdentifierHashCode;
   public final TileDamageType type;
   public final int tileX;
   public final int tileY;
   public final int damage;
   public final boolean showEffect;
   public final int mouseX;
   public final int mouseY;

   public PacketTileDamage(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.levelIdentifierHashCode = var2.getNextInt();
      this.type = TileDamageType.values()[var2.getNextByteUnsigned()];
      this.tileX = var2.getNextShortUnsigned();
      this.tileY = var2.getNextShortUnsigned();
      this.damage = var2.getNextInt();
      this.showEffect = var2.getNextBoolean();
      this.mouseX = var2.getNextInt();
      this.mouseY = var2.getNextInt();
   }

   public PacketTileDamage(Level var1, TileDamageType var2, int var3, int var4, int var5, boolean var6, int var7, int var8) {
      this.levelIdentifierHashCode = var1.getIdentifierHashCode();
      this.type = var2;
      this.tileX = var3;
      this.tileY = var4;
      this.damage = var5;
      this.showEffect = var6;
      this.mouseX = var7;
      this.mouseY = var8;
      PacketWriter var9 = new PacketWriter(this);
      var9.putNextInt(this.levelIdentifierHashCode);
      var9.putNextByteUnsigned(var2.ordinal());
      var9.putNextShortUnsigned(var3);
      var9.putNextShortUnsigned(var4);
      var9.putNextInt(var5);
      var9.putNextBoolean(var6);
      var9.putNextInt(var7);
      var9.putNextInt(var8);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
         if (this.type == TileDamageType.Tile) {
            var2.getLevel().getLevelTile(this.tileX, this.tileY).onDamaged(this.damage, (ServerClient)null, this.showEffect, this.mouseX, this.mouseY);
         } else if (this.type == TileDamageType.Object) {
            var2.getLevel().getLevelObject(this.tileX, this.tileY).onDamaged(this.damage, (ServerClient)null, this.showEffect, this.mouseX, this.mouseY);
         }

      }
   }
}
