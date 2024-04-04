package necesse.level.maps;

public class LevelPosition {
   public final Level level;
   public final int x;
   public final int y;

   public LevelPosition(Level var1, int var2, int var3) {
      this.level = var1;
      this.x = var2;
      this.y = var3;
   }

   public int getTileX() {
      return this.x / 32;
   }

   public int getTileY() {
      return this.x / 32;
   }

   public TilePosition getTilePos() {
      return new TilePosition(this.level, this.getTileX(), this.getTileY());
   }
}
