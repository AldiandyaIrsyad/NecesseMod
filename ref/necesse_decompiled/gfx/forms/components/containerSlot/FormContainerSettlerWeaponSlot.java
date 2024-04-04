package necesse.gfx.forms.components.containerSlot;

import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.item.SettlerWeaponItem;

public class FormContainerSettlerWeaponSlot extends FormContainerSlot {
   private EquipmentForm equipmentForm;

   public FormContainerSettlerWeaponSlot(Client var1, Container var2, int var3, int var4, int var5, EquipmentForm var6) {
      super(var1, var2, var3, var4, var5);
      this.equipmentForm = var6;
      this.setDecal(Settings.UI.inventoryslot_icon_weapon);
   }

   public GameTooltips getClearTooltips() {
      return new StringTooltips(Localization.translate("itemtooltip", "weaponslot"));
   }

   public GameTooltips getItemTooltip(InventoryItem var1, PlayerMob var2) {
      if (var1.item instanceof SettlerWeaponItem) {
         GameBlackboard var3 = (new GameBlackboard()).set("perspective", this.equipmentForm.getMob());
         return var1.item.getTooltips(var1, (PlayerMob)null, var3);
      } else {
         return super.getItemTooltip(var1, var2);
      }
   }
}
