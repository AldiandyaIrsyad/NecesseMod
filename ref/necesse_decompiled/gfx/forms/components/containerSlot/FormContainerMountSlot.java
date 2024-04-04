package necesse.gfx.forms.components.containerSlot;

import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;

public class FormContainerMountSlot extends FormContainerSlot {
   public FormContainerMountSlot(Client var1, Container var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
      this.setDecal(Settings.UI.inventoryslot_icon_mount);
   }

   public GameTooltips getItemTooltip(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add((Object)super.getItemTooltip(var1, var2));
      if (var1.item.isMountItem()) {
         var3.add(Localization.translate("ui", "hotkeytip", "hotkey", "[input=" + Control.USE_MOUNT.id + "]"));
      }

      return var3;
   }

   public GameTooltips getClearTooltips() {
      StringTooltips var1 = new StringTooltips();
      var1.add(Localization.translate("itemtooltip", "mountslot"));
      var1.add(Localization.translate("ui", "hotkeytip", "hotkey", "[input=" + Control.USE_MOUNT.id + "]"));
      return var1;
   }
}
