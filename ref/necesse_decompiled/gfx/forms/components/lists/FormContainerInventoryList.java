package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;

public class FormContainerInventoryList extends FormGeneralGridList<ContainerSlotElement> {
   protected Client client;

   public FormContainerInventoryList(int var1, int var2, int var3, int var4, Client var5) {
      super(var1, var2, var3, var4, 40, 40);
      this.client = var5;
   }

   public FormContainerInventoryList(int var1, int var2, int var3, int var4, Client var5, int var6, int var7) {
      super(var1, var2, var3, var4, 40, 40);
      this.client = var5;
      this.addSlots(var6, var7);
   }

   public void addSlots(int var1, int var2) {
      for(int var3 = var1; var3 <= var2; ++var3) {
         this.addSlot(var3);
      }

   }

   public void addSlot(int var1) {
      this.elements.add(new ContainerSlotElement(var1));
   }

   public Container getContainer() {
      return this.client.getContainer();
   }

   public ContainerSlotElement getSlotElement(int var1) {
      return (ContainerSlotElement)this.elements.get(var1);
   }

   public ContainerSlotElement getSlotElementByContainerIndex(int var1) {
      Iterator var2 = this.elements.iterator();

      ContainerSlotElement var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (ContainerSlotElement)var2.next();
      } while(var3.containerSlotIndex != var1);

