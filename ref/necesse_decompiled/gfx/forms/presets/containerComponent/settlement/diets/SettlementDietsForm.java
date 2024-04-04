package necesse.gfx.forms.presets.containerComponent.settlement.diets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.ClipboardTracker;
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
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerDietsData;
import necesse.inventory.container.settlement.events.SettlementNewSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerDietsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;

public class SettlementDietsForm<T extends SettlementContainer> extends FormSwitcher implements SettlementSubForm {
   public final Client client;
   public final T container;
   public final SettlementContainerForm<T> containerForm;
   public int maxHeight;
   protected FormSwitcher setCurrentWhenLoaded;
   protected ArrayList<SettlementSettlerDietsData> settlers;
   protected Form mainForm;
   protected SettlementDietsContentBox content;
   public int dietsSubscription = -1;
   public ItemCategoriesFilter newSettlerDiet;
   public PasteButton newSettlerDietPasteButton;
   public ClipboardTracker<SettlementDietFilterForm.DietData> listClipboard;

   public SettlementDietsForm(Client var1, T var2, SettlementContainerForm<T> var3) {
      this.newSettlerDiet = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
      this.client = var1;
      this.container = var2;
      this.containerForm = var3;
      this.maxHeight = 300;
      this.mainForm = (Form)this.addComponent(new Form("settlers", 500, 300));
      this.mainForm.addComponent(new FormLocalLabel("ui", "settlementdiets", new FontOptions(20), 0, this.mainForm.getWidth() / 2, 5));
      short var4 = 150;
      int var5 = this.mainForm.getWidth() - var4 - Settings.UI.scrollbar.active.getHeight() - 2;
      byte var6 = 30;
      ((FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "settlementchangediet", var5, var6 + 3, var4, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var3x) -> {
         SettlementDietFilterForm var4 = new SettlementDietFilterForm("changediet", 408, 250, this.newSettlerDiet, var1, new LocalMessage("ui", "settlementnewsettlers"), new LocalMessage("ui", "backbutton")) {
            public void onItemsChanged(Item[] var1, boolean var2x) {
               var2.setNewSettlerDiet.runAndSendChange(ItemCategoriesFilterChange.itemsAllowed(var1, var2x));
            }

            public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2x) {
               var2.setNewSettlerDiet.runAndSendChange(ItemCategoriesFilterChange.categoryAllowed(var1, var2x));
            }

            public void onFullChange(ItemCategoriesFilter var1) {
               var2.setNewSettlerDiet.runAndSendChange(ItemCategoriesFilterChange.fullChange(var1));
            }

            public void onButtonPressed() {
               SettlementDietsForm.this.makeCurrent(SettlementDietsForm.this.mainForm);
            }

            public void onWindowResized() {
               super.onWindowResized();
               ContainerComponent.setPosInventory(this);
            }
         };
         this.addAndMakeCurrentTemporary(var4);
         var4.onWindowResized();
      });
      var5 -= 24;
      FormContentIconButton var7 = (FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(var5, var6 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}));
      var7.onClicked((var3x) -> {
         SettlementDietFilterForm.DietData var4 = (SettlementDietFilterForm.DietData)this.listClipboard.getValue();
         if (var4 != null) {
            SaveData var5 = new SaveData("");
            var4.filter.addSaveData(var5);
            this.newSettlerDiet.applyLoadData(var5.toLoadData());
            var2.setNewSettlerDiet.runAndSendChange(ItemCategoriesFilterChange.fullChange(this.newSettlerDiet));
            var7.setActive(false);
         }

      });
      var7.setupDragPressOtherButtons("dietPasteButton");
      this.newSettlerDietPasteButton = new PasteButton(var7, this.newSettlerDiet);
      var5 -= 24;
      ((FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(var5, var6 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((var1x) -> {
         SettlementDietFilterForm.DietData var2 = new SettlementDietFilterForm.DietData(this.newSettlerDiet);
         Screen.putClipboard(var2.getSaveData().getScript());
         this.listClipboard.forceUpdate();
      });
      this.mainForm.addComponent(new FormLocalLabel("ui", "settlementnewsettlers", new FontOptions(20), -1, 5, var6 + 5));
      this.mainForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 0, 60, this.mainForm.getWidth(), true));
      this.content = (SettlementDietsContentBox)this.mainForm.addComponent(new SettlementDietsContentBox(this, 0, 62, this.mainForm.getWidth(), this.mainForm.getHeight() - 30 - 32));
      this.listClipboard = new ClipboardTracker<SettlementDietFilterForm.DietData>() {
         public SettlementDietFilterForm.DietData parse(String var1) {
            try {
               return new SettlementDietFilterForm.DietData(new LoadData(var1));
            } catch (Exception var3) {
               return null;
            }
         }

         public void onUpdate(SettlementDietFilterForm.DietData var1) {
            SettlementDietsForm.this.newSettlerDietPasteButton.updateActive(var1);
            SettlementDietsForm.this.content.updatePasteButtons(var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onUpdate(Object var1) {
            this.onUpdate((SettlementDietFilterForm.DietData)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Object parse(String var1) {
            return this.parse(var1);
         }
      };
   }

   protected void init() {
      super.init();
      this.container.onEvent(SettlementSettlersChangedEvent.class, (var1) -> {
         this.container.requestSettlerDiets.runAndSend();
      });
      this.container.onEvent(SettlementSettlerDietsEvent.class, (var1) -> {
         if (this.setCurrentWhenLoaded != null) {
            this.setCurrentWhenLoaded.makeCurrent(this);
         }

         this.setCurrentWhenLoaded = null;
         if (this.containerForm.isCurrent(this)) {
            this.settlers = var1.settlers;
            this.content.updateDietsContent();
         }
      });
      this.container.onEvent(SettlementNewSettlerDietChangedEvent.class, (var1) -> {
         var1.change.applyTo(this.newSettlerDiet);
         this.listClipboard.forceUpdate();
         this.listClipboard.onUpdate((SettlementDietFilterForm.DietData)this.listClipboard.getValue());
      });
      this.container.onEvent(SettlementSettlerDietChangedEvent.class, (var1) -> {
         if (this.settlers != null) {
            Iterator var2 = this.settlers.iterator();

            SettlementSettlerDietsData var3;
            do {
               if (!var2.hasNext()) {
                  this.container.requestSettlerDiets.runAndSend();
                  return;
               }

               var3 = (SettlementSettlerDietsData)var2.next();
            } while(var3.mobUniqueID != var1.mobUniqueID);

            var1.change.applyTo(var3.dietFilter);
            this.listClipboard.forceUpdate();
            this.listClipboard.onUpdate((SettlementDietFilterForm.DietData)this.listClipboard.getValue());
         }
      });
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.listClipboard.update();
      super.draw(var1, var2, var3);
   }

   public void updateSize() {
      this.mainForm.setHeight(Math.min(this.maxHeight, this.content.getY() + this.content.contentHeight));
      this.content.setContentBox(new Rectangle(0, 0, this.content.getWidth(), this.content.contentHeight));
      this.content.setWidth(this.mainForm.getWidth());
      this.content.setHeight(this.mainForm.getHeight() - this.content.getY());
      ContainerComponent.setPosInventory(this.mainForm);
   }

   public void onSetCurrent(boolean var1) {
      this.content.clearComponents();
      this.settlers = null;
      if (var1) {
         if (this.dietsSubscription == -1) {
            this.dietsSubscription = this.container.subscribeDiets.subscribe();
         }

         this.makeCurrent(this.mainForm);
      } else if (this.dietsSubscription != -1) {
         this.container.subscribeDiets.unsubscribe(this.dietsSubscription);
         this.dietsSubscription = -1;
      }

   }

   public void onMenuButtonClicked(FormSwitcher var1) {
      this.setCurrentWhenLoaded = var1;
      this.container.requestSettlerDiets.runAndSend();
      if (this.dietsSubscription == -1) {
         this.dietsSubscription = this.container.subscribeDiets.subscribe();
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.updateSize();
   }

   public GameMessage getMenuButtonName() {
      return new LocalMessage("ui", "settlementdiets");
   }

   public String getTypeString() {
      return "diets";
   }

   public static class PasteButton {
      public final FormContentIconButton button;
      public final ItemCategoriesFilter dietFilter;

      public PasteButton(FormContentIconButton var1, ItemCategoriesFilter var2) {
         this.button = var1;
         this.dietFilter = var2;
      }

      public void updateActive(SettlementDietFilterForm.DietData var1) {
         if (var1 != null && var1.filter != null) {
            this.button.setActive(!var1.filter.isEqualsFilter(this.dietFilter));
         } else {
            this.button.setActive(false);
         }
      }
   }
}
