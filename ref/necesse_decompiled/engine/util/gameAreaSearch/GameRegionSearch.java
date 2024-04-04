package necesse.engine.util.gameAreaSearch;

import necesse.level.maps.Level;

public abstract class GameRegionSearch<T> extends GameAreaSearch<T> {
   public final Level level;

   public GameRegionSearch(Level var1, int var2, int var3, int var4) {
      super(var2, var3, 0, 0, var1.regionManager.getRegionsWidth() - 1, var1.regionManager.getRegionsHeight() - 1, var4);
      this.level = var1;
   }

   protected abstract T get(int var1, int var2);
}
