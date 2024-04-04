package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.ClipboardTracker;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ItemCategoriesFilterForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.Inventory;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public abstract class SettlementStorageConfigForm extends FormSwitcher {
   public final Point tile;
   public final Client client;
   public final ItemCategoriesFilter filter;
   private Form mainForm;
   private ClipboardTracker<ConfigData> listClipboard;
   private FormContentIconButton pasteButton;
   public final ItemCategoriesFilterForm filterForm;
   public final FormDropdownSelectionButton<Integer> prioritySelect;
   public final FormDropdownSelectionButton<ItemCategoriesFilter.ItemLimitMode> limitMode;
   public final FormTextInput limitInput;

   public SettlementStorageConfigForm(String var1, int var2, int var3, Point var4, Client var5, Inventory var6, GameMessage var7, ItemCategoriesFilter var8, int var9) {
      this.tile = var4;
      this.client = var5;
      this.filter = var8;
      this.mainForm = (Form)this.addComponent(new Form(var1, var2, var3));
      FormFlow var10 = new FormFlow(5);
      if (var7 != null) {
         this.mainForm.addComponent(new FormLocalLabel(var7, new FontOptions(20), -1, 5, var10.next(30)));
      }

      int var11 = var10.next(28);
      this.prioritySelect = (FormDropdownSelectionButton)this.mainForm.addComponent(new FormDropdownSelectionButton(4, var11, FormInputSize.SIZE_24, ButtonColor.BASE, this.mainForm.getWidth() - 8));
      this.prioritySelect.options.add(300, SettlementInventory.getPriorityText(300));
      this.prioritySelect.options.add(200, SettlementInventory.getPriorityText(200));
      this.prioritySelect.options.add(100, SettlementInventory.getPriorityText(100));
      this.prioritySelect.options.add(0, SettlementInventory.getPriorityText(0));
      this.prioritySelect.options.add(-100, SettlementInventory.getPriorityText(-100));
      this.prioritySelect.options.add(-200, SettlementInventory.getPriorityText(-200));
      this.prioritySelect.options.add(-300, SettlementInventory.getPriorityText(-300));
      this.updatePrioritySelect(var9);
      this.prioritySelect.onSelected((var1x) -> {
         this.onPriorityChange((Integer)var1x.value);
      });
      int var12 = var10.next(28);
      this.limitMode = (FormDropdownSelectionButton)this.mainForm.addComponent(new FormDropdownSelectionButton(4, var12, FormInputSize.SIZE_24, ButtonColor.BASE, this.mainForm.getWidth() / 2 - 6));
      ItemCategoriesFilter.ItemLimitMode[] var13 = ItemCategoriesFilter.ItemLimitMode.values();
      int var14 = var13.length;

      int var15;
      for(var15 = 0; var15 < var14; ++var15) {
         ItemCategoriesFilter.ItemLimitMode var16 = var13[var15];
         this.limitMode.options.add(var16, var16.displayName, var16.tooltip == null ? null : () -> {
            return var16.tooltip;
         });
      }

      this.updateLimitMode();
      this.limitInput = (FormTextInput)this.mainForm.addComponent(new FormTextInput(this.mainForm.getWidth() / 2 + 2, var12, FormInputSize.SIZE_24, this.mainForm.getWidth() / 2 - 6, 7));
      this.updateLimitInput();
      this.limitInput.setRegexMatchFull("([0-9]+)?");
      this.limitInput.rightClickToClear = true;
      this.limitInput.onSubmit((var2x) -> {
         try {
            int var3;
            if (this.limitInput.getText().isEmpty()) {
               var3 = Integer.MAX_VALUE;
            } else {
               var3 = Integer.parseInt(this.limitInput.getText());
            }

            if (var8.maxAmount != var3) {
               var8.maxAmount = var3;
               this.updateLimitInput();
               this.onLimitChange(var8.limitMode, var8.maxAmount);
            }
         } catch (NumberFormatException var4) {
            this.updateLimitInput();
         }

      });
      this.limitMode.onSelected((var2x) -> {
         if (var8.limitMode != var2x.value) {
            var8.limitMode = (ItemCategoriesFilter.ItemLimitMode)var2x.value;
            this.limitInput.tooltip = var8.limitMode.inputPlaceholder;
            this.limitInput.placeHolder = var8.limitMode.inputPlaceholder;
            this.onLimitChange(var8.limitMode, var8.maxAmount);
         }

      });
      int var19 = var10.next(28);
      var14 = var10.next();
      var15 = var3 - var14 - 32;
      final FormContentBox var20 = (FormContentBox)this.mainForm.addComponent(new FormContentBox(0, var14, this.mainForm.getWidth(), var15));
      ItemCategoryExpandedSetting var17 = Settings.getItemCategoryExpandedSetting(var1);
      this.filterForm = (ItemCategoriesFilterForm)var20.addComponent(new ItemCategoriesFilterForm(4, 28, var8, true, var17, var5.characterStats.items_obtained, true) {
         public void onDimensionsChanged(int var1, int var2) {
            var20.setContentBox(new Rectangle(0, 0, Math.max(SettlementStorageConfigForm.this.mainForm.getWidth(), var1), this.getY() + var2));
         }

         public void onItemsChanged(Item[] var1, boolean var2) {
            SettlementStorageConfigForm.this.onItemsChanged(var1, var2);
         }

         public void onItemLimitsChanged(Item var1, ItemCategoriesFilter.ItemLimits var2) {
            SettlementStorageConfigForm.this.onItemLimitsChanged(var1, var2);
         }

         public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2) {
            SettlementStorageConfigForm.this.onCategoryChanged(var1, var2);
         }

         public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1, int var2) {
            SettlementStorageConfigForm.this.onCategoryLimitsChanged(var1, var2);
         }
      });
      ((FormLocalTextButton)var20.addComponent(new FormLocalTextButton("ui", "allowallbutton", 4, 0, this.mainForm.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
         if (!this.filterForm.filter.master.isAllAllowed() || !this.filterForm.filter.master.isAllDefault()) {
            this.filterForm.filter.master.setAllowed(true);
            this.filterForm.updateAllButtons();
            this.onCategoryChanged(this.filterForm.filter.master, true);
         }

      });
      ((FormLocalTextButton)var20.addComponent(new FormLocalTextButton("ui", "clearallbutton", this.mainForm.getWidth() / 2 + 2, 0, this.mainForm.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
         if (this.filterForm.filter.master.isAnyAllowed()) {
            this.filterForm.filter.master.setAllowed(false);
            this.filterForm.updateAllButtons();
            this.onCategoryChanged(this.filterForm.filter.master, false);
         }

      });
      FormTextInput var18 = (FormTextInput)this.mainForm.addComponent(new FormTextInput(4, var19, FormInputSize.SIZE_24, this.mainForm.getWidth() - 28 - 28 - 28 - 8, -1, 500));
      var18.placeHolder = new LocalMessage("ui", "searchtip");
      var18.onChange((var2x) -> {
         this.filterForm.setSearch(var18.getText());
      });
      ((FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(this.mainForm.getWidth() - 28, var19, FormInputSize.SIZE_24, ButtonColor.RED, Settings.UI.container_storage_remove, new GameMessage[]{new LocalMessage("ui", "settlementremovestorage")}))).onClicked((var1x) -> {
         this.onRemove();
      });
      this.pasteButton = (FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(this.mainForm.getWidth() - 28 - 28, var19, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}));
      this.pasteButton.onClicked((var2x) -> {
         ConfigData var3 = (ConfigData)this.listClipboard.getValue();
         if (var3 != null && (var8.loadFromCopy(var3.filter) || (Integer)this.prioritySelect.getSelected() != var3.priority)) {
            this.filterForm.updateAllButtons();
            this.updatePrioritySelect(var3.priority);
            this.updateLimitMode();
            this.updateLimitInput();
            this.onFullChange(var8, var3.priority);
         }

      });
      ((FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(this.mainForm.getWidth() - 28 - 28 - 28, var19, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((var2x) -> {
         SaveData var3 = (new ConfigData((Integer)this.prioritySelect.getSelected(), var8)).getSaveData();
         Screen.putClipboard(var3.getScript());
         this.listClipboard.forceUpdate();
      });
      this.listClipboard = new ClipboardTracker<ConfigData>() {
         public ConfigData parse(String var1) {
            try {
               return new ConfigData(new LoadData(var1));
            } catch (Exception var3) {
               return null;
            }
         }

         public void onUpdate(ConfigData var1) {
            SettlementStorageConfigForm.this.pasteButton.setActive(var1 != null);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onUpdate(Object var1) {
            this.onUpdate((ConfigData)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Object parse(String var1) {
            return this.parse(var1);
         }
      };
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "backbutton", var2 / 2 - 4, this.mainForm.getHeight() - 28, var2 / 2, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
         this.onBack();
      });
      this.makeCurrent(this.mainForm);
   }

   public void updatePrioritySelect(int var1) {
      this.prioritySelect.setSelected(var1, SettlementInventory.getPriorityText(var1));
   }

   public void updateLimitMode() {
      this.limitMode.setSelected(this.filter.limitMode, this.filter.limitMode.displayName);
   }

   public void updateLimitInput() {
      this.limitInput.tooltip = this.filter.limitMode.inputPlaceholder;
      this.limitInput.placeHolder = this.filter.limitMode.inputPlaceholder;
      if (!this.limitInput.isTyping()) {
         if (this.filter.maxAmount != Integer.MAX_VALUE) {
            this.limitInput.setText(String.valueOf(this.filter.maxAmount));
         } else {
            this.limitInput.setText("");
         }
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.listClipboard.update();
      super.draw(var1, var2, var3);
   }

   public void setPosFocus() {
      ContainerComponent.setPosFocus(this.mainForm);
   }

   public void setPosInventory() {
      ContainerComponent.setPosInventory(this.mainForm);
   }

   public abstract void onItemsChanged(Item[] var1, boolean var2);

   public abstract void onItemLimitsChanged(Item var1, ItemCategoriesFilter.ItemLimits var2);

   public abstract void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2);

   public abstract void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1, int var2);

   public abstract void onFullChange(ItemCategoriesFilter var1, int var2);

   public abstract void onPriorityChange(int var1);

   public abstract void onLimitChange(ItemCategoriesFilter.ItemLimitMode var1, int var2);

   public abstract void onRemove();

   public abstract void onBack();

   private static class ConfigData {
      public final int priority;
      public final ItemCategoriesFilter filter;

      public ConfigData(int var1, ItemCategoriesFilter var2) {
         this.priority = var1;
         this.filter = var2;
      }

      public ConfigData(LoadData var1) {
         this.priority = var1.getInt("priority", 0, false);
         this.filter = new ItemCategoriesFilter(false);
         this.filter.applyLoadData(var1.getFirstLoadDataByName("filter"));
      }

      public SaveData getSaveData() {
         SaveData var1 = new SaveData("config");
         var1.addInt("priority", this.priority);
         SaveData var2 = new SaveData("filter");
         this.filter.addSaveData(var2);
         var1.addSaveData(var2);
         return var1;
      }
   }
}
