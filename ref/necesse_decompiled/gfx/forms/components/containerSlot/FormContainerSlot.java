package necesse.gfx.forms.components.containerSlot;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.Control;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.screenHudManager.UniqueScreenFloatText;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerAction;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;

public class FormContainerSlot extends FormComponent implements FormPositionContainer {
   private FormPosition position;
   private boolean active;
   protected Client client;
   protected Container container;
   protected int containerSlotIndex;
   public boolean isSelected;
   protected GameSprite decal;
   public boolean drawDecalWhenOccupied;
   private boolean isHovering;

   public FormContainerSlot(Client var1, Container var2, int var3, int var4, int var5) {
      this.drawDecalWhenOccupied = false;
      this.client = var1;
      this.container = var2;
      this.containerSlotIndex = var3;
      if (var2 != null && this.getContainerSlot() == null) {
         throw new IllegalArgumentException("Container slot with index " + var3 + " does not exist in container");
      } else {
         this.position = new FormFixedPosition(var4, var5);
         this.setActive(true);
      }
   }

   /** @deprecated */
   @Deprecated
   public FormContainerSlot(Client var1, int var2, int var3, int var4) {
      this(var1, (Container)null, var2, var3, var4);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      this.handleMouseMoveEvent(var1);
      this.handleActionInputEvents(var1);
   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      this.handleActionControllerEvents(var1);
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
   }

   protected void handleMouseMoveEvent(InputEvent var1) {
      if (var1.isMouseMoveEvent()) {
         this.isHovering = this.isMouseOver(var1);
         if (this.isHovering) {
            var1.useMove();
         }
      }

   }

   protected void handleActionInputEvents(InputEvent var1) {
      if (var1.state && !var1.isKeyboardEvent()) {
         ContainerAction var2 = null;
         if (var1.getID() == -100) {
            if (this.isMouseOver(var1) && (!Screen.isKeyDown(340) || !FormTypingComponent.appendItemToTyping(this.getContainerSlot().getItem()))) {
               if (Control.INV_QUICK_MOVE.isDown()) {
                  var2 = ContainerAction.QUICK_MOVE;
               } else if (Control.INV_QUICK_TRASH.isDown() && this.canCurrentlyQuickTrash()) {
                  var2 = ContainerAction.QUICK_TRASH;
               } else if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem()) {
                  var2 = ContainerAction.TOGGLE_LOCKED;
               } else {
                  var2 = ContainerAction.LEFT_CLICK;
               }
            }
         } else if (var1.getID() != -99 && !var1.isRepeatEvent((Object)this)) {
            if (var1.getID() == -102) {
               if (this.isMouseOver(var1)) {
                  var2 = ContainerAction.QUICK_MOVE_ONE;
               }
            } else if (var1.getID() == -103 && this.isMouseOver(var1)) {
               var2 = ContainerAction.QUICK_GET_ONE;
            }
         } else if (this.isMouseOver(var1)) {
            if (Control.INV_QUICK_MOVE.isDown()) {
               ContainerSlot var3 = this.getContainerSlot();
               InventoryItem var4 = var3.getItem();
               int var5 = var4 == null ? -1 : var4.item.getID();
               if (var1.getID() == -99 || var1.isRepeatEvent(this, ContainerAction.TAKE_ONE, var5)) {
                  if (var5 != -1) {
                     var1.startRepeatEvents(this, ContainerAction.TAKE_ONE, var5);
                  }

                  var2 = ContainerAction.TAKE_ONE;
               }
            } else if (Control.INV_QUICK_TRASH.isDown() && this.canCurrentlyQuickTrash()) {
               var2 = ContainerAction.QUICK_TRASH_ONE;
            } else if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem()) {
               var2 = ContainerAction.TOGGLE_LOCKED;
            } else {
               var2 = ContainerAction.RIGHT_CLICK;
               if (!this.getContainerSlot().isClear()) {
                  InventoryItem var6 = this.getContainerSlot().getItem();
                  Supplier var8 = var6.item.getInventoryRightClickAction(this.getContainer(), var6, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                  if (var8 != null) {
                     var2 = ContainerAction.RIGHT_CLICK_ACTION;
                  }
               }
            }
         }

         if (var2 != null) {
            ContainerActionResult var7 = this.getContainer().applyContainerAction(this.containerSlotIndex, var2);
            this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, var2, var7.value));
            if (var7.error != null) {
               Screen.hudManager.addElement(new UniqueScreenFloatText(Screen.mousePos().hudX, Screen.mousePos().hudY, var7.error, (new FontOptions(16)).outline(), "slotError"));
            }

