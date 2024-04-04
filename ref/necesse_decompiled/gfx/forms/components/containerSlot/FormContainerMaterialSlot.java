package necesse.gfx.forms.components.containerSlot;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;

public class FormContainerMaterialSlot extends FormContainerSlot {
   public InventoryItem ghostItem;

   public FormContainerMaterialSlot(Client var1, Container var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
   }

   protected void handleActionControllerEvents(ControllerEvent var1) {
      if (this.ghostItem != null && this.getContainer().getClientDraggingSlot().getItem() == null) {
         if (this.isControllerFocus() && var1.getState() == ControllerInput.MENU_SELECT) {
            if (var1.buttonState) {
               this.ghostItem = null;
               this.playTickSound();
            }

            var1.use();
         }
      } else {
         super.handleActionControllerEvents(var1);
      }

   }

   protected void handleActionInputEvents(InputEvent var1) {
      if (this.ghostItem != null && this.getContainer().getClientDraggingSlot().getItem() == null) {
         if (this.isMouseOver(var1) && var1.isMouseClickEvent()) {
            if (var1.state) {
               this.ghostItem = null;
               this.playTickSound();
            }

            var1.use();
         }
      } else {
         super.handleActionInputEvents(var1);
      }

   }

   public GameTooltips getClearTooltips() {
      return this.ghostItem != null ? null : new StringTooltips(Localization.translate("itemtooltip", "materialslot"));
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      super.draw(var1, var2, var3);
      InventoryItem var4 = this.getContainerSlot().getItem();
      if (var4 == null) {
         if (this.ghostItem != null) {
            GameSprite var5 = this.ghostItem.item.getItemSprite(this.ghostItem, var2);
            if (var5 != null) {
               Color var6 = this.ghostItem.item.getDrawColor(this.ghostItem, var2);
               var5.initDraw().color(var6).alpha(0.5F).size(32).draw(this.getX() + 4, this.getY() + 4);
            } else {
               this.ghostItem.item.draw(this.ghostItem, var2, this.getX() + 4, this.getY() + 4, false);
            }

            if (this.isHovering() && !Screen.input().isKeyDown(-100) && !Screen.input().isKeyDown(-99)) {
               this.addItemTooltips(this.ghostItem, var2);
            }
         }
      } else {
         this.ghostItem = null;
      }

   }
}
