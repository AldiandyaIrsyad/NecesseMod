package necesse.level.maps.regionSystem;

import java.util.Collection;
import java.util.function.Predicate;
import necesse.level.maps.Level;

public interface RegionPositionGetter {
   Level getLevel();

   Collection<RegionPosition> getRegionPositions();

   default boolean isInRegion(RegionPosition var1) {
      Collection var2 = this.getRegionPositions();
      return var2.isEmpty() || var2.stream().anyMatch((var1x) -> {
         return var1x.isSame(var1);
      });
   }

   default boolean checkIfOccupyingRegions(Predicate<RegionPosition> var1) {
      Collection var2 = this.getRegionPositions();
      return var2.isEmpty() || var2.stream().anyMatch(var1);
   }
}