      return var3;
   }

   public class ContainerSlotElement extends FormListGridElement<FormContainerInventoryList> {
      public final int containerSlotIndex;
      private GameSprite decal;

      public ContainerSlotElement(int var2) {
         this.containerSlotIndex = var2;
      }

      void draw(FormContainerInventoryList var1, TickManager var2, PlayerMob var3, int var4) {
         ContainerSlot var5 = this.getContainerSlot();
         if (this.isMouseOver(var1) && !var5.isClear()) {
            if (Control.INV_QUICK_TRASH.isDown() && !var5.isItemLocked()) {
               Screen.setCursor(Screen.CURSOR.TRASH);
            } else if (Control.INV_LOCK.isDown() && var5.canLockItem()) {
               if (var5.isItemLocked()) {
                  Screen.setCursor(Screen.CURSOR.UNLOCK);
               } else {
                  Screen.setCursor(Screen.CURSOR.LOCK);
               }
            }
         }

         Color var6 = Settings.UI.activeElementColor;
         if (this.isMouseOver(var1)) {
            var6 = Settings.UI.highlightElementColor;
         }

         InventoryItem var7 = this.getContainerSlot().getItem();
         HoverStateTextures var8 = var7 != null && var7.isNew() ? Settings.UI.inventoryslot_big_new : Settings.UI.inventoryslot_big;
         GameTexture var9 = this.isMouseOver(var1) ? var8.highlighted : var8.active;
         var9.initDraw().color(var6).draw(0, 0);
         if (this.decal != null) {
            this.decal.initDraw().color(var6).draw(20 + this.decal.width / 2, 20 + this.decal.height / 2);
         }

         if (!var5.isClear()) {
            var5.getItem().draw(var3, 4, 4);
            if (this.isMouseOver(var1) && !Screen.input().isKeyDown(-100) && !Screen.input().isKeyDown(-99)) {
               Screen.addTooltip(var5.getItem().getTooltip(var3, new GameBlackboard()), GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            }
         }

      }

      void onClick(FormContainerInventoryList var1, int var2, InputEvent var3, PlayerMob var4) {
         ContainerAction var5 = null;
         if (var3.getID() == -100) {
            if (Control.INV_QUICK_MOVE.isDown()) {
               var5 = ContainerAction.QUICK_MOVE;
            } else if (Control.INV_QUICK_TRASH.isDown()) {
               var5 = ContainerAction.QUICK_TRASH;
            } else if (Control.INV_LOCK.isDown()) {
               var5 = ContainerAction.TOGGLE_LOCKED;
            } else {
               var5 = ContainerAction.LEFT_CLICK;
            }
         } else if (var3.getID() == -99) {
            if (Control.INV_QUICK_MOVE.isDown()) {
               ContainerSlot var6 = this.getContainerSlot();
               InventoryItem var7 = var6.getItem();
               int var8 = var7 == null ? -1 : var7.item.getID();
               if (var3.getID() == -99 || var3.isRepeatEvent(this, ContainerAction.TAKE_ONE, var8)) {
                  if (var8 != -1) {
                     var3.startRepeatEvents(this, ContainerAction.TAKE_ONE, var8);
                  }

                  var5 = ContainerAction.TAKE_ONE;
               }
            } else if (Control.INV_QUICK_TRASH.isDown()) {
               var5 = ContainerAction.QUICK_TRASH_ONE;
            } else if (Control.INV_LOCK.isDown()) {
               var5 = ContainerAction.TOGGLE_LOCKED;
            } else {
               var5 = ContainerAction.RIGHT_CLICK;
               if (!this.getContainerSlot().isClear()) {
                  InventoryItem var9 = this.getContainerSlot().getItem();
                  Supplier var11 = var9.item.getInventoryRightClickAction(FormContainerInventoryList.this.getContainer(), var9, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                  if (var11 != null) {
                     var5 = ContainerAction.RIGHT_CLICK_ACTION;
                  }
               }
            }
         } else if (var3.getID() == -102) {
            var5 = ContainerAction.QUICK_MOVE_ONE;
         } else if (var3.getID() == -103) {
            var5 = ContainerAction.QUICK_GET_ONE;
         }

         if (var5 != null) {
            ContainerActionResult var10 = FormContainerInventoryList.this.getContainer().applyContainerAction(this.containerSlotIndex, var5);
            FormContainerInventoryList.this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, var5, var10.value));
            if (var10.error != null) {
               Screen.hudManager.addElement(new UniqueScreenFloatText(Screen.mousePos().hudX, Screen.mousePos().hudY, var10.error, (new FontOptions(16)).outline(), "slotError"));
            }

            if ((var10.value != 0 || var10.error != null) && var3.shouldSubmitSound()) {
               FormContainerInventoryList.this.playTickSound();
            }
         }

      }

      void onControllerEvent(FormContainerInventoryList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            this.runAction(ContainerAction.LEFT_CLICK, var3.shouldSubmitSound());
            var3.use();
         } else if (var3.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU) {
            if (!this.getContainerSlot().isClear()) {
               ControllerFocus var6 = FormContainerInventoryList.this.getManager().getCurrentFocus();
               if (var6 != null) {
                  InventoryItem var7 = this.getContainerSlot().getItem();
                  SelectionFloatMenu var8 = new SelectionFloatMenu(var1) {
                     public void draw(TickManager var1, PlayerMob var2) {
                        if (!FormContainerInventoryList.this.client.getPlayer().isInventoryExtended()) {
                           this.remove();
                        }

                        super.draw(var1, var2);
                     }
                  };
                  var8.add(Localization.translate("ui", "slottransfer"), () -> {
                     this.runAction(ContainerAction.QUICK_MOVE, false);
                     var8.remove();
                  });
                  Supplier var9 = var7.item.getInventoryRightClickAction(FormContainerInventoryList.this.getContainer(), var7, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                  if (var9 != null) {
                     String var10 = var7.item.getInventoryRightClickControlTip(FormContainerInventoryList.this.getContainer(), var7, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                     if (var10 != null) {
                        var8.add(var10, () -> {
                           this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                           var8.remove();
                        });
                     } else {
                        var8.add(Localization.translate("ui", "slotuse"), () -> {
                           this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                           var8.remove();
                        });
                     }
                  } else {
                     var8.add(Localization.translate("ui", "slotsplit"), () -> {
                        this.runAction(ContainerAction.RIGHT_CLICK, false);
                        var8.remove();
                     });
                  }

                  var8.add(Localization.translate("ui", this.getContainerSlot().isItemLocked() ? "slotunlock" : "slotlock"), () -> {
                     this.runAction(ContainerAction.TOGGLE_LOCKED, false);
                     var8.remove();
                  });
                  if (!this.getContainerSlot().isItemLocked()) {
                     var8.add(Localization.translate("ui", "slottrash"), () -> {
                        this.runAction(ContainerAction.QUICK_TRASH, false);
                        var8.remove();
                     });
                  }

                  var8.add(Localization.translate("ui", "slottakeone"), () -> {
                     this.runAction(ContainerAction.TAKE_ONE, false);
                     var8.remove();
                  });
                  if (!this.getContainerSlot().isItemLocked()) {
                     var8.add(Localization.translate("ui", "slotdrop"), () -> {
                        this.runAction(ContainerAction.QUICK_DROP, false);
                        var8.remove();
                     });
                  }

                  FormContainerInventoryList.this.getManager().openFloatMenuAt(var8, var6.boundingBox.x, var6.boundingBox.y + var6.boundingBox.height);
                  FormContainerInventoryList.this.playTickSound();
               }
            }

            var3.use();
         } else if (var3.getState() == ControllerInput.MENU_INTERACT_ITEM) {
            if (!this.getContainerSlot().isClear()) {
               InventoryItem var11 = this.getContainerSlot().getItem();
               Supplier var12 = var11.item.getInventoryRightClickAction(FormContainerInventoryList.this.getContainer(), var11, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
               if (var12 != null) {
                  this.runAction(ContainerAction.RIGHT_CLICK_ACTION, var3.shouldSubmitSound());
               } else {
                  this.runAction(ContainerAction.RIGHT_CLICK, var3.shouldSubmitSound());
               }
            } else {
               this.runAction(ContainerAction.RIGHT_CLICK, var3.shouldSubmitSound());
            }

            var3.use();
         } else if (var3.getState() == ControllerInput.MENU_QUICK_TRANSFER) {
            this.runAction(ContainerAction.QUICK_MOVE, var3.shouldSubmitSound());
            var3.use();
         } else if (var3.getState() == ControllerInput.MENU_QUICK_TRASH) {
            this.runAction(ContainerAction.QUICK_TRASH, var3.shouldSubmitSound());
            var3.use();
         } else if (var3.getState() == ControllerInput.MENU_DROP_ITEM) {
            this.runAction(ContainerAction.QUICK_DROP, var3.shouldSubmitSound());
            var3.use();
         } else if (var3.getState() == ControllerInput.MENU_LOCK_ITEM) {
            this.runAction(ContainerAction.TOGGLE_LOCKED, var3.shouldSubmitSound());
            var3.use();
         } else if (var3.getState() == ControllerInput.MENU_MOVE_ONE_ITEM) {
            this.runAction(ContainerAction.QUICK_MOVE_ONE, var3.shouldSubmitSound());
            var3.use();
         } else if (var3.getState() == ControllerInput.MENU_GET_ONE_ITEM) {
            this.runAction(ContainerAction.QUICK_GET_ONE, var3.shouldSubmitSound());
            var3.use();
         }

      }

      protected void runAction(ContainerAction var1, boolean var2) {
         ContainerActionResult var3 = FormContainerInventoryList.this.getContainer().applyContainerAction(this.containerSlotIndex, var1);
         FormContainerInventoryList.this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, var1, var3.value));
         if (var3.error != null) {
            ControllerFocus var4 = FormContainerInventoryList.this.getManager().getCurrentFocus();
            Screen.hudManager.addElement(new UniqueScreenFloatText(var4.boundingBox.x + var4.boundingBox.width / 2, var4.boundingBox.y, var3.error, (new FontOptions(16)).outline(), "slotError"));
         }

         if ((var3.value != 0 || var3.error != null) && var2) {
            FormContainerInventoryList.this.playTickSound();
         }

      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
         Screen.addControllerGlyph(Localization.translate("ui", "slotactions"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
      }

      public boolean isMouseOver(FormContainerInventoryList var1) {
         if (var1.isControllerFocus(this)) {
            return true;
         } else {
            InputEvent var2 = this.getMoveEvent();
            return var2 == null ? false : (new Rectangle(2, 2, var1.elementWidth - 4, var1.elementHeight - 4)).contains(var2.pos.hudX, var2.pos.hudY);
         }
      }

      public ContainerSlot getContainerSlot() {
         return FormContainerInventoryList.this.getContainer().getSlot(this.containerSlotIndex);
      }

      public void setDecal(GameSprite var1) {
         this.decal = var1;
      }

      public void setDecal(GameTexture var1) {
         this.setDecal(new GameSprite(var1));
      }

      // $FF: synthetic method
      // $FF: bridge method
      public boolean isMouseOver(FormGeneralGridList var1) {
         return this.isMouseOver((FormContainerInventoryList)var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      public boolean isMouseOver(FormGeneralList var1) {
         return this.isMouseOver((FormContainerInventoryList)var1);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormContainerInventoryList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormContainerInventoryList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormContainerInventoryList)var1, var2, var3, var4);
      }
   }
}
