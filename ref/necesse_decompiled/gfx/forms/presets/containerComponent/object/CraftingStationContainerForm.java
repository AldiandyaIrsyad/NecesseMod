package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.lists.FormContainerCraftingList;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.CraftFilterForm;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.recipe.RecipeFilter;
import necesse.level.maps.hudManager.HudDrawElement;

public class CraftingStationContainerForm<T extends CraftingStationContainer> extends ContainerFormSwitcher<T> {
   public Form craftingForm;
   public SettlementObjectStatusFormManager settlementObjectFormManager;
   protected FormContainerCraftingList craftList;
   protected FormLocalCheckBox onlyCraftable;
   protected FormLocalCheckBox useNearby;
   protected FormContentIconToggleButton filterButton;
   protected CraftFilterForm filterForm;
   protected HudDrawElement rangeElement;

   public CraftingStationContainerForm(Client var1, T var2) {
      super(var1, var2);
      this.craftingForm = (Form)this.addComponent(new Form(408, (Boolean)Settings.hasCraftingListExpanded.get() ? 300 : 180));
      this.craftingForm.addComponent(new FormLocalLabel(var2.header, new FontOptions(20), -1, 5, 5));
      this.craftList = (FormContainerCraftingList)this.craftingForm.addComponent(new FormContainerCraftingList(0, 24, this.craftingForm.getWidth(), this.craftingForm.getHeight() - 50, var1, false, false, var2.techs));
      this.onlyCraftable = (FormLocalCheckBox)this.craftingForm.addComponent(new FormLocalCheckBox("ui", "filteronlycraftable", 5, this.craftingForm.getHeight() - 32 - 6, (Boolean)Settings.craftingListOnlyCraftable.get()), 100);
      this.onlyCraftable.onClicked((var0) -> {
         Settings.craftingListOnlyCraftable.set(((FormCheckBox)var0.from).checked);
         Settings.saveClientSettings();
      });
      Settings.craftingListOnlyCraftable.addChangeListener((var1x) -> {
         this.onlyCraftable.checked = var1x;
         this.craftList.setOnlyCraftable(var1x);
         GlobalData.updateCraftable();
      }, this::isDisposed);
      this.craftList.setShowHidden(true);
      this.craftList.setOnlyCraftable(this.onlyCraftable.checked);
      this.useNearby = (FormLocalCheckBox)this.craftingForm.addComponent(new FormLocalCheckBox("ui", "usenearbyinv", 5, this.craftingForm.getHeight() - 16 - 4, (Boolean)Settings.craftingUseNearby.get()) {
         public GameTooltips getTooltip() {
            return (new StringTooltips()).add(Localization.translate("ui", "usenearbyinvtip"), 400);
         }
      }, 100);
      Settings.craftingUseNearby.addChangeListener((var1x) -> {
         this.useNearby.checked = var1x;
         GlobalData.updateCraftable();
      }, this::isDisposed);
      this.useNearby.onClicked((var0) -> {
         Settings.craftingUseNearby.set(((FormCheckBox)var0.from).checked);
      });
      int var3 = var2.craftingStationObject.getCraftingObjectID();
      RecipeFilter var4 = Settings.getRecipeFilterSetting(ObjectRegistry.getObject(var3));
      this.craftList.setFilter(var4);
      FormFlow var5 = new FormFlow(this.craftingForm.getWidth() - 4);
      this.filterButton = (FormContentIconToggleButton)this.craftingForm.addComponent(new FormContentIconToggleButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_search_24, Settings.UI.button_search_24, new GameMessage[]{new LocalMessage("ui", "craftfilters")}));
      this.filterButton.useDownTexture = false;
      this.filterButton.onToggled((var0) -> {
         Settings.hasCraftingFilterExpanded.set(((FormButtonToggle)var0.from).isToggled());
         Settings.saveClientSettings();
      });
      this.filterButton.setToggled((Boolean)Settings.hasCraftingFilterExpanded.get());
      FormContentIconToggleButton var6 = (FormContentIconToggleButton)this.craftingForm.addComponent((new FormContentIconToggleButton(var5.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_expanded_24, Settings.UI.button_expanded_24, new GameMessage[0]) {
         protected void addTooltips(PlayerMob var1) {
            super.addTooltips(var1);
            if (this.isToggled()) {
               Screen.addTooltip(new StringTooltips(Localization.translate("ui", "collapse")), TooltipLocation.FORM_FOCUS);
            } else {
               Screen.addTooltip(new StringTooltips(Localization.translate("ui", "expand")), TooltipLocation.FORM_FOCUS);
            }

         }
      }).onMirrorY());
      var6.useDownTexture = false;
      var6.onToggled((var2x) -> {
         Settings.hasCraftingListExpanded.set(((FormButtonToggle)var2x.from).isToggled());
         Settings.saveClientSettings();
         this.getManager().setNextControllerFocus(var6);
         ControllerInput.submitNextRefreshFocusEvent();
      });
      var6.setToggled((Boolean)Settings.hasCraftingListExpanded.get());
      Settings.hasCraftingListExpanded.addChangeListener((var1x) -> {
         this.craftingForm.setHeight(var1x ? 300 : 180);
         this.craftList.setHeight(this.craftingForm.getHeight() - 46);
         this.onlyCraftable.setPosition(5, this.craftingForm.getHeight() - 32);
         this.useNearby.setPosition(5, this.craftingForm.getHeight() - 16);
         this.onWindowResized();
      }, this::isDisposed);
      this.settlementObjectFormManager = var2.settlementObjectManager.getFormManager(this, this.craftingForm, var1);
      this.settlementObjectFormManager.addConfigButtonRow(this.craftingForm, var5, 4, -1);
      this.makeCurrent(this.craftingForm);
      this.filterForm = new CraftFilterForm("filtersForm", 148, 0, 180, var4);
      this.filterForm.setPosition(new FormRelativePosition(this.craftingForm, this.craftingForm.getWidth() + Settings.UI.formSpacing, 0));
      this.filterForm.setHidden(!this.filterButton.isToggled());
   }

   protected void init() {
      super.init();
      this.getManager().addComponent(this.filterForm);
      if (this.rangeElement != null) {
         this.rangeElement.remove();
      }

      this.rangeElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            if (CraftingStationContainerForm.this.useNearby.isHovering()) {
               final SharedTextureDrawOptions var4 = ((CraftingStationContainer)CraftingStationContainerForm.this.container).range.getDrawOptions(new Color(255, 255, 255, 200), new Color(255, 255, 255, 75), ((CraftingStationContainer)CraftingStationContainerForm.this.container).objectX, ((CraftingStationContainer)CraftingStationContainerForm.this.container).objectY, var2);
               if (var4 != null) {
                  var1.add(new SortedDrawable() {
                     public int getPriority() {
                        return -1000000;
                     }

                     public void draw(TickManager var1) {
                        var4.draw();
                     }
                  });
               }

            }
         }
      };
      this.client.getLevel().hudManager.addElement(this.rangeElement);
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      if (this.isCurrent(this.craftingForm) && var1.state && Screen.isKeyDown(341) && var1.getID() == 70) {
         var1.use();
         this.filterButton.setToggled(true);
         Settings.hasCraftingFilterExpanded.set(true);
         this.filterForm.selectAllTextAndSetTypingTrue();
      }

      super.handleInputEvent(var1, var2, var3);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.craftingForm);
      this.settlementObjectFormManager.onWindowResized();
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.filterForm.setHidden(!this.isCurrent(this.craftingForm) || !(Boolean)Settings.hasCraftingFilterExpanded.get());
      this.settlementObjectFormManager.updateButtons();
      if (this.useNearby.isHovering()) {
         Screen.fadeHUD();
      }

      super.draw(var1, var2, var3);
   }

   public void dispose() {
      super.dispose();
      if (this.filterForm != null) {
         this.getManager().removeComponent(this.filterForm);
         this.filterForm = null;
      }

      Settings.hasCraftingListExpanded.cleanListeners();
      Settings.hasCraftingFilterExpanded.cleanListeners();
      if (this.rangeElement != null) {
         this.rangeElement.remove();
      }

   }
}
