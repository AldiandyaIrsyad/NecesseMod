package necesse.level.gameObject;

import java.awt.Color;
import necesse.engine.registries.TileRegistry;
import necesse.level.maps.Level;

public class CryptGrassObject extends GrassObject {
   public CryptGrassObject() {
      super("cryptgrass", 1);
      this.mapColor = new Color(61, 50, 47);
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         int var6 = var1.getTileID(var2, var3);
         return var6 != TileRegistry.cryptAshID ? "wrongtile" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      if (!super.isValid(var1, var2, var3)) {
         return false;
      } else {
         int var4 = var1.getTileID(var2, var3);
         return var4 == TileRegistry.cryptAshID;
      }
   }
}
