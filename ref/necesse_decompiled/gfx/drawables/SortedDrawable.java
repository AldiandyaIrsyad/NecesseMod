package necesse.gfx.drawables;

public abstract class SortedDrawable implements Drawable, Comparable<SortedDrawable> {
   private int priority = this.getPriority();

   public SortedDrawable() {
   }

   public abstract int getPriority();

   public int compareTo(SortedDrawable var1) {
      return Integer.compare(this.priority, var1.priority);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((SortedDrawable)var1);
   }
}
