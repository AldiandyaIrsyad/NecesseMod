package necesse.level.gameObject;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.TileRegistry;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;

public class DeepSwampGrassObject extends GrassObject {
   public DeepSwampGrassObject() {
      super("deepswampgrass", 4);
      this.mapColor = new Color(37, 63, 37);
   }

   public GameMessage getNewLocalization() {
      return new LocalMessage("object", "swampgrass");
   }

   public LootTable getLootTable(Level var1, int var2, int var3) {
      float var4 = 30.0F;
      if (var1.rainingLayer.isRaining()) {
         var4 = 10.0F;
      }

      if (var1.isCave) {
         var4 = 25.0F;
      }

      return new LootTable(new LootItemInterface[]{new ChanceLootItem(1.0F / var4, "swamplarva")});
   }

   public String canPlace(Level var1, int var2, int var3, int var4) {
      String var5 = super.canPlace(var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      } else {
         int var6 = var1.getTileID(var2, var3);
         return var6 != TileRegistry.deepSwampRockID ? "wrongtile" : null;
      }
   }

   public boolean isValid(Level var1, int var2, int var3) {
      if (!super.isValid(var1, var2, var3)) {
         return false;
      } else {
         int var4 = var1.getTileID(var2, var3);
         return var4 == TileRegistry.deepSwampRockID;
      }
   }
}