            if ((var7.value != 0 || var7.error != null) && var1.shouldSubmitSound()) {
               this.playTickSound();
            }
         }

      }
   }

   protected void handleActionControllerEvents(ControllerEvent var1) {
      if (var1.buttonState) {
         if (this.isControllerFocus()) {
            if (var1.getState() == ControllerInput.MENU_SELECT) {
               this.runAction(ContainerAction.LEFT_CLICK, var1.shouldSubmitSound());
               var1.use();
            } else if (var1.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU) {
               if (!this.getContainerSlot().isClear()) {
                  ControllerFocus var2 = this.getManager().getCurrentFocus();
                  if (var2 != null) {
                     InventoryItem var3 = this.getContainerSlot().getItem();
                     SelectionFloatMenu var4 = new SelectionFloatMenu(this) {
                        public void draw(TickManager var1, PlayerMob var2) {
                           if (!FormContainerSlot.this.client.getPlayer().isInventoryExtended()) {
                              this.remove();
                           }

                           super.draw(var1, var2);
                        }
                     };
                     var4.add(Localization.translate("ui", "slottransfer"), () -> {
                        this.runAction(ContainerAction.QUICK_MOVE, false);
                        var4.remove();
                     });
                     Supplier var5 = var3.item.getInventoryRightClickAction(this.getContainer(), var3, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                     if (var5 != null) {
                        String var6 = var3.item.getInventoryRightClickControlTip(this.getContainer(), var3, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                        if (var6 != null) {
                           var4.add(var6, () -> {
                              this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                              var4.remove();
                           });
                        } else {
                           var4.add(Localization.translate("ui", "slotuse"), () -> {
                              this.runAction(ContainerAction.RIGHT_CLICK_ACTION, false);
                              var4.remove();
                           });
                        }
                     } else {
                        var4.add(Localization.translate("ui", "slotsplit"), () -> {
                           this.runAction(ContainerAction.RIGHT_CLICK, false);
                           var4.remove();
                        });
                     }

                     var4.add(Localization.translate("ui", this.getContainerSlot().isItemLocked() ? "slotunlock" : "slotlock"), () -> {
                        this.runAction(ContainerAction.TOGGLE_LOCKED, false);
                        var4.remove();
                     });
                     if (!this.getContainerSlot().isItemLocked()) {
                        var4.add(Localization.translate("ui", "slottrash"), () -> {
                           this.runAction(ContainerAction.QUICK_TRASH, false);
                           var4.remove();
                        });
                     }

                     var4.add(Localization.translate("ui", "slottakeone"), () -> {
                        this.runAction(ContainerAction.TAKE_ONE, false);
                        var4.remove();
                     });
                     if (!this.getContainerSlot().isItemLocked()) {
                        var4.add(Localization.translate("ui", "slotdrop"), () -> {
                           this.runAction(ContainerAction.QUICK_DROP, false);
                           var4.remove();
                        });
                     }

                     this.getManager().openFloatMenuAt(var4, var2.boundingBox.x, var2.boundingBox.y + var2.boundingBox.height);
                     this.playTickSound();
                  }
               }

               var1.use();
            } else if (var1.getState() == ControllerInput.MENU_INTERACT_ITEM) {
               if (!this.getContainerSlot().isClear()) {
                  InventoryItem var7 = this.getContainerSlot().getItem();
                  Supplier var8 = var7.item.getInventoryRightClickAction(this.getContainer(), var7, this.getContainerSlot().getContainerIndex(), this.getContainerSlot());
                  if (var8 != null) {
                     this.runAction(ContainerAction.RIGHT_CLICK_ACTION, var1.shouldSubmitSound());
                  } else {
                     this.runAction(ContainerAction.RIGHT_CLICK, var1.shouldSubmitSound());
                  }
               } else {
                  this.runAction(ContainerAction.RIGHT_CLICK, var1.shouldSubmitSound());
               }

               var1.use();
            } else if (var1.getState() == ControllerInput.MENU_QUICK_TRANSFER) {
               this.runAction(ContainerAction.QUICK_MOVE, var1.shouldSubmitSound());
               var1.use();
            } else if (var1.getState() == ControllerInput.MENU_QUICK_TRASH) {
               this.runAction(ContainerAction.QUICK_TRASH, var1.shouldSubmitSound());
               var1.use();
            } else if (var1.getState() == ControllerInput.MENU_DROP_ITEM) {
               this.runAction(ContainerAction.QUICK_DROP, var1.shouldSubmitSound());
               var1.use();
            } else if (var1.getState() == ControllerInput.MENU_LOCK_ITEM) {
               this.runAction(ContainerAction.TOGGLE_LOCKED, var1.shouldSubmitSound());
               var1.use();
            } else if (var1.getState() == ControllerInput.MENU_MOVE_ONE_ITEM) {
               this.runAction(ContainerAction.QUICK_MOVE_ONE, var1.shouldSubmitSound());
               var1.use();
            } else if (var1.getState() == ControllerInput.MENU_GET_ONE_ITEM) {
               this.runAction(ContainerAction.QUICK_GET_ONE, var1.shouldSubmitSound());
               var1.use();
            }
         }

      }
   }

   protected void runAction(ContainerAction var1, boolean var2) {
      ContainerActionResult var3 = this.getContainer().applyContainerAction(this.containerSlotIndex, var1);
      this.client.network.sendPacket(new PacketContainerAction(this.containerSlotIndex, var1, var3.value));
      if (var3.error != null) {
         ControllerFocus var4 = this.getManager().getCurrentFocus();
         Screen.hudManager.addElement(new UniqueScreenFloatText(var4.boundingBox.x + var4.boundingBox.width / 2, var4.boundingBox.y, var3.error, (new FontOptions(16)).outline(), "slotError"));
      }

      if ((var3.value != 0 || var3.error != null) && var2) {
         this.playTickSound();
      }

   }

   public boolean canCurrentlyLockItem() {
      return true;
   }

   public boolean canCurrentlyQuickTrash() {
      return true;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.isHovering() && !this.getContainerSlot().isClear()) {
         if (Control.INV_QUICK_TRASH.isDown() && this.canCurrentlyQuickTrash() && !this.getContainerSlot().isItemLocked()) {
            Screen.setCursor(Screen.CURSOR.TRASH);
         } else if (Control.INV_LOCK.isDown() && this.canCurrentlyLockItem() && this.getContainerSlot().canLockItem()) {
            if (this.getContainerSlot().isItemLocked()) {
               Screen.setCursor(Screen.CURSOR.UNLOCK);
            } else {
               Screen.setCursor(Screen.CURSOR.LOCK);
            }
         }
      }

      Color var4 = this.getDrawColor();
      InventoryItem var5 = this.getContainerSlot().getItem();
      HoverStateTextures var6 = var5 != null && var5.isNew() ? Settings.UI.inventoryslot_big_new : Settings.UI.inventoryslot_big;
      GameTexture var7 = this.isHovering() ? var6.highlighted : var6.active;
      var7.initDraw().color(var4).draw(this.getX(), this.getY());
      this.drawDecal(var2);
      if (var5 != null) {
         var5.draw(var2, this.getX() + 4, this.getY() + 4);
         if (this.isHovering()) {
            var5.setNew(false);
            if (!Screen.input().isKeyDown(-100) && !Screen.input().isKeyDown(-99)) {
               this.addItemTooltips(var5, var2);
            }
         }
      } else if (this.isHovering()) {
         this.addClearTooltips(var2);
      }

      if (this.getContainerSlot().isItemLocked()) {
         Settings.UI.note_locked.initDraw().draw(this.getX() + 5, this.getY() + 35 - Settings.UI.note_locked.getHeight());
      }

   }

   public void drawControllerFocus(ControllerFocus var1) {
      super.drawControllerFocus(var1);
      Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      Screen.addControllerGlyph(Localization.translate("ui", "slotactions"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
   }

   public Color getDecalDrawColor() {
      if (!this.isActive()) {
         return Settings.UI.inactiveFadedTextColor;
      } else if (this.getContainer().isSlotLocked(this.getContainerSlot())) {
         return Settings.UI.inactiveFadedTextColor;
      } else {
         return !this.isHovering() && !this.isSelected ? Settings.UI.activeFadedTextColor : Settings.UI.highlightFadedTextColor;
      }
   }

   public void drawDecal(PlayerMob var1) {
      if (this.decal != null) {
         if (!this.drawDecalWhenOccupied && !this.getContainerSlot().isClear()) {
            return;
         }

         this.decal.initDraw().color(this.getDecalDrawColor()).draw(this.getX() + 20 - this.decal.width / 2, this.getY() + 20 - this.decal.height / 2);
      }

   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX() + 2, this.getY() + 2, 36, 36));
   }

   public GameTooltips getItemTooltip(InventoryItem var1, PlayerMob var2) {
      return var1.getTooltip(var2, new GameBlackboard());
   }

   public void addItemTooltips(InventoryItem var1, PlayerMob var2) {
      ListGameTooltips var3 = new ListGameTooltips();
      var3.add((Object)this.getItemTooltip(var1, var2));
      if (Settings.showControlTips) {
         String var4 = var1.item.getInventoryRightClickControlTip(this.getContainer(), var1, this.containerSlotIndex, this.getContainerSlot());
         if (var4 != null) {
            if (Input.lastInputIsController) {
               var3.add((Object)(new InputTooltip(ControllerInput.MENU_INTERACT_ITEM, var4)));
            } else {
               var3.add((Object)(new InputTooltip(-99, var4)));
            }
         }
      }

      Screen.addTooltip(var3, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
   }

   public GameTooltips getClearTooltips() {
      return null;
   }

   public void addClearTooltips(PlayerMob var1) {
      GameTooltips var2 = this.getClearTooltips();
      if (var2 != null) {
         Screen.addTooltip(var2, TooltipLocation.FORM_FOCUS);
      }

   }

   public Color getDrawColor() {
      if (!this.isActive()) {
         return Settings.UI.inactiveElementColor;
      } else if (this.getContainer().isSlotLocked(this.getContainerSlot())) {
         return Settings.UI.inactiveElementColor;
      } else {
         return !this.isHovering() && !this.isSelected ? Settings.UI.activeElementColor : Settings.UI.highlightElementColor;
      }
   }

   public FormContainerSlot setDecal(GameSprite var1) {
      this.decal = var1;
      return this;
   }

   public FormContainerSlot setDecal(GameTexture var1) {
      this.setDecal(new GameSprite(var1));
      return this;
   }

   public Container getContainer() {
      return this.container != null ? this.container : this.client.getContainer();
   }

   public ContainerSlot getContainerSlot() {
      return this.getContainer().getSlot(this.containerSlotIndex);
   }

   public boolean isHovering() {
      return this.isHovering || this.isControllerFocus();
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean var1) {
      this.active = var1;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
