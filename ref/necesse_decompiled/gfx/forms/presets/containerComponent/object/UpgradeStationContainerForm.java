package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.lists.FormRecipeList;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.BackgroundedGameTooltips;
import necesse.gfx.gameTooltips.CompareGameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.MinWidthGameTooltips;
import necesse.gfx.gameTooltips.OffsetGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryUpdateListener;
import necesse.inventory.container.object.UpgradeStationContainer;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.hudManager.HudDrawElement;

public class UpgradeStationContainerForm<T extends UpgradeStationContainer> extends ContainerFormSwitcher<T> {
   public Form mainForm = (Form)this.addComponent(new Form(400, 120));
   public UpgradedItem lastUpgradedItem;
   private int upgradeContentY;
   private FormContentBox upgradeContent;
   protected FormLocalCheckBox useNearby;
   protected FormLocalTextButton upgradeButton;
   protected HudDrawElement rangeElement;
   protected FormRecipeList recipeUpdateListener;
   protected InventoryUpdateListener inventoryUpdateListener;

   public UpgradeStationContainerForm(Client var1, final T var2) {
      super(var1, var2);
      FormFlow var3 = new FormFlow(5);
      this.mainForm.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel(var2.upgradeEntity.getObject().getLocalization(), new FontOptions(20), 0, this.mainForm.getWidth() / 2, 5), 5));
      this.mainForm.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel("ui", "upgradeplaceitem", new FontOptions(16), 0, this.mainForm.getWidth() / 2, 4, this.mainForm.getWidth() - 10), 5));
      ((FormContainerSlot)this.mainForm.addComponent(new FormContainerSlot(var1, var2, var2.UPGRADE_SLOT, this.mainForm.getWidth() / 2 - 20, var3.next(45)))).setDecal(Settings.UI.inventoryslot_icon_trinket);
      this.upgradeContentY = var3.next();
      short var4 = 300;
      int var5 = Math.min(this.mainForm.getWidth() - 8, var4);
      this.upgradeButton = (FormLocalTextButton)this.mainForm.addComponent(new FormLocalTextButton("ui", "applyupgradebutton", this.mainForm.getWidth() / 2 - var5 / 2, var3.next(28), var5, FormInputSize.SIZE_24, ButtonColor.BASE) {
         protected void addTooltips(PlayerMob var1) {
            super.addTooltips(var1);
            if (UpgradeStationContainerForm.this.lastUpgradedItem != null) {
               ListGameTooltips var2x = new ListGameTooltips();
               if (UpgradeStationContainerForm.this.lastUpgradedItem.cost != null && UpgradeStationContainerForm.this.lastUpgradedItem.cost.length > 0) {
                  var2x.add(Localization.translate("misc", "recipecostsing"));
                  CanCraft var3 = var2.canUpgrade(UpgradeStationContainerForm.this.lastUpgradedItem, true);

                  for(int var4 = 0; var4 < UpgradeStationContainerForm.this.lastUpgradedItem.cost.length; ++var4) {
                     Ingredient var5 = UpgradeStationContainerForm.this.lastUpgradedItem.cost[var4];
                     var2x.add((Object)var5.getTooltips(var3.haveIngredients[var4], true));
                  }
               }

               GameBlackboard var8 = (new GameBlackboard()).set("compareItem", UpgradeStationContainerForm.this.lastUpgradedItem.lastItem).set("hideModifierAndRewards", true);
               ListGameTooltips var9 = UpgradeStationContainerForm.this.lastUpgradedItem.lastItem.item.getTooltips(UpgradeStationContainerForm.this.lastUpgradedItem.lastItem, var1, new GameBlackboard());
               ListGameTooltips var10 = UpgradeStationContainerForm.this.lastUpgradedItem.upgradedItem.item.getTooltips(UpgradeStationContainerForm.this.lastUpgradedItem.upgradedItem, var1, var8);
               CompareGameTooltips var6 = new CompareGameTooltips(new BackgroundedGameTooltips(var9, GameBackground.getItemTooltipBackground()), new BackgroundedGameTooltips(var10, GameBackground.getItemTooltipBackground()), 5, false);
               ListGameTooltips var7 = new ListGameTooltips();
               var7.add((Object)(new OffsetGameTooltips(new BackgroundedGameTooltips(new MinWidthGameTooltips(var2x, var6.getWidth() - 5), GameBackground.getItemTooltipBackground()), var6.getDrawXOffset())));
               var7.add((Object)(new SpacerGameTooltip(5)));
               var7.add((Object)var6);
               Screen.addTooltip(var7, TooltipLocation.FORM_FOCUS);
            }

         }
      });
      this.upgradeButton.onClicked((var1x) -> {
         var2.upgradeButton.runAndSend();
      });
      this.useNearby = (FormLocalCheckBox)this.mainForm.addComponent(new FormLocalCheckBox("ui", "usenearbyinv", 5, var3.next(20), (Boolean)Settings.craftingUseNearby.get()), 100);
      Settings.craftingUseNearby.addChangeListener((var1x) -> {
         this.useNearby.checked = var1x;
         GlobalData.updateCraftable();
      }, this::isDisposed);
      this.useNearby.onClicked((var0) -> {
         Settings.craftingUseNearby.set(((FormCheckBox)var0.from).checked);
      });
      this.lastUpgradedItem = var2.getUpgradedItem();
      this.updateFormContent();
      this.makeCurrent(this.mainForm);
   }

   protected void init() {
      super.init();
      if (this.rangeElement != null) {
         this.rangeElement.remove();
      }

      this.rangeElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, GameCamera var2, PlayerMob var3) {
            if (UpgradeStationContainerForm.this.useNearby.isHovering()) {
               final SharedTextureDrawOptions var4 = ((UpgradeStationContainer)UpgradeStationContainerForm.this.container).ingredientRange.getDrawOptions(new Color(255, 255, 255, 200), new Color(255, 255, 255, 75), ((UpgradeStationContainer)UpgradeStationContainerForm.this.container).upgradeEntity.getTileX(), ((UpgradeStationContainer)UpgradeStationContainerForm.this.container).upgradeEntity.getTileY(), var2);
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
      GlobalData.craftingLists.add(this.recipeUpdateListener = new FormRecipeList() {
         public void updateCraftable() {
            UpgradeStationContainerForm.this.updateFormContent();
         }

         public void updateRecipes() {
         }
      });
      this.inventoryUpdateListener = ((UpgradeStationContainer)this.container).upgradeEntity.inventory.addSlotUpdateListener(new InventoryUpdateListener() {
         public void onSlotUpdate(int var1) {
            UpgradeStationContainerForm.this.lastUpgradedItem = ((UpgradeStationContainer)UpgradeStationContainerForm.this.container).getUpgradedItem();
            UpgradeStationContainerForm.this.updateFormContent();
         }

         public boolean isDisposed() {
            return UpgradeStationContainerForm.this.isDisposed();
         }
      });
   }

   public void updateFormContent() {
      if (this.upgradeContent != null) {
         this.mainForm.removeComponent(this.upgradeContent);
      }

      FormFlow var1 = new FormFlow(this.upgradeContentY);
      this.upgradeContent = (FormContentBox)this.mainForm.addComponent(new FormContentBox(0, var1.next(), this.mainForm.getWidth(), 300));
      FormFlow var2 = new FormFlow();
      CanCraft var3 = null;
      if (this.lastUpgradedItem != null && this.lastUpgradedItem.upgradedItem.item instanceof UpgradableItem) {
         var3 = ((UpgradeStationContainer)this.container).canUpgrade(this.lastUpgradedItem, true);
         FormFairTypeLabel var7;
         if (this.lastUpgradedItem.lastItem != null && this.lastUpgradedItem.lastItem.item instanceof UpgradableItem) {
            this.upgradeContent.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("ui", "upgradesto", new FontOptions(20), 0, this.upgradeContent.getWidth() / 2, 0, this.upgradeContent.getWidth() - 10), 2));
            this.upgradeContent.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel("ui", "upgradesbasestats", new FontOptions(12), 0, this.upgradeContent.getWidth() / 2, 0, this.upgradeContent.getWidth() - 10), 5));
            ItemStatTipList var9 = new ItemStatTipList();
            ((UpgradableItem)this.lastUpgradedItem.upgradedItem.item).addUpgradeStatTips(var9, this.lastUpgradedItem.lastItem, this.lastUpgradedItem.upgradedItem, this.client.getPlayer(), (Mob)null);
            Iterator var11 = var9.iterator();

            while(var11.hasNext()) {
               ItemStatTip var13 = (ItemStatTip)var11.next();
               var7 = new FormFairTypeLabel("", this.upgradeContent.getMinContentWidth() / 2, 0);
               var7.setMaxWidth(this.upgradeContent.getMinContentWidth() - 10);
               var7.setTextAlign(FairType.TextAlign.CENTER);
               var7.setCustomFairType(var13.toFairType(new FontOptions(16), Settings.UI.successTextColor, Settings.UI.errorTextColor, Settings.UI.warningTextColor, true));
               this.upgradeContent.addComponent((FormFairTypeLabel)var2.nextY(var7, 2));
            }

            var2.next(10);
         }

         FontOptions var10 = new FontOptions(16);
         this.upgradeContent.addComponent((FormLocalLabel)var2.nextY(new FormLocalLabel(new LocalMessage("ui", "upgradescost"), new FontOptions(20), 0, this.upgradeContent.getWidth() / 2, 0), 2));

         for(int var12 = 0; var12 < this.lastUpgradedItem.cost.length; ++var12) {
            Ingredient var14 = this.lastUpgradedItem.cost[var12];
            var7 = new FormFairTypeLabel("", this.upgradeContent.getMinContentWidth() / 2, 0);
            var7.setMaxWidth(this.upgradeContent.getMinContentWidth() - 10);
            var7.setTextAlign(FairType.TextAlign.CENTER);
            FairType var8 = var14.getTooltipText(var10, var3.haveIngredients[var12], Settings.UI.activeTextColor, Settings.UI.errorTextColor, true);
            var7.setCustomFairType(var8);
            this.upgradeContent.addComponent((FormFairTypeLabel)var2.nextY(var7, 2));
         }

         var2.next(10);
      } else {
         InventoryItem var4 = ((UpgradeStationContainer)this.container).getSlot(((UpgradeStationContainer)this.container).UPGRADE_SLOT).getItem();
         if (var4 != null) {
            String var5 = Localization.translate("ui", "itemnotupgradable");
            if (var4.item instanceof UpgradableItem) {
               String var6 = ((UpgradableItem)var4.item).getCanBeUpgradedError(var4);
               if (var6 != null) {
                  var5 = var6;
               }
            }

            this.upgradeContent.addComponent((FormLabel)var2.nextY(new FormLabel(var5, (new FontOptions(16)).color(Settings.UI.errorTextColor), 0, this.upgradeContent.getWidth() / 2, 0, this.upgradeContent.getWidth() - 10), 2));
            var2.next(10);
         }
      }

      if (this.upgradeContent.getHeight() > var2.next()) {
         this.upgradeContent.setHeight(var2.next());
      }

      this.upgradeContent.setContentBox(new Rectangle(this.upgradeContent.getWidth(), var2.next()));
      var1.next(this.upgradeContent.getHeight());
      var1.nextY(this.upgradeButton, 4);
      var1.nextY(this.useNearby, 4);
      this.upgradeButton.setActive(this.lastUpgradedItem != null && var3 != null && var3.canCraft());
      this.mainForm.setHeight(var1.next());
      this.onWindowResized();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.useNearby.isHovering()) {
         Screen.fadeHUD();
      }

      super.draw(var1, var2, var3);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this.mainForm);
   }

   public boolean shouldOpenInventory() {
      return true;
   }

   public void dispose() {
      super.dispose();
      Settings.hasCraftingListExpanded.cleanListeners();
      Settings.hasCraftingFilterExpanded.cleanListeners();
      if (this.rangeElement != null) {
         this.rangeElement.remove();
      }

      if (this.recipeUpdateListener != null) {
         GlobalData.craftingLists.remove(this.recipeUpdateListener);
      }

      if (this.inventoryUpdateListener != null) {
         this.inventoryUpdateListener.dispose();
      }

   }
}
