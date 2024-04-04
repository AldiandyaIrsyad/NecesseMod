package necesse.level.maps;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.level.gameObject.GameObject;

public class LevelObjectHit extends Rectangle {
   public final Level level;
   public final int tileX;
   public final int tileY;

   public LevelObjectHit(Rectangle var1, Level var2, int var3, int var4) {
      super(var1);
      this.level = var2;
      this.tileX = var3;
      this.tileY = var4;
   }

   public LevelObjectHit(Rectangle var1, Level var2) {
      this(var1, var2, -1, -1);
   }

   public Point getPoint() {
      return new Point(this.tileX, this.tileY);
   }

   public GameObject getObject() {
      return this.level.getObject(this.tileX, this.tileY);
   }

   public LevelObject getLevelObject() {
      return this.level.getLevelObject(this.tileX, this.tileY);
   }

   public boolean invalidPos() {
      return this.tileX == -1 || this.tileY == -1;
   }
}
