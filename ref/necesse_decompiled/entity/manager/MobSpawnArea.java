package necesse.entity.manager;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;

public class MobSpawnArea {
   public final int minSpawnDistance;
   public final int maxSpawnDistance;
   private ArrayList<Point> positions = new ArrayList();

   public MobSpawnArea(int var1, int var2) {
      this.minSpawnDistance = var1;
      this.maxSpawnDistance = var2;
      int var3 = -var2 / 32 - 1;
      int var4 = var2 / 32 + 1;

      for(int var5 = var3; var5 <= var4; ++var5) {
         for(int var6 = var3; var6 <= var4; ++var6) {
            double var7 = (new Point(var5 * 32, var6 * 32)).distance(0.0, 0.0);
            if (!(var7 < (double)var1) && !(var7 > (double)var2)) {
               this.positions.add(new Point(var5, var6));
            }
         }
      }

   }

   public Point getRandomTile(GameRandom var1, int var2, int var3) {
      Point var4 = (Point)var1.getOneOf((List)this.positions);
      return new Point(var2 + var4.x, var3 + var4.y);
   }

   public Point getRandomTicketTile(GameRandom var1, int var2, int var3, Function<Point, Integer> var4) {
      TicketSystemList var5 = new TicketSystemList();
      Iterator var6 = this.positions.iterator();

      while(var6.hasNext()) {
         Point var7 = (Point)var6.next();
         Point var8 = new Point(var2 + var7.x, var3 + var7.y);
         int var9 = (Integer)var4.apply(var8);
         if (var9 > 0) {
            var5.addObject(var9, var8);
         }
      }

      return (Point)var5.getRandomObject(var1);
   }
}
