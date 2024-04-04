package necesse.level.maps;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;

public class CustomTilePosition extends TilePosition {
   public CustomTilePosition(Level var1, int var2, int var3, int var4, int var5, byte var6) {
      super(var1, var2, var3);
      this.object = LevelObject.custom(var1, var2, var3, ObjectRegistry.getObject(var5), var6);
      this.tile = LevelTile.custom(var1, var2, var3, TileRegistry.getTile(var4));
   }

   public int objectID() {
      return this.object.object.getID();
   }

   public byte objectRotation() {
      return this.object.rotation;
   }

   public int tileID() {
      return this.tile.tile.getID();
   }

   public boolean isLiquidTile() {
      return this.tile.tile.isLiquid;
   }

   public boolean isShore() {
      return super.isShore();
   }

   public boolean isSolidTile() {
      return this.object.object.isSolid(this.level, this.tileX, this.tileY);
   }
}
