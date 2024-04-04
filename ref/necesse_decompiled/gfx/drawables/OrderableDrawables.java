package necesse.gfx.drawables;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.tickManager.TickManager;

public class OrderableDrawables {
   private NavigableMap<Integer, List<Drawable>> map;
   private Supplier<List<Drawable>> listGenerator;

   public OrderableDrawables(NavigableMap<Integer, List<Drawable>> var1, Supplier<List<Drawable>> var2) {
      this.map = var1;
      this.listGenerator = var2;
   }

   public OrderableDrawables(NavigableMap<Integer, List<Drawable>> var1) {
      this(var1, ArrayList::new);
   }

   public void add(Drawable var1) {
      this.add(0, var1);
   }

   public void add(int var1, Drawable var2) {
      this.map.compute(var1, (var2x, var3) -> {
         if (var3 == null) {
            var3 = (List)this.listGenerator.get();
         }

         var3.add(var2);
         return var3;
      });
   }

   public void forEach(Consumer<? super Drawable> var1) {
      this.map.forEach((var1x, var2) -> {
         var2.forEach(var1);
      });
   }

   public void draw(TickManager var1) {
      this.forEach((var1x) -> {
         var1x.draw(var1);
      });
   }
}
