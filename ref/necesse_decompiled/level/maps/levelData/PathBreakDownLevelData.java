package necesse.level.maps.levelData;

import java.awt.Point;
import java.util.HashMap;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class PathBreakDownLevelData extends LevelData {
   protected HashMap<Point, PathBreakData> tileBreakData = new HashMap();

   public PathBreakDownLevelData() {
   }

   public boolean shouldSave() {
      return false;
   }

   public int doDamage(int var1, int var2, int var3) {
      PathBreakData var4 = (PathBreakData)this.tileBreakData.compute(new Point(var1, var2), (var3x, var4x) -> {
         return var4x == null ? new PathBreakData(var1, var2) : var4x;
      });
      return var4.doDamage(var3);
   }

   public void clear(int var1, int var2) {
      this.tileBreakData.remove(new Point(var1, var2));
   }

   public static PathBreakDownLevelData getPathBreakDownData(Level var0) {
      LevelData var1 = var0.getLevelData("pathbreak");
      if (var1 instanceof PathBreakDownLevelData) {
         return (PathBreakDownLevelData)var1;
      } else {
         PathBreakDownLevelData var2 = new PathBreakDownLevelData();
         var0.addLevelData("pathbreak", var2);
         return var2;
      }
   }

   protected class PathBreakData {
      public final int tileX;
      public final int tileY;
      public int objectID;
      public int damageDone;
      public long lastDamageDoneTime;

      public PathBreakData(int var2, int var3) {
         this.tileX = var2;
         this.tileY = var3;
         this.objectID = PathBreakDownLevelData.this.getLevel().getObjectID(var2, var3);
      }

      public int doDamage(int var1) {
         GameObject var2 = PathBreakDownLevelData.this.getLevel().getObject(this.tileX, this.tileY);
         if (var2.getID() != this.objectID) {
            this.damageDone = 0;
         } else {
            long var3 = PathBreakDownLevelData.this.getLevel().getWorldEntity().getTime() - this.lastDamageDoneTime;
            if (var3 > 60000L) {
               this.damageDone = 0;
            }

            if (var3 < 1000L) {
               float var5 = (float)var3 / 1000.0F;
               var1 = Math.max(1, Math.round((float)var1 * var5));
            }
         }

         this.damageDone += var1;
         this.lastDamageDoneTime = PathBreakDownLevelData.this.getLevel().getWorldEntity().getTime();
         return this.damageDone;
      }
   }
}
