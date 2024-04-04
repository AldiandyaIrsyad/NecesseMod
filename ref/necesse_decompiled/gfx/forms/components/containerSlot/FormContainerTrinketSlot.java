package necesse.gfx.forms.components.containerSlot;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.slots.ContainerSlot;

public class FormContainerTrinketSlot extends FormContainerSlot {
   public boolean isAbilitySlot;

   public FormContainerTrinketSlot(Client var1, Container var2, int var3, int var4, int var5, boolean var6) {
      super(var1, var2, var3, var4, var5);
      this.isAbilitySlot = var6;
      this.setDecal(var6 ? Settings.UI.inventoryslot_icon_trinket_ability : Settings.UI.inventoryslot_icon_trinket);
   }

   public GameTooltips getItemTooltip(InventoryItem var1, PlayerMob var2) {
      GameBlackboard var3 = (new GameBlackboard()).set("equipped", true).set("isAbilitySlot", this.isAbilitySlot);
      ListGameTooltips var4 = var1.item.getTooltips(var1, var2, var3);
      ArrayList var5 = this.getDisables(var2);
      if (var5.size() > 0) {
         StringBuilder var6 = new StringBuilder();

         for(int var7 = 0; var7 < var5.size(); ++var7) {
            var6.append(ItemRegistry.getDisplayName((Integer)var5.get(var7)));
            if (var7 < var5.size() - 1) {
               var6.append(", ");
            }
         }

         var4.add((Object)(new StringTooltips(Localization.translate("itemtooltip", "trinketdisabled", "items", var6.toString()))));
      }

      return var4;
   }

   public ArrayList<Integer> getDisables(PlayerMob var1) {
      ContainerSlot var2 = this.getContainerSlot();
      return var1.equipmentBuffManager.getTrinketBuffDisables(var2.getItem());
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      super.draw(var1, var2, var3);
      ArrayList var4 = this.getDisables(var2);
      if (!var4.isEmpty()) {
         Settings.UI.note_disabled.initDraw().draw(this.getX() + 5, this.getY() + 5);
      }

   }

   public GameTooltips getClearTooltips() {
      StringTooltips var1 = new StringTooltips();
      var1.add(Localization.translate("itemtooltip", this.isAbilitySlot ? "trinketabilityslot" : "trinketslot"));
      if (this.isAbilitySlot) {
         var1.add(Localization.translate("ui", "hotkeytip", "hotkey", "[input=" + Control.TRINKET_ABILITY.id + "]"));
      }

      return var1;
   }
}
