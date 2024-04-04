package necesse.engine.save.levelData;

import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.inventory.item.Item;

public class ItemSave {
   public ItemSave() {
   }

   public static Item loadItem(String var0) {
      if (var0 == null) {
         return null;
      } else {
         if (!ItemRegistry.itemExists(var0)) {
            String var1 = VersionMigration.tryFixStringID(var0, VersionMigration.oldItemStringIDs);
            if (!var0.equals(var1)) {
               System.out.println("Migrated item from " + var0 + " to " + var1);
               var0 = var1;
            }
         }

         return ItemRegistry.getItem(var0);
      }
   }
}
