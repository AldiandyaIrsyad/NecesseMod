package necesse.engine.util.gameAreaSearch;

import java.awt.Point;

public class GameAreaPointSearch extends GameAreaSearch<Point> {
   public GameAreaPointSearch(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super(var1, var2, var3, var4, var5, var6, var7);
   }

   public GameAreaPointSearch(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected Point get(int var1, int var2) {
      return new Point(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Object get(int var1, int var2) {
      return this.get(var1, var2);
   }
}
