package necesse.entity.manager;

import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;
import necesse.engine.util.gameAreaSearch.EntityListRegionSearch;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.LevelRegionsSpliterator;
import necesse.level.maps.regionSystem.RegionPosition;

public class EntityRegionList<T extends Entity> {
   protected final Level level;
   protected final Function<T, Point> getPosition;
   protected GameLinkedList<? extends T>[][] regionEntities;

   public EntityRegionList(Level var1, Function<T, Point> var2) {
      this.level = var1;
      this.getPosition = var2;
      this.regionEntities = new GameLinkedList[var1.regionManager.getRegionsWidth()][var1.regionManager.getRegionsHeight()];

      for(int var3 = 0; var3 < this.regionEntities.length; ++var3) {
         for(int var4 = 0; var4 < this.regionEntities[var3].length; ++var4) {
            this.regionEntities[var3][var4] = new GameLinkedList();
         }
      }

   }

   public void updateRegion(T var1) {
      Point var2 = (Point)this.getPosition.apply(var1);
      RegionPosition var3 = this.level.regionManager.getRegionPosByTile(var2.x / 32, var2.y / 32);
      var1.updateRegion(this.regionEntities[var3.regionX][var3.regionY]);
   }

   public GameLinkedList<T> getInRegion(int var1, int var2) {
      return this.regionEntities[var1][var2];
   }

   public Stream<T> streamInRegionsShape(Shape var1, int var2) {
      return (new LevelRegionsSpliterator(this.level, var1, var2)).stream().flatMap((var1x) -> {
         return this.getInRegion(var1x.regionX, var1x.regionY).stream();
      });
   }

   public Stream<T> streamInRegionsInRange(float var1, float var2, int var3) {
      return this.streamInRegionsShape(GameUtils.rangeBounds(var1, var2, var3), 0);
   }

   public Stream<T> streamInRegionsInTileRange(int var1, int var2, int var3) {
      return this.streamInRegionsShape(GameUtils.rangeTileBounds(var1, var2, var3), 0);
   }

   public GameAreaStream<T> streamArea(float var1, float var2, int var3) {
      return this.streamAreaTileRange((int)var1, (int)var2, var3 / 32 + 1);
   }

   public GameAreaStream<T> streamAreaTileRange(int var1, int var2, int var3) {
      return (new EntityListRegionSearch(this.level, this, (float)var1, (float)var2, var3)).streamEach();
   }

   public GameLinkedList<T> getInRegionTileByTile(int var1, int var2) {
      return this.getInRegion(this.level.regionManager.getRegionXByTile(var1), this.level.regionManager.getRegionYByTile(var2));
   }

   public ArrayList<T> getInRegionRange(int var1, int var2, int var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = var1 - var3; var5 <= var1 + var3; ++var5) {
         if (var5 >= 0 && var5 < this.level.regionManager.getRegionsWidth()) {
            for(int var6 = var2 - var3; var6 <= var2 + var3; ++var6) {
               if (var6 >= 0 && var6 < this.level.regionManager.getRegionsHeight()) {
                  var4.addAll(this.getInRegion(var5, var6));
               }
            }
         }
      }

      return var4;
   }

   public ArrayList<T> getInRegionRangeByTile(int var1, int var2, int var3) {
      RegionPosition var4 = this.level.regionManager.getRegionPosByTile(var1, var2);
      return this.getInRegionRange(var4.regionX, var4.regionY, var3);
   }

   public ArrayList<T> getInRegionByTileRange(int var1, int var2, int var3) {
      int var4 = Math.max(1, var3 / 15 + 1);
      RegionPosition var5 = this.level.regionManager.getRegionPosByTile(var1, var2);
      ArrayList var6 = new ArrayList();
      Iterator var7 = this.getInRegionRange(var5.regionX, var5.regionY, var4).iterator();

      while(var7.hasNext()) {
         Entity var8 = (Entity)var7.next();
         Point var9 = (Point)this.getPosition.apply(var8);
         Point var10 = new Point(var9.x / 32, var9.y / 32);
         if (var10.x >= var1 - var3 && var10.x <= var1 + var3 && var10.y >= var2 - var3 && var10.y <= var2 + var3) {
            var6.add(var8);
         }
      }

      return var6;
   }
}
