package necesse.gfx.forms.presets.containerComponent.settlement.equipment;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Supplier;
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
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentFiltersForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerEquipmentFilterData;
import necesse.inventory.container.settlement.events.SettlementNewSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFiltersEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;

public class SettlementEquipmentForm<T extends SettlementContainer> extends FormSwitcher implements SettlementSubForm {
   public final Client client;
   public final T container;
   public final SettlementContainerForm<T> containerForm;
   public int maxHeight;
   protected FormSwitcher setCurrentWhenLoaded;
   protected ArrayList<SettlementSettlerEquipmentFilterData> settlers;
   protected Form mainForm;
   protected SettlementEquipmentContentBox content;
   public int equipmentsSubscription = -1;
   public boolean newSettlerSelfManageEquipment = true;
   public boolean newSettlerPreferArmorSets = true;
   public ItemCategoriesFilter newSettlerEquipmentsFilter;
   public PasteButton newSettlerPasteButton;
   public ClipboardTracker<EquipmentFiltersForm.EquipmentFilterData> listClipboard;

   public SettlementEquipmentForm(Client var1, T var2, SettlementContainerForm<T> var3) {
      this.newSettlerEquipmentsFilter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
      this.client = var1;
      this.container = var2;
      this.containerForm = var3;
      this.maxHeight = 300;
      this.mainForm = (Form)this.addComponent(new Form("settlers", 500, 300));
      this.mainForm.addComponent(new FormLocalLabel("ui", "settlementequipment", new FontOptions(20), 0, this.mainForm.getWidth() / 2, 5));
      FormContentIconToggleButton var4 = (FormContentIconToggleButton)this.mainForm.addComponent(new FormContentIconToggleButton(this.mainForm.getWidth() - 28, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_checked_20, Settings.UI.button_escaped_20, new GameMessage[]{new LocalMessage("settingsui", "showsettlerheadarmor")}) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            this.setToggled(Settings.showSettlerHeadArmor);
            super.draw(var1, var2, var3);
         }
      });
      var4.onToggled((var0) -> {
         Settings.showSettlerHeadArmor = ((FormButtonToggle)var0.from).isToggled();
         Settings.saveClientSettings();
      });
      byte var5 = 24;
      int var6 = this.mainForm.getWidth() - var5 - Settings.UI.scrollbar.active.getHeight() - 2;
      byte var7 = 30;
      FormContentIconButton var8 = (FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(var6, var7 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_storage_config, new GameMessage[]{new LocalMessage("ui", "settlerfilterequipment")}) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            this.setActive(SettlementEquipmentForm.this.newSettlerSelfManageEquipment);
            super.draw(var1, var2, var3);
         }
      });
      var8.onClicked((var3x) -> {
         EquipmentFiltersForm var4 = new EquipmentFiltersForm("changeequipmentfilter", 408, 300, this.newSettlerEquipmentsFilter, () -> {
            return this.newSettlerPreferArmorSets;
         }, var1, new LocalMessage("ui", "settlementnewsettlers"), new LocalMessage("ui", "backbutton")) {
            public void onSetPreferArmorSets(boolean var1) {
               SettlementEquipmentForm.this.newSettlerPreferArmorSets = var1;
               var2.setNewSettlerEquipmentFilter.runAndSendPreferArmorSets(var1);
            }

            public void onItemsChanged(Item[] var1, boolean var2x) {
               var2.setNewSettlerEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.itemsAllowed(var1, var2x));
            }

            public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2x) {
               var2.setNewSettlerEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.categoryAllowed(var1, var2x));
            }

            public void onFullChange(ItemCategoriesFilter var1) {
               var2.setNewSettlerEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.fullChange(var1));
            }

            public void onButtonPressed() {
               SettlementEquipmentForm.this.makeCurrent(SettlementEquipmentForm.this.mainForm);
            }

            public void onWindowResized() {
               super.onWindowResized();
               ContainerComponent.setPosInventory(this);
            }
         };
         this.addAndMakeCurrentTemporary(var4);
         var4.onWindowResized();
      });
      var6 -= 24;
      FormContentIconButton var9 = (FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(var6, var7 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}) {
         public boolean isActive() {
            return super.isActive() && SettlementEquipmentForm.this.newSettlerSelfManageEquipment;
         }
      });
      var9.onClicked((var3x) -> {
         EquipmentFiltersForm.EquipmentFilterData var4 = (EquipmentFiltersForm.EquipmentFilterData)this.listClipboard.getValue();
         if (var4 != null) {
            SaveData var5 = new SaveData("");
            var4.filter.addSaveData(var5);
            this.newSettlerPreferArmorSets = var4.preferArmorSets;
            this.newSettlerEquipmentsFilter.applyLoadData(var5.toLoadData());
            var2.setNewSettlerEquipmentFilter.runAndSendPreferArmorSets(this.newSettlerPreferArmorSets);
            var2.setNewSettlerEquipmentFilter.runAndSendChange(ItemCategoriesFilterChange.fullChange(this.newSettlerEquipmentsFilter));
            var9.setActive(false);
         }

      });
      var9.setupDragPressOtherButtons("equipmentPasteButton");
      this.newSettlerPasteButton = new PasteButton(var9, () -> {
         return this.newSettlerPreferArmorSets;
      }, this.newSettlerEquipmentsFilter);
      var6 -= 24;
      FormContentIconButton var10 = (FormContentIconButton)this.mainForm.addComponent(new FormContentIconButton(var6, var7 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            this.setActive(SettlementEquipmentForm.this.newSettlerSelfManageEquipment);
            super.draw(var1, var2, var3);
         }
      });
      var10.onClicked((var1x) -> {
         EquipmentFiltersForm.EquipmentFilterData var2 = new EquipmentFiltersForm.EquipmentFilterData(this.newSettlerPreferArmorSets, this.newSettlerEquipmentsFilter);
         Screen.putClipboard(var2.getSaveData().getScript());
         this.listClipboard.forceUpdate();
      });
      var6 -= 24;
      FormContentIconToggleButton var11 = (FormContentIconToggleButton)this.mainForm.addComponent(new FormContentIconToggleButton(var6, var7 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_checked_20, Settings.UI.button_escaped_20, new GameMessage[]{new LocalMessage("ui", "settlerselfmanagequipment")}) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            this.setToggled(SettlementEquipmentForm.this.newSettlerSelfManageEquipment);
            super.draw(var1, var2, var3);
         }
      });
      var11.onToggled((var2x) -> {
         this.newSettlerSelfManageEquipment = ((FormButtonToggle)var2x.from).isToggled();
         var2.setNewSettlerEquipmentFilter.runAndSendSelfManageEquipment(this.newSettlerSelfManageEquipment);
      });
      var11.setupDragToOtherButtons("selfManageEquipment");
      this.mainForm.addComponent(new FormLocalLabel("ui", "settlementnewsettlers", new FontOptions(20), -1, 5, var7 + 5));
      this.mainForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 0, 60, this.mainForm.getWidth(), true));
      this.content = (SettlementEquipmentContentBox)this.mainForm.addComponent(new SettlementEquipmentContentBox(this, 0, 62, this.mainForm.getWidth(), this.mainForm.getHeight() - 30 - 32));
      this.listClipboard = new ClipboardTracker<EquipmentFiltersForm.EquipmentFilterData>() {
         public EquipmentFiltersForm.EquipmentFilterData parse(String var1) {
            try {
               return new EquipmentFiltersForm.EquipmentFilterData(new LoadData(var1));
            } catch (Exception var3) {
               return null;
            }
         }

         public void onUpdate(EquipmentFiltersForm.EquipmentFilterData var1) {
            SettlementEquipmentForm.this.newSettlerPasteButton.updateActive(var1);
            SettlementEquipmentForm.this.content.updatePasteButtons(var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onUpdate(Object var1) {
            this.onUpdate((EquipmentFiltersForm.EquipmentFilterData)var1);
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
         this.container.requestSettlerEquipmentFilters.runAndSend();
      });
      this.container.onEvent(SettlementSettlerEquipmentFiltersEvent.class, (var1) -> {
         if (this.setCurrentWhenLoaded != null) {
            this.setCurrentWhenLoaded.makeCurrent(this);
         }

         this.setCurrentWhenLoaded = null;
         if (this.containerForm.isCurrent(this)) {
            this.settlers = var1.settlers;
            this.content.updateEquipmentsContent();
         }
      });
      this.container.onEvent(SettlementNewSettlerEquipmentFilterChangedEvent.class, (var1) -> {
         this.newSettlerSelfManageEquipment = var1.selfManageEquipment;
         this.newSettlerPreferArmorSets = var1.preferArmorSets;
         if (var1.change != null) {
            var1.change.applyTo(this.newSettlerEquipmentsFilter);
         }

         this.listClipboard.forceUpdate();
         this.listClipboard.onUpdate((EquipmentFiltersForm.EquipmentFilterData)this.listClipboard.getValue());
      });
      this.container.onEvent(SettlementSettlerEquipmentFilterChangedEvent.class, (var1) -> {
         if (this.settlers != null) {
            Iterator var2 = this.settlers.iterator();

            SettlementSettlerEquipmentFilterData var3;
            do {
               if (!var2.hasNext()) {
                  this.container.requestSettlerEquipmentFilters.runAndSend();
                  return;
               }

               var3 = (SettlementSettlerEquipmentFilterData)var2.next();
            } while(var3.mobUniqueID != var1.mobUniqueID);

            var3.preferArmorSets = var1.preferArmorSets;
            if (var1.change != null) {
               var1.change.applyTo(var3.equipmentFilter);
            }

            this.listClipboard.forceUpdate();
            this.listClipboard.onUpdate((EquipmentFiltersForm.EquipmentFilterData)this.listClipboard.getValue());
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
         if (this.equipmentsSubscription == -1) {
            this.equipmentsSubscription = this.container.subscribeEquipment.subscribe();
         }

         this.makeCurrent(this.mainForm);
      } else if (this.equipmentsSubscription != -1) {
         this.container.subscribeEquipment.unsubscribe(this.equipmentsSubscription);
         this.equipmentsSubscription = -1;
      }

   }

   public void onMenuButtonClicked(FormSwitcher var1) {
      this.setCurrentWhenLoaded = var1;
      this.container.requestSettlerEquipmentFilters.runAndSend();
      if (this.equipmentsSubscription == -1) {
         this.equipmentsSubscription = this.container.subscribeEquipment.subscribe();
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.updateSize();
   }

   public GameMessage getMenuButtonName() {
      return new LocalMessage("ui", "settlementequipment");
   }

   public String getTypeString() {
      return "equipment";
   }

   public static class PasteButton {
      public final FormContentIconButton button;
      public final Supplier<Boolean> preferArmorSets;
      public final ItemCategoriesFilter equipmentsFilter;

      public PasteButton(FormContentIconButton var1, Supplier<Boolean> var2, ItemCategoriesFilter var3) {
         this.button = var1;
         this.preferArmorSets = var2;
         this.equipmentsFilter = var3;
      }

      public void updateActive(EquipmentFiltersForm.EquipmentFilterData var1) {
         if (var1 != null && var1.filter != null) {
            this.button.setActive(var1.preferArmorSets != (Boolean)this.preferArmorSets.get() || !var1.filter.isEqualsFilter(this.equipmentsFilter));
         } else {
            this.button.setActive(false);
         }
      }
   }
}
