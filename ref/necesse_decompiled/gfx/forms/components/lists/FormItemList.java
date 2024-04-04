package necesse.gfx.forms.components.lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

public abstract class FormItemList extends FormGeneralGridList<ItemElement> {
   protected LinkedList<InventoryItem> allItems;
   protected Predicate<InventoryItem> filter = null;
   protected boolean sorted = true;

   public FormItemList(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, 36, 36);
      this.reset();
   }

   public void reset() {
      this.allItems = new LinkedList();
      this.resetScroll();
   }

   public void populateIfNotAlready() {
      if (this.allItems.isEmpty()) {
         this.addAllItems(this.allItems);
         this.setFilter(this.filter, false);
         this.resetScroll();
      }

   }

   public abstract void addAllItems(List<InventoryItem> var1);

   public void setFilter(Predicate<InventoryItem> var1) {
      this.setFilter(var1, true);
   }

   private void setFilter(Predicate<InventoryItem> var1, boolean var2) {
      this.filter = var1;
      this.updateList(var2);
   }

   public void setSorted(boolean var1) {
      if (this.sorted != var1) {
         this.sorted = var1;
         this.updateList(true);
      }

   }

   protected void updateList(boolean var1) {
      this.elements = new ArrayList();
      Iterator var2 = this.allItems.iterator();

      while(true) {
         InventoryItem var3;
         do {
            if (!var2.hasNext()) {
               if (this.sorted) {
                  this.elements.sort(Comparator.comparing((var0) -> {
                     return var0.item;
                  }));
               }

               if (var1) {
                  this.limitMaxScroll();
               }

               return;
            }

            var3 = (InventoryItem)var2.next();
         } while(this.filter != null && !this.filter.test(var3));

         this.elements.add(new ItemElement(var3));
      }
   }

   public abstract void onItemClicked(InventoryItem var1, InputEvent var2);

   public void addTooltips(InventoryItem var1, PlayerMob var2) {
      Screen.addTooltip(var1.getTooltip(var2, new GameBlackboard()), GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
   }

   public class ItemElement extends FormListGridElement<FormItemList> {
      public final InventoryItem item;

      public ItemElement(InventoryItem var2) {
         this.item = var2;
      }

      void draw(FormItemList var1, TickManager var2, PlayerMob var3, int var4) {
         if (this.isMouseOver(var1)) {
            Settings.UI.inventoryslot_small.highlighted.initDraw().color(Settings.UI.highlightElementColor).draw(2, 2);
            FormItemList.this.addTooltips(this.item, var3);
         } else {
            Settings.UI.inventoryslot_small.active.initDraw().color(Settings.UI.activeElementColor).draw(2, 2);
         }

         this.item.draw(var3, 2, 2);
      }

      void onClick(FormItemList var1, int var2, InputEvent var3, PlayerMob var4) {
         FormItemList.this.onItemClicked(this.item, var3);
      }

      void onControllerEvent(FormItemList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         if (var3.getState() == ControllerInput.MENU_SELECT) {
            FormItemList.this.onItemClicked(this.item, InputEvent.ControllerButtonEvent(var3, var4));
            var3.use();
         }
      }

      public void drawControllerFocus(ControllerFocus var1) {
         super.drawControllerFocus(var1);
         Screen.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onControllerEvent(FormGeneralList var1, int var2, ControllerEvent var3, TickManager var4, PlayerMob var5) {
         this.onControllerEvent((FormItemList)var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void onClick(FormGeneralList var1, int var2, InputEvent var3, PlayerMob var4) {
         this.onClick((FormItemList)var1, var2, var3, var4);
      }

      // $FF: synthetic method
      // $FF: bridge method
      void draw(FormGeneralList var1, TickManager var2, PlayerMob var3, int var4) {
         this.draw((FormItemList)var1, var2, var3, var4);
      }
   }
}
