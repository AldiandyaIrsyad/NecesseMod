package necesse.engine.util.gameAreaSearch;

import java.util.stream.Stream;
import necesse.entity.Entity;
import necesse.entity.manager.EntityRegionList;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPosition;

public class EntityListsRegionSearch<T extends Entity> extends GameRegionSearch<Stream<? extends T>> {
   private final EntityRegionList<? extends T>[] entityRegionLists;

   public EntityListsRegionSearch(Level var1, float var2, float var3, int var4, EntityRegionList<? extends T>... var5) {
      super(var1, var1.regionManager.getRegionXByTile((int)(var2 / 32.0F)), var1.regionManager.getRegionYByTile((int)(var3 / 32.0F)), Math.max(var1.regionManager.getRegionsWidth(), var1.regionManager.getRegionsHeight()) + 1);
      this.entityRegionLists = var5;
      int var6 = (int)(var2 / 32.0F);
      int var7 = (int)(var3 / 32.0F);
      RegionPosition var8 = var1.regionManager.getRegionPosByTile(var6 - var4 - 1, var7 - var4 - 1);
      RegionPosition var9 = var1.regionManager.getRegionPosByTile(var6 + var4 + 1, var7 + var4 + 1);
      this.shrinkLimit(var8.regionX, var8.regionY, var9.regionX, var9.regionY);
   }

   protected Stream<? extends T> get(int var1, int var2) {
      Stream var3 = null;
      EntityRegionList[] var4 = this.entityRegionLists;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EntityRegionList var7 = var4[var6];
         if (var3 == null) {
            var3 = var7.getInRegion(var1, var2).stream();
         } else {
            var3 = Stream.concat(var3, var7.getInRegion(var1, var2).stream());
         }
      }

      return var3;
   }

   public GameAreaStream<T> streamEach() {
      return this.stream().flatStream((var0) -> {
         return var0;
      });
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Object get(int var1, int var2) {
      return this.get(var1, var2);
   }
}
