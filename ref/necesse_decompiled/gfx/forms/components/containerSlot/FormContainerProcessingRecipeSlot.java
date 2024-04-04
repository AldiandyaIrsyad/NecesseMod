package necesse.gfx.forms.components.containerSlot;

import java.awt.Color;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;

public class FormContainerProcessingRecipeSlot extends FormContainerSlot {
   protected ProcessingHelp help;

   public FormContainerProcessingRecipeSlot(Client var1, Container var2, int var3, int var4, int var5, ProcessingHelp var6) {
      super(var1, var2, var3, var4, var5);
      this.help = var6;
   }

   public void drawDecal(PlayerMob var1) {
      super.drawDecal(var1);
      if (this.help != null) {
         InventoryItem var2 = this.getContainerSlot().getItem();
         if (var2 == null) {
            InventoryItem var3 = this.help.getGhostItem(this.getContainerSlot().getInventorySlot());
            if (var3 != null) {
               GameSprite var4 = var3.item.getItemSprite(var3, var1);
               Color var5 = var3.item.getDrawColor(var3, var1);
               var4.initDraw().size(32).color(var5).alpha(0.25F).draw(this.getX() + 4, this.getY() + 4);
            }
         }
      }

   }

   public void addItemTooltips(InventoryItem var1, PlayerMob var2) {
      if (this.help != null) {
         GameTooltips var3 = this.help.getTooltip(this.getContainerSlot().getInventorySlot(), var2);
         if (var3 != null) {
            ListGameTooltips var4 = new ListGameTooltips(var3);
            if (this.help.needsFuel()) {
               var4.add((Object)(new StringTooltips(Localization.translate("ui", "needfuel"), GameColor.RED)));
            }

            Screen.addTooltip(var4, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
         } else {
            super.addItemTooltips(var1, var2);
         }
      } else {
         super.addItemTooltips(var1, var2);
      }

   }

   public void addClearTooltips(PlayerMob var1) {
      if (this.help != null) {
         GameTooltips var2 = this.help.getTooltip(this.getContainerSlot().getInventorySlot(), var1);
         if (var2 != null) {
            ListGameTooltips var3 = new ListGameTooltips(var2);
            if (this.help.needsFuel()) {
               var3.add((Object)(new StringTooltips(Localization.translate("ui", "needfuel"), GameColor.RED)));
            }

            Screen.addTooltip(var3, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
         }
      }

      super.addClearTooltips(var1);
   }
}
