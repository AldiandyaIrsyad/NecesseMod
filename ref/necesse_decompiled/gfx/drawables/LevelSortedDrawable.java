package necesse.gfx.drawables;

import necesse.level.gameObject.GameObject;

public abstract class LevelSortedDrawable implements Drawable, Comparable<LevelSortedDrawable> {
   private final Object object;
   private final int uniqueDecider;
   private int sort;

   protected LevelSortedDrawable(Object var1, int var2, boolean var3) {
      this.object = var1;
      this.uniqueDecider = var2;
      if (var3) {
         this.init();
      }

   }

   protected LevelSortedDrawable(Object var1, boolean var2) {
      this(var1, 0, var2);
   }

   public LevelSortedDrawable(Object var1, int var2) {
      this(var1, var2, true);
   }

   public LevelSortedDrawable(Object var1) {
      this(var1, true);
   }

   protected void init() {
      this.sort = this.getSortY();
   }

   public LevelSortedDrawable(GameObject var1, int var2, int var3) {
      this.object = var1;
      this.uniqueDecider = var2;
      this.sort = var3 * 32 + this.getSortY();
   }

   public abstract int getSortY();

   public int compareTo(LevelSortedDrawable var1) {
      if (var1 == null) {
         return -1;
      } else {
         int var2 = Integer.compare(this.sort, var1.sort);
         if (var2 == 0) {
            var2 = Integer.compare(System.identityHashCode(this.object), System.identityHashCode(var1.object));
            if (var2 == 0) {
               var2 = Integer.compare(this.uniqueDecider, this.uniqueDecider);
            }
         }

         return var2;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((LevelSortedDrawable)var1);
   }
}
