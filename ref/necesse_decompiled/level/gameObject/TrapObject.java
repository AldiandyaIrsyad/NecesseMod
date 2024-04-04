package necesse.level.gameObject;

import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.maps.Level;

public class TrapObject extends GameObject {
   public TrapObject(Rectangle var1) {
      super(var1);
      this.setItemCategory(new String[]{"objects", "traps"});
      this.displayMapTooltip = true;
      this.showsWire = true;
      this.drawDamage = false;
      this.objectHealth = 1;
      this.toolType = ToolType.ALL;
      this.isLightTransparent = true;
   }

   public void onWireUpdate(Level var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         ObjectEntity var6 = var1.entityManager.getObjectEntity(var2, var3);
         if (var6 != null) {
            ((TrapObjectEntity)var6).triggerTrap(var4, var1.getObjectRotation(var2, var3));
         }
      }

   }

   public ObjectEntity getNewObjectEntity(Level var1, int var2, int var3) {
      return new TrapObjectEntity(var1, var2, var3, 10000L);
   }

   public ListGameTooltips getItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = super.getItemTooltips(var1, var2);
      var3.add(Localization.translate("itemtooltip", "activatedwiretip"));
      return var3;
   }
}
