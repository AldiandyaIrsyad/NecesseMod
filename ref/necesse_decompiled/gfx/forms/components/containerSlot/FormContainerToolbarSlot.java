package necesse.gfx.forms.components.containerSlot;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;

public class FormContainerToolbarSlot extends FormContainerSlot {
   private PlayerMob player;

   public FormContainerToolbarSlot(Client var1, Container var2, int var3, int var4, int var5, PlayerMob var6) {
      super(var1, var2, var3, var4, var5);
      this.player = var6;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      this.handleMouseMoveEvent(var1);
      if (this.player.isInventoryExtended()) {
         if (var1.state) {
            this.handleActionInputEvents(var1);
         }
      } else if (!this.player.hotbarLocked && var1.state) {
         ContainerAction var4 = null;
         if (var1.getID() == -100 && this.isMouseOver(var1)) {
            if (!Screen.isKeyDown(340) || !FormTypingComponent.appendItemToTyping(this.getContainerSlot().getItem())) {
               if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem()) {
                  var4 = ContainerAction.TOGGLE_LOCKED;
               } else {
                  this.player.setSelectedSlot(this.getContainerSlot().getInventorySlot());
               }
            }

            var1.use();
         } else if (var1.getID() == -99 && this.isMouseOver(var1)) {
            if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem()) {
               var4 = ContainerAction.TOGGLE_LOCKED;
            } else {
               var4 = ContainerAction.RIGHT_CLICK_ACTION;
            }

            var1.use();
         }

         if (var4 != null) {
            ContainerActionResult var5 = this.getContainer().applyContainerAction(this.containerSlotIndex, var4);
            this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, var4, var5.value));
            if (var5.error != null) {
               Screen.hudManager.addElement(new UniqueScreenFloatText(Screen.mousePos().hudX, Screen.mousePos().hudY, var5.error, (new FontOptions(16)).outline(), "slotError"));
            }

            if ((var5.value != 0 || var5.error != null) && var1.shouldSubmitSound()) {
               this.playTickSound();
            }
         }
      }

   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (this.player.isInventoryExtended()) {
         ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
      }

   }

   public boolean canCurrentlyQuickTrash() {
      return this.player.isInventoryExtended();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.isSelected = this.player.getSelectedSlot() == this.getContainerSlot().getInventorySlot();
      super.draw(var1, var2, var3);
   }

   public void drawDecal(PlayerMob var1) {
      super.drawDecal(var1);
      int var2 = this.getContainerSlot().getInventorySlot();
      String var3 = Integer.toString((var2 + 1) % 10);
      FontManager.bit.drawString((float)(this.getX() + 4), (float)(this.getY() + 4), var3, (new FontOptions(10)).color(Settings.UI.inactiveButtonTextColor).forceNonPixelFont());
   }

   public Color getDrawColor() {
      if (this.player.getSelectedSlot() == this.getContainerSlot().getInventorySlot()) {
         return Settings.UI.highlightElementColor;
      } else {
         return !this.player.isInventoryExtended() ? Settings.UI.unfocusedElementColor : super.getDrawColor();
      }
   }
}
