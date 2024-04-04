package necesse.gfx.forms.presets.containerComponent.settlement.diets;

import java.awt.Rectangle;
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
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ItemCategoriesFilterForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;

public abstract class SettlementDietFilterForm extends Form {
   private ClipboardTracker<DietData> listClipboard;
   private FormContentIconButton pasteButton;
   private FormContentBox filterContent;
   private ItemCategoriesFilterForm filterForm;

   public SettlementDietFilterForm(String var1, int var2, int var3, Mob var4, ItemCategoriesFilter var5, Client var6) {
      this(var1, var2, var3, var5, var6, MobRegistry.getLocalization(var4.getID()), new LocalMessage("ui", "backbutton"));
   }

   public SettlementDietFilterForm(String var1, int var2, int var3, ItemCategoriesFilter var4, Client var5, GameMessage var6, GameMessage var7) {
      super(var1, var2, var3);
      FormFlow var8 = new FormFlow(5);
      if (var6 != null) {
         String var9 = var6.translate();
         FontOptions var10 = new FontOptions(20);
         String var11 = GameUtils.maxString(var9, var10, this.getWidth() - 10);
         this.addComponent(new FormLabel(var11, var10, -1, 5, var8.next(30)));
      }

      int var14 = var8.next(28);
      int var15 = this.getWidth() - 28;
      this.pasteButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(var15, var14, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}));
      this.pasteButton.onClicked((var2x) -> {
         DietData var3 = (DietData)this.listClipboard.getValue();
         if (var3 != null) {
            SaveData var4x = new SaveData("");
            var3.filter.addSaveData(var4x);
            var4.applyLoadData(var4x.toLoadData());
            this.onFullChange(var4);
         }

      });
      this.listClipboard = new ClipboardTracker<DietData>() {
         public DietData parse(String var1) {
            try {
               return new DietData(new LoadData(var1));
            } catch (Exception var3) {
               return null;
            }
         }

         public void onUpdate(DietData var1) {
            SettlementDietFilterForm.this.pasteButton.setActive(var1 != null);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onUpdate(Object var1) {
            this.onUpdate((DietData)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Object parse(String var1) {
            return this.parse(var1);
         }
      };
      var15 -= 28;
      ((FormContentIconButton)this.addComponent(new FormContentIconButton(var15, var14, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((var2x) -> {
         DietData var3 = new DietData(var4);
         Screen.putClipboard(var3.getSaveData().getScript());
         this.listClipboard.forceUpdate();
      });
      FormTextInput var16 = (FormTextInput)this.addComponent(new FormTextInput(4, var14, FormInputSize.SIZE_24, var15 - 8, -1, 500));
      var16.placeHolder = new LocalMessage("ui", "searchtip");
      var16.onChange((var2x) -> {
         this.filterForm.setSearch(var16.getText());
      });
      int var12 = this.getHeight() - var8.next() - (var7 != null ? 24 : 0) - 4;
      this.filterContent = (FormContentBox)this.addComponent(new FormContentBox(0, var8.next(var12), this.getWidth(), var12));
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
      ItemCategoryExpandedSetting var13 = Settings.getItemCategoryExpandedSetting("settlerdietfilter");
      this.filterForm = (ItemCategoriesFilterForm)this.filterContent.addComponent(new ItemCategoriesFilterForm(4, 28, var4, false, var13, var5.characterStats.items_obtained, true) {
         public void onDimensionsChanged(int var1, int var2) {
            SettlementDietFilterForm.this.filterContent.setContentBox(new Rectangle(0, 0, Math.max(SettlementDietFilterForm.this.getWidth(), var1), this.getY() + var2));
         }

         public void onItemLimitsChanged(Item var1, ItemCategoriesFilter.ItemLimits var2) {
         }

         public void onItemsChanged(Item[] var1, boolean var2) {
            SettlementDietFilterForm.this.onItemsChanged(var1, var2);
         }

         public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2) {
            SettlementDietFilterForm.this.onCategoryChanged(var1, var2);
         }

         public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1, int var2) {
         }
      });
      if (var7 != null) {
         ((FormLocalTextButton)this.addComponent(new FormLocalTextButton(var7, var2 / 2, var8.next(), var2 / 2 - 4, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
            this.onButtonPressed();
         });
      }

   }

   public abstract void onItemsChanged(Item[] var1, boolean var2);

   public abstract void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2);

   public abstract void onFullChange(ItemCategoriesFilter var1);

   public abstract void onButtonPressed();

   public static class DietData {
      public final ItemCategoriesFilter filter;

      public DietData(ItemCategoriesFilter var1) {
         this.filter = var1;
      }

      public DietData(LoadData var1) {
         if (!var1.getName().equals("diet")) {
            throw new LoadDataException("Diet filter incorrect save component name");
         } else {
            this.filter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, false);
            this.filter.applyLoadData(var1);
         }
      }

      public SaveData getSaveData() {
         SaveData var1 = new SaveData("diet");
         this.filter.addSaveData(var1);
         return var1;
      }
   }
}
