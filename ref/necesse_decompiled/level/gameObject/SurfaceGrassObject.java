package necesse.level.gameObject;

import java.awt.Color;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;

public class SurfaceGrassObject extends GrassObject {
   public SurfaceGrassObject() {
      super("grass", 2);
      this.mapColor = new Color(52, 111, 43);
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      float var4 = 35.0F;
      if (var1.rainingLayer.isRaining()) {
         var4 = 15.0F;
      }

      return new LootTable(new LootItemInterface[]{new ChanceLootItem(1.0F / var4, "wormbait"), new ChanceLootItem(0.01F, "grassseed")});
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         return var1.getTileID(var2, var3) != TileRegistry.grassID ? "wrongtile" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      return super.isValid(var1, var2, var3) && var1.getTileID(var2, var3) == TileRegistry.grassID;
   }
}
