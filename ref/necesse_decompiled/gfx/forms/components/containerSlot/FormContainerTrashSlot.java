package necesse.gfx.forms.components.containerSlot;

import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.container.Container;

public class FormContainerTrashSlot extends FormContainerSlot {
   public FormContainerTrashSlot(Client var1, Container var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
      this.setDecal(Settings.UI.inventoryslot_icon_trash);
   }

   public GameTooltips getClearTooltips() {
      return new StringTooltips(Localization.translate("itemtooltip", "trashslot"));
   }
}
