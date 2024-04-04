package necesse.level.gameObject;

import java.awt.Color;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;

public class SwampGrassObject extends GrassObject {
   public SwampGrassObject() {
      super("swampgrass", 4);
      this.mapColor = new Color(55, 67, 53);
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      float var4 = 30.0F;
      if (var1.rainingLayer.isRaining()) {
         var4 = 10.0F;
      }

      if (var1.isCave) {
         var4 = 25.0F;
      }

      return new LootTable(new LootItemInterface[]{new ChanceLootItem(1.0F / var4, "swamplarva"), new ChanceLootItem(0.01F, "swampgrassseed")});
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         int var6 = var1.getTileID(var2, var3);
         return var6 != TileRegistry.swampRockID && var6 != TileRegistry.swampGrassID ? "wrongtile" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      if (!super.isValid(var1, var2, var3)) {
         return false;
      } else {
         int var4 = var1.getTileID(var2, var3);
         return var4 == TileRegistry.swampRockID || var4 == TileRegistry.swampGrassID;
      }
   }
}
