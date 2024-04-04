package necesse.entity.mobs.ai.path;

import java.awt.Point;
import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.entity.mobs.Mob;
import necesse.gfx.camera.GameCamera;

public class FinalPath {
   private static float TILE_LENGTH = 32.0F;
   private static float TILE_DIAGONAL_LENGTH;
   private ArrayList<FinalPathPoint> pathArray;
   private float removedPathLength;
   private float pathLength = -1.0F;

   public FinalPath(ArrayList<FinalPathPoint> var1) {
      this.pathArray = var1;
   }

   public float getCurrentLength() {
      if (this.pathLength == -1.0F) {
         this.pathLength = calculatePathLength(this.pathArray);
      }

      return this.pathLength;
   }

   public float getFullLength() {
      return this.getCurrentLength() + this.removedPathLength;
   }

   public int size() {
      return this.pathArray != null ? this.pathArray.size() : 0;
   }

   public void removeFirst() {
      FinalPathPoint var1 = (FinalPathPoint)this.pathArray.get(0);
      this.pathArray.remove(0);
      if (this.pathArray.size() <= 1) {
         this.pathLength = 0.0F;
      } else {
         FinalPathPoint var2 = (FinalPathPoint)this.pathArray.get(0);
         float var3 = calculatePathLength(var1, var2);
         if (this.pathLength > 0.0F) {
            this.pathLength -= var3;
         }

         this.removedPathLength += var3;
      }

   }

   public FinalPathPoint getFirst() {
      return this.pathArray != null && this.pathArray.size() != 0 ? (FinalPathPoint)this.pathArray.get(0) : null;
   }

   public FinalPathPoint getLast() {
      return this.pathArray != null && this.pathArray.size() != 0 ? (FinalPathPoint)this.pathArray.get(this.pathArray.size() - 1) : null;
   }

   public Stream<FinalPathPoint> streamPathPoints() {
      return this.pathArray.stream();
   }

   public void drawPath(Mob var1, GameCamera var2) {
      TilePathfinding.drawPath(var1, this.pathArray, var2);
   }

   public static float calculatePathLength(ArrayList<? extends Point> var0) {
      if (var0 != null && var0.size() != 1) {
         float var1 = 0.0F;

         for(int var2 = 1; var2 < var0.size(); ++var2) {
            Point var3 = (Point)var0.get(var2 - 1);
            Point var4 = (Point)var0.get(var2);
            var1 += calculatePathLength(var3, var4);
         }

         return var1;
      } else {
         return 0.0F;
      }
   }

   public static float calculatePathLength(Point var0, Point var1) {
      int var2 = Math.abs(var0.x - var1.x);
      int var3 = Math.abs(var0.y - var1.y);
      if (var2 == 0) {
         return TILE_LENGTH * (float)var3;
      } else if (var2 == var3) {
         return TILE_DIAGONAL_LENGTH * (float)var2;
      } else {
         float var4 = 0.0F;
         if (var2 < var3) {
            while(var3 != var2) {
               var4 += TILE_LENGTH;
               --var3;
            }

            var4 += TILE_DIAGONAL_LENGTH * (float)var2;
         } else {
            while(var2 != var3) {
               var4 += TILE_LENGTH;
               --var2;
            }

            var4 += TILE_DIAGONAL_LENGTH * (float)var3;
         }

         return var4;
      }
   }

   static {
      TILE_DIAGONAL_LENGTH = (float)Math.sqrt((double)(TILE_LENGTH * TILE_LENGTH * 2.0F));
   }
}
