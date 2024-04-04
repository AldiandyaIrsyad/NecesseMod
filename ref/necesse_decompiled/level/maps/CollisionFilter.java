package necesse.level.maps;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import necesse.level.maps.regionSystem.SemiRegion;

public class CollisionFilter {
   private Predicate<TilePosition> filter = null;
   private LinkedList<BiConsumer<TilePosition, LinkedList<Rectangle>>> customAdders = new LinkedList();

   public CollisionFilter() {
   }

   public CollisionFilter copy() {
      CollisionFilter var1 = new CollisionFilter();
      var1.filter = this.filter;
      var1.customAdders.addAll(this.customAdders);
      return var1;
   }

   public boolean hasAdders() {
      return !this.customAdders.isEmpty();
   }

   public CollisionFilter customAdder(BiConsumer<TilePosition, LinkedList<Rectangle>> var1) {
      this.customAdders.add(var1);
      return this;
   }

   public CollisionFilter mobCollision() {
      return this.customAdder((var0, var1) -> {
         var1.addAll(var0.object().getCollisions(var0.object().rotation));
      });
   }

   public CollisionFilter projectileCollision() {
      return this.customAdder((var0, var1) -> {
         var1.addAll(var0.object().getProjectileCollisions(var0.object().rotation));
      });
   }

   public CollisionFilter attackThroughCollision() {
      return this.attackThroughCollision((var0) -> {
         return var0.object().object.attackThrough;
      });
   }

   public CollisionFilter attackThroughCollision(Predicate<TilePosition> var1) {
      return this.customAdder((var1x, var2) -> {
         if (var1.test(var1x)) {
            var2.addAll(var1x.object().getAttackThroughCollisions());
         }

      });
   }

   public CollisionFilter summonedMobCollision() {
      return this.addFilter((var0) -> {
         return var0.object().object.getRegionType() != SemiRegion.RegionType.SUMMON_IGNORED;
      });
   }

   public CollisionFilter allLandTiles() {
      return this.customAdder((var0, var1) -> {
         if (!var0.tile().tile.isLiquid) {
            var1.add(new Rectangle(var0.tileX * 32, var0.tileY * 32, 32, 32));
         }

      });
   }

   public CollisionFilter allLiquidTiles() {
      return this.customAdder((var0, var1) -> {
         if (var0.tile().tile.isLiquid) {
            var1.add(new Rectangle(var0.tileX * 32, var0.tileY * 32, 32, 32));
         }

      });
   }

   public CollisionFilter overrideFilter(Predicate<TilePosition> var1) {
      this.filter = var1;
      return this;
   }

   public CollisionFilter addFilter(Predicate<TilePosition> var1) {
      if (this.filter == null) {
         this.filter = var1;
      } else {
         this.filter = this.filter.and(var1);
      }

      return this;
   }

   public boolean testFilter(TilePosition var1) {
      return this.filter == null ? true : this.filter.test(var1);
   }

   public boolean check(Shape var1, TilePosition var2) {
      CollisionsGenerator var3 = new CollisionsGenerator(var2);

      for(Rectangle var4 = var3.getNext(); var4 != null; var4 = var3.getNext()) {
         if (var1.intersects(var4)) {
            return true;
         }
      }

      return false;
   }

   public void addCollisions(ArrayList<LevelObjectHit> var1, Shape var2, TilePosition var3) {
      CollisionsGenerator var4 = new CollisionsGenerator(var3);

      for(Rectangle var5 = var4.getNext(); var5 != null; var5 = var4.getNext()) {
         if (var2.intersects(var5)) {
            var1.add(new LevelObjectHit(var5, var3.level, var3.tileX, var3.tileY));
         }
      }

   }

   private class CollisionsGenerator {
      private LinkedList<Runnable> adders = new LinkedList();
      private LinkedList<Rectangle> rectangles = new LinkedList();

      public CollisionsGenerator(TilePosition var2) {
         if (CollisionFilter.this.testFilter(var2)) {
            Iterator var3 = CollisionFilter.this.customAdders.iterator();

            while(var3.hasNext()) {
               BiConsumer var4 = (BiConsumer)var3.next();
               this.adders.add(() -> {
                  var4.accept(var2, this.rectangles);
               });
            }

         }
      }

      public Rectangle getNext() {
         while(this.rectangles.isEmpty() && !this.adders.isEmpty()) {
            ((Runnable)this.adders.removeFirst()).run();
         }

         Rectangle var1;
         do {
            if (this.rectangles.isEmpty()) {
               return null;
            }

            var1 = (Rectangle)this.rectangles.removeFirst();
         } while(var1 == null);

         return var1;
      }
   }
}
