package necesse.gfx.forms.components.containerSlot;

import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;

public class FormContainerBrokerSlot extends FormContainerSlot {
   public FormContainerBrokerSlot(Client var1, Container var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   public GameTooltips getItemTooltip(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips(super.getItemTooltip(var1, var2));
      float var4 = GameMath.toDecimals(var1.getBrokerValue(), 2);
      String var5 = (float)((int)var4) == var4 ? String.valueOf((int)var4) : String.valueOf(var4);
      var3.add((Object)(new SpacerGameTooltip(10)));
      var3.add((Object)(new StringTooltips(Localization.translate("ui", "brokervalue", "value", var5 + TypeParsers.getItemParseString(new InventoryItem("coin"))))));
      return var3;
   }
}
