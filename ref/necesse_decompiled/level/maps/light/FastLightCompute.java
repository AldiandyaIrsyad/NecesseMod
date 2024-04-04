package necesse.level.maps.light;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.level.maps.Level;

public class FastLightCompute {
   protected final Level level;
   protected final FastLightMap map;
   protected final LightArea area;
   protected LinkedList<LightPoint> sources = new LinkedList();
   protected LinkedList<Point> open = new LinkedList();
   protected HashSet<Point> closed = new HashSet();

   public FastLightCompute(Level var1, FastLightMap var2, int var3, int var4, int var5, int var6) {
      this.level = var1;
      this.map = var2;
      this.area = new LightArea(var3, var4, var5, var6);
   }

   public void addSource(int var1, int var2, GameLight var3) {
      this.sources.add(new LightPoint(var1, var2, var3));
   }

   public int compute() {
      this.area.initLights();
      Iterator var1 = this.sources.iterator();

      while(var1.hasNext()) {
         LightPoint var2 = (LightPoint)var1.next();
         this.area.lights[this.area.getIndex(var2.x, var2.y)] = var2.light;
         this.open.add(new Point(var2.x, var2.y));
      }

      this.sources = null;
      int var5 = 0;

      while(!this.open.isEmpty()) {
         ++var5;
         Point var6 = (Point)this.open.removeFirst();
         GameLight var3 = this.area.lights[this.area.getIndex(var6.x, var6.y)];
         this.closed.add(new Point(var6.x, var6.y));
         int var4 = Math.max(10, this.level.getObject(var6.x, var6.y).getLightLevelMod(this.level, var6.x, var6.y));
         this.handleTile(var6.x, var6.y - 1, var3, var4);
         this.handleTile(var6.x - 1, var6.y, var3, var4);
         this.handleTile(var6.x + 1, var6.y, var3, var4);
         this.handleTile(var6.x, var6.y + 1, var3, var4);
      }

      return var5;
   }

   public int apply(LightArea var1) {
      Iterator var2 = this.closed.iterator();

      while(var2.hasNext()) {
         Point var3 = (Point)var2.next();
         var1.lights[var1.getIndex(var3.x, var3.y)].combine(this.area.lights[this.area.getIndex(var3.x, var3.y)]);
      }

      return this.closed.size();
   }

   private void handleTile(int var1, int var2, GameLight var3, int var4) {
      if (!this.area.isOutsideArea(var1, var2)) {
         int var5 = this.area.getIndex(var1, var2);
         float var6 = Math.max(var3.getLevel() - (float)var4, 0.0F);
         if (var6 > 0.0F && (this.area.lights[var5] == null || this.area.lights[var5].getLevel() < var6)) {
            GameLight var7 = var3.copy();
            var7.setLevel(var6);
            this.area.lights[var5] = var7;
            if (!this.open.contains(new Point(var1, var2))) {
               this.open.add(new Point(var1, var2));
            }
         }

      }
   }

   private static class LightPoint {
      public final int x;
      public final int y;
      public final GameLight light;

      public LightPoint(int var1, int var2, GameLight var3) {
         this.x = var1;
         this.y = var2;
         this.light = var3;
      }
   }
}
