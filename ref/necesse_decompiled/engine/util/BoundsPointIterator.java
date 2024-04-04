package necesse.engine.util;

import java.awt.Point;
import java.util.Iterator;

public class BoundsPointIterator implements Iterator<Point> {
   public final int startX;
   public final int endX;
   public final int startY;
   public final int endY;
   private int currentX;
   private int currentY;

   public BoundsPointIterator(int var1, int var2, int var3, int var4) {
      this.startX = var1;
      this.endX = var2;
      this.startY = var3;
      this.endY = var4;
      this.currentX = var1;
      this.currentY = var3;
   }

   public boolean hasNext() {
      return this.currentX <= this.endX && this.currentY <= this.endY;
   }

   public Point next() {
      Point var1 = new Point(this.currentX, this.currentY);
      ++this.currentX;
      if (this.currentX > this.endX) {
         this.currentX = this.startX;
         ++this.currentY;
      }

      return var1;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object next() {
      return this.next();
   }
}
