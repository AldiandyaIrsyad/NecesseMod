package necesse.engine.util.gameAreaSearch;

import necesse.entity.Entity;
import necesse.entity.manager.EntityRegionList;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPosition;

public class EntityListRegionSearch<T extends Entity> extends GameRegionSearch<Iterable<T>> {
   private final EntityRegionList<T> entityRegionList;

   public EntityListRegionSearch(Level var1, EntityRegionList<T> var2, float var3, float var4, int var5) {
      super(var1, var1.regionManager.getRegionXByTile((int)(var3 / 32.0F)), var1.regionManager.getRegionYByTile((int)(var4 / 32.0F)), Math.max(var1.regionManager.getRegionsWidth(), var1.regionManager.getRegionsHeight()) + 1);
      this.entityRegionList = var2;
      int var6 = (int)(var3 / 32.0F);
      int var7 = (int)(var4 / 32.0F);
      RegionPosition var8 = var1.regionManager.getRegionPosByTile(var6 - var5 - 1, var7 - var5 - 1);
      RegionPosition var9 = var1.regionManager.getRegionPosByTile(var6 + var5 + 1, var7 + var5 + 1);
      this.shrinkLimit(var8.regionX, var8.regionY, var9.regionX, var9.regionY);
   }

   protected Iterable<T> get(int var1, int var2) {
      return this.entityRegionList.getInRegion(var1, var2);
   }

   public GameAreaStream<T> streamEach() {
      return this.stream().flatMap((var0) -> {
         return var0;
      });
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected Object get(int var1, int var2) {
      return this.get(var1, var2);
   }
}
