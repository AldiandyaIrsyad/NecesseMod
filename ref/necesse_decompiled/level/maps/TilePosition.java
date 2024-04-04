package necesse.level.maps;

import java.awt.Point;

public class TilePosition {
   public final Level level;
   public final int tileX;
   public final int tileY;
   protected LevelObject object;
   protected LevelTile tile;

   public TilePosition(Level var1, int var2, int var3) {
      this.level = var1;
      this.tileX = var2;
      this.tileY = var3;
   }

   public TilePosition(Level var1, Point var2) {
      this(var1, var2.x, var2.y);
   }

   public int objectID() {
      return this.level.getObjectID(this.tileX, this.tileY);
   }

   public byte objectRotation() {
      return this.level.getObjectRotation(this.tileX, this.tileY);
   }

   public LevelObject object() {
      if (this.object == null) {
         this.object = new LevelObject(this.level, this.tileX, this.tileY);
      }

      return this.object;
   }

   public int tileID() {
      return this.level.getTileID(this.tileX, this.tileY);
   }

   public LevelTile tile() {
      if (this.tile == null) {
         this.tile = new LevelTile(this.level, this.tileX, this.tileY);
      }

      return this.tile;
   }

   public boolean isLiquidTile() {
      return this.level.isLiquidTile(this.tileX, this.tileY);
   }

   public boolean isShore() {
      return this.level.isShore(this.tileX, this.tileY);
   }

   public boolean isSolidTile() {
      return this.level.isSolidTile(this.tileX, this.tileY);
   }
}
