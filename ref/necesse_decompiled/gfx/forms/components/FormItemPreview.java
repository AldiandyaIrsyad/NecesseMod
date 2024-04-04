package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.InputEvent;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class FormItemPreview extends FormComponent implements FormPositionContainer {
   public boolean useHoverMoveEvents;
   private boolean isHovering;
   protected FormEventsHandler<FormInputEvent<FormItemPreview>> changedHoverEvents;
   private FormPosition position;
   private InventoryItem item;
   public int size;

   public FormItemPreview(int var1, int var2, int var3, Item var4) {
      this.useHoverMoveEvents = true;
      this.position = new FormFixedPosition(var1, var2);
      this.size = var3;
      this.changedHoverEvents = new FormEventsHandler();
      this.setItem(var4);
   }

   public FormItemPreview(int var1, int var2, int var3, String var4) {
      this(var1, var2, var3, ItemRegistry.getItem(var4));
   }

   public FormItemPreview(int var1, int var2, Item var3) {
      this(var1, var2, 32, (Item)var3);
   }

   public FormItemPreview(int var1, int var2, String var3) {
      this(var1, var2, 32, (String)var3);
   }

   public FormItemPreview onChangedHover(FormEventListener<FormInputEvent<FormItemPreview>> var1) {
      this.changedHoverEvents.addListener(var1);
      return this;
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (var1.isMouseMoveEvent()) {
         boolean var4 = this.isMouseOver(var1);
         if (this.isHovering != var4) {
            this.isHovering = var4;
            FormInputEvent var5 = new FormInputEvent(this, var1);
            this.changedHoverEvents.onEvent(var5);
         }

         if (var4 && this.useHoverMoveEvents) {
            var1.useMove();
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
   }

   public void addNextControllerFocus(List<ControllerFocus> var1, int var2, int var3, ControllerNavigationHandler var4, Rectangle var5, boolean var6) {
      if (this.allowControllerFocus()) {
         ControllerFocus.add(var1, var5, this, this.getBoundingBox(), var2, var3, this.controllerInitialFocusPriority, var4);
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      InventoryItem var4 = this.getDrawItem(var2);
      if (var4 != null) {
         var4.drawIcon(var2, this.getX(), this.getY(), this.size);
      } else {
         this.drawEmpty(var1, var2, var3);
      }

      if (this.isHovering()) {
         this.addTooltips(var4, var2);
      }

   }

   public void drawEmpty(TickManager var1, PlayerMob var2, Rectangle var3) {
   }

   public boolean isHovering() {
      return this.isHovering || this.isControllerFocus();
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.size, this.size));
   }

   public boolean allowControllerFocus() {
      return false;
   }

   public InventoryItem getDrawItem(PlayerMob var1) {
      return this.item;
   }

   public void addTooltips(InventoryItem var1, PlayerMob var2) {
   }

   public FormItemPreview setItem(Item var1) {
      if (var1 != null) {
         this.item = new InventoryItem(var1);
      } else {
         this.item = null;
      }

      return this;
   }

   public FormItemPreview setSize(int var1) {
      this.size = var1;
      return this;
   }

   public FormPosition getPosition() {
      return this.position;
   }

   public void setPosition(FormPosition var1) {
      this.position = var1;
   }
}
