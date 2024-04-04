package necesse.gfx.forms.components.containerSlot;

import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.item.armorItem.ArmorItem;

public class FormContainerArmorStandSlot extends FormContainerSlot {
   public FormContainerArmorStandSlot(Client var1, Container var2, int var3, int var4, int var5, ArmorItem.ArmorType var6) {
      super(var1, var2, var3, var4, var5);
      if (var6 == ArmorItem.ArmorType.HEAD) {
         this.setDecal(Settings.UI.inventoryslot_icon_helmet);
      }

      if (var6 == ArmorItem.ArmorType.CHEST) {
         this.setDecal(Settings.UI.inventoryslot_icon_chestplate);
      }

      if (var6 == ArmorItem.ArmorType.FEET) {
         this.setDecal(Settings.UI.inventoryslot_icon_boots);
      }

   }

   public GameTooltips getItemTooltip(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add((Object)super.getItemTooltip(var1, var2));
      if (var1.item.isArmorItem()) {
         var3.add((Object)(new InputTooltip(-99, Localization.translate("controls", "equiptip")) {
            public int getDrawOrder() {
               return Integer.MAX_VALUE;
            }
         }));
      }

      return var3;
   }
}
