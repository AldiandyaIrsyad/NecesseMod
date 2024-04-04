package necesse.inventory.container.slots;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.SettlerWeaponItem;

public class SettlerWeaponContainerSlot extends ContainerSlot {
   public HumanMob mob;

   public SettlerWeaponContainerSlot(Inventory var1, int var2, HumanMob var3) {
      super(var1, var2);
      this.mob = var3;
   }

   public String getItemInvalidError(InventoryItem var1) {
      if (var1 != null) {
         if (var1.item instanceof SettlerWeaponItem) {
            GameMessage var2 = ((SettlerWeaponItem)var1.item).getSettlerCanUseError(this.mob, var1);
            return var2 == null ? null : var2.translate();
         } else {
            return Localization.translate("ui", "settlercantuseitem");
         }
      } else {
         return null;
      }
   }
}
