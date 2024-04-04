package necesse.level.maps.light;

import java.awt.Point;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import necesse.level.maps.Level;

public class LightCompute {
   protected final Level level;
   protected final LightMap map;
   protected final SourcedLightArea area;
   protected boolean computed;
   protected LinkedList<DetachedSourceLight> sources = new LinkedList();
   protected LinkedList<Point> open = new LinkedList();
   protected HashSet<Point> closed = new HashSet();

   public LightCompute(Level var1, LightMap var2, int var3, int var4, int var5, int var6) {
      this.level = var1;
      this.map = var2;
      this.area = new SourcedLightArea(var3, var4, var5, var6);
   }

   public void addSource(int var1, int var2, SourcedGameLight var3) {
      if (this.computed) {
         throw new IllegalStateException("Cannot add sources to already computed area");
      } else {
         this.sources.add(new DetachedSourceLight(var1, var2, var3));
      }
   }

   public void addSource(SourcedGameLight var1) {
      this.addSource(var1.sourceX, var1.sourceY, var1);
   }

   public int compute() {
      this.computed = true;
      this.area.initLights();
      Iterator var1 = this.sources.iterator();

      while(var1.hasNext()) {
         DetachedSourceLight var2 = (DetachedSourceLight)var1.next();
         this.area.lights[this.area.getIndex(var2.x, var2.y)] = var2.source;
         this.open.add(new Point(var2.x, var2.y));
      }

      int var5 = 0;

      while(!this.open.isEmpty()) {
         ++var5;
         Point var6 = (Point)this.open.removeFirst();
         SourcedGameLight var3 = this.area.lights[this.area.getIndex(var6.x, var6.y)];
         this.closed.add(new Point(var6.x, var6.y));
         int var4 = Math.max(10, this.level.getObject(var6.x, var6.y).getLightLevelMod(this.level, var6.x, var6.y));
         this.handleTile(var6.x, var6.y - 1, var3, var4);
         this.handleTile(var6.x - 1, var6.y, var3, var4);
         this.handleTile(var6.x + 1, var6.y, var3, var4);
         this.handleTile(var6.x, var6.y + 1, var3, var4);
      }

      return var5;
   }

   public int apply(Collection<Integer> var1) {
      Iterator var2 = this.closed.iterator();

      while(var2.hasNext()) {
         Point var3 = (Point)var2.next();
         SourcedGameLight var4 = this.area.lights[this.area.getIndex(var3.x, var3.y)];
         this.map.addSourcedLight(var3.x, var3.y, var4, var1);
      }

      return this.closed.size();
   }

   private void handleTile(int var1, int var2, SourcedGameLight var3, int var4) {
      if (!this.area.isOutsideArea(var1, var2)) {
         int var5 = this.area.getIndex(var1, var2);
         float var6 = Math.max(var3.light.getLevel() - (float)var4, 0.0F);
         if (var6 > 0.0F && (this.area.lights[var5] == null || this.area.lights[var5].light.getLevel() < var6)) {
            GameLight var7 = var3.light.copy();
            var7.setLevel(var6);
            SourcedGameLight var8 = new SourcedGameLight(var3.sourceX, var3.sourceY, var7);
            if (this.map.hasNoBetterSameSource(var1, var2, var8) && this.map.hasNoBetterSameColor(var1, var2, var8)) {
               this.area.lights[var5] = var8;
               if (!this.open.contains(new Point(var1, var2))) {
                  this.open.add(new Point(var1, var2));
               }
            }
         }

      }
   }

   public void printDebug(PrintStream var1) {
      var1.println("Computed: " + this.computed + ", Sources: " + this.sources.size());
      var1.println(this.safeToArrayString(this.sources.stream().map((var0) -> {
         return var0.source;
      }).limit(100L)));
      var1.println("Open: " + this.open.size() + ", " + this.safeToArrayString(this.open.stream().map((var0) -> {
         return var0.x + "x" + var0.y;
      })));
      var1.println("Closed: " + this.closed.size() + ", " + this.safeToArrayString(this.closed.stream().map((var0) -> {
         return var0.x + "x" + var0.y;
      })));
   }

   private String safeToArrayString(Stream<?> var1) {
      try {
         return Arrays.toString(var1.toArray());
      } catch (Exception var3) {
         return "ERR:" + var3;
      }
   }

   protected static class DetachedSourceLight {
      public final int x;
      public final int y;
      public final SourcedGameLight source;

      public DetachedSourceLight(int var1, int var2, SourcedGameLight var3) {
         this.x = var1;
         this.y = var2;
         this.source = var3;
      }
   }
}
