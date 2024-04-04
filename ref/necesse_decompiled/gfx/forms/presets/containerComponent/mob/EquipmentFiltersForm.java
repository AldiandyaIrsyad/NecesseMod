package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.function.Supplier;
import necesse.engine.ClipboardTracker;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ItemCategoriesFilterForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public abstract class EquipmentFiltersForm extends Form {
   private ClipboardTracker<EquipmentFilterData> listClipboard;
   private FormContentIconButton pasteButton;
   private Supplier<Boolean> preferArmorSets;
   private ItemCategoriesFilter equipmentFilter;
   private FormContentIconToggleButton preferArmorSetsCheckBox;
   private FormContentBox filterContent;
   private ItemCategoriesFilterForm filterForm;

   public EquipmentFiltersForm(String var1, int var2, int var3, Mob var4, ItemCategoriesFilter var5, Supplier<Boolean> var6, Client var7) {
      this(var1, var2, var3, var5, var6, var7, MobRegistry.getLocalization(var4.getID()), new LocalMessage("ui", "backbutton"));
   }

   public EquipmentFiltersForm(String var1, int var2, int var3, ItemCategoriesFilter var4, Supplier<Boolean> var5, Client var6, GameMessage var7, GameMessage var8) {
      super(var1, var2, var3);
      this.preferArmorSets = var5;
      this.equipmentFilter = var4;
      FormFlow var9 = new FormFlow(5);
      if (var7 != null) {
         String var10 = var7.translate();
         FontOptions var11 = new FontOptions(20);
         String var12 = GameUtils.maxString(var10, var11, this.getWidth() - 10);
         this.addComponent(new FormLabel(var12, var11, -1, 5, var9.next(30)));
      }

      FormLocalLabel var16 = (FormLocalLabel)this.addComponent((FormLocalLabel)var9.nextY(new FormLocalLabel("ui", "settlerpreferarmorsets", new FontOptions(16), -1, 32, 0, this.getWidth() - 42), 8));
      this.preferArmorSetsCheckBox = (FormContentIconToggleButton)this.addComponent(new FormContentIconToggleButton(5, var16.getY() + var16.getHeight() / 2 - 12, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_checked_20, Settings.UI.button_escaped_20, new GameMessage[0]));
      this.preferArmorSetsCheckBox.onToggled((var1x) -> {
         this.onSetPreferArmorSets(this.preferArmorSetsCheckBox.isToggled());
      });
      this.preferArmorSetsCheckBox.setToggled((Boolean)var5.get());
      int var17 = var9.next(28);
      int var18 = this.getWidth() - 28;
      this.pasteButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(var18, var17, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}));
      this.pasteButton.onClicked((var2x) -> {
         EquipmentFilterData var3 = (EquipmentFilterData)this.listClipboard.getValue();
         if (var3 != null) {
            SaveData var4x = new SaveData("");
            var3.filter.addSaveData(var4x);
            var4.applyLoadData(var4x.toLoadData());
            this.onFullChange(var4);
            this.preferArmorSetsCheckBox.setToggled(var3.preferArmorSets);
            this.onSetPreferArmorSets(var3.preferArmorSets);
         }

      });
      this.listClipboard = new ClipboardTracker<EquipmentFilterData>() {
         public EquipmentFilterData parse(String var1) {
            try {
               return new EquipmentFilterData(new LoadData(var1));
            } catch (Exception var3) {
               return null;
            }
         }

         public void onUpdate(EquipmentFilterData var1) {
            EquipmentFiltersForm.this.pasteButton.setActive(var1 != null);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onUpdate(Object var1) {
            this.onUpdate((EquipmentFilterData)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Object parse(String var1) {
            return this.parse(var1);
         }
      };
      var18 -= 28;
      ((FormContentIconButton)this.addComponent(new FormContentIconButton(var18, var17, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((var2x) -> {
         EquipmentFilterData var3 = new EquipmentFilterData(this.preferArmorSetsCheckBox.isToggled(), var4);
         Screen.putClipboard(var3.getSaveData().getScript());
         this.listClipboard.forceUpdate();
      });
      FormTextInput var13 = (FormTextInput)this.addComponent(new FormTextInput(4, var17, FormInputSize.SIZE_24, var18 - 8, -1, 500));
      var13.placeHolder = new LocalMessage("ui", "searchtip");
      var13.onChange((var2x) -> {
         this.filterForm.setSearch(var13.getText());
      });
      int var14 = this.getHeight() - var9.next() - (var8 != null ? 24 : 0) - 4;
      this.filterContent = (FormContentBox)this.addComponent(new FormContentBox(0, var9.next(var14), this.getWidth(), var14));
      ((FormLocalTextButton)this.filterContent.addComponent(new FormLocalTextButton("ui", "allowallbutton", 4, 0, this.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
         if (!this.filterForm.filter.master.isAllAllowed()) {
            this.filterForm.filter.master.setAllowed(true);
            this.filterForm.updateAllButtons();
            this.onCategoryChanged(this.filterForm.filter.master, true);
         }

      });
      ((FormLocalTextButton)this.filterContent.addComponent(new FormLocalTextButton("ui", "clearallbutton", this.getWidth() / 2 + 2, 0, this.getWidth() / 2 - 6, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
         if (this.filterForm.filter.master.isAnyAllowed()) {
            this.filterForm.filter.master.setAllowed(false);
            this.filterForm.updateAllButtons();
            this.onCategoryChanged(this.filterForm.filter.master, false);
         }

      });
      ItemCategoryExpandedSetting var15 = Settings.getItemCategoryExpandedSetting("settlerequipmentfilter");
      this.filterForm = (ItemCategoriesFilterForm)this.filterContent.addComponent(new ItemCategoriesFilterForm(4, 28, var4, false, var15, var6.characterStats.items_obtained, true) {
         public void onDimensionsChanged(int var1, int var2) {
            EquipmentFiltersForm.this.filterContent.setContentBox(new Rectangle(0, 0, Math.max(EquipmentFiltersForm.this.getWidth(), var1), this.getY() + var2));
         }

         public void onItemLimitsChanged(Item var1, ItemCategoriesFilter.ItemLimits var2) {
         }

         public void onItemsChanged(Item[] var1, boolean var2) {
            EquipmentFiltersForm.this.onItemsChanged(var1, var2);
         }

         public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2) {
            EquipmentFiltersForm.this.onCategoryChanged(var1, var2);
         }

         public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1, int var2) {
         }
      });
      if (var8 != null) {
         ((FormLocalTextButton)this.addComponent(new FormLocalTextButton(var8, var2 / 2, var9.next(), var2 / 2 - 4, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
            this.onButtonPressed();
         });
      }

   }

   public abstract void onSetPreferArmorSets(boolean var1);

   public abstract void onItemsChanged(Item[] var1, boolean var2);

   public abstract void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2);

   public abstract void onFullChange(ItemCategoriesFilter var1);

   public abstract void onButtonPressed();

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.preferArmorSetsCheckBox.setToggled((Boolean)this.preferArmorSets.get());
      super.draw(var1, var2, var3);
   }

   public static class EquipmentFilterData {
      public final boolean preferArmorSets;
      public final ItemCategoriesFilter filter;

      public EquipmentFilterData(boolean var1, ItemCategoriesFilter var2) {
         this.preferArmorSets = var1;
         this.filter = var2;
      }

      public EquipmentFilterData(LoadData var1) {
         if (!var1.getName().equals("equipmentFilter")) {
            throw new LoadDataException("Equipment filter incorrect save component name");
         } else {
            this.filter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, false);
            this.preferArmorSets = var1.getBoolean("preferArmorSets");
            this.filter.applyLoadData(var1);
         }
      }

      public SaveData getSaveData() {
         SaveData var1 = new SaveData("equipmentFilter");
         var1.addBoolean("preferArmorSets", this.preferArmorSets);
         this.filter.addSaveData(var1);
         return var1;
      }
   }
}
