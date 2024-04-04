package necesse.inventory.item.placeableItem.objectItem;

import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class CampfireAddonObjectItem extends ObjectItem {
   public CampfireAddonObjectItem(GameObject var1) {
      super(var1);
   }

   public boolean onPlaceObject(GameObject var1, Level var2, int var3, int var4, int var5, ServerClient var6, InventoryItem var7) {
      ObjectEntity var8 = var2.entityManager.getObjectEntity(var3, var4);
      boolean var9 = super.onPlaceObject(var1, var2, var3, var4, var5, var6, var7);
      if (var9 && var8 instanceof OEInventory) {
         OEInventory var10 = (OEInventory)var8;
         ObjectEntity var11 = var2.entityManager.getObjectEntity(var3, var4);
         if (var11 instanceof OEInventory) {
            OEInventory var12 = (OEInventory)var11;
            var12.getInventory().override(var10.getInventory());
            if (var8 instanceof FueledInventoryObjectEntity && var11 instanceof FueledInventoryObjectEntity) {
               ((FueledInventoryObjectEntity)var11).fuelBurnTime = ((FueledInventoryObjectEntity)var8).fuelBurnTime;
               ((FueledInventoryObjectEntity)var11).fuelStartTime = ((FueledInventoryObjectEntity)var8).fuelStartTime;
            }
         }
      }

      return var9;
   }

   public boolean canReplace(Level var1, int var2, int var3, int var4, PlayerMob var5, InventoryItem var6, String var7) {
      return false;
   }
}
