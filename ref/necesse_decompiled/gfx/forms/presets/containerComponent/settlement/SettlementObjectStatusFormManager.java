package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.Inventory;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class SettlementObjectStatusFormManager {
   private final SettlementDependantContainer container;
   private final SettlementContainerObjectStatusManager manager;
   private final FormSwitcher switcher;
   private final FormComponent defaultForm;
   private final Client client;
   public FormContentIconButton configureStorageButton;
   public SettlementStorageConfigForm storageConfigForm;
   public boolean openStorageConfig;
   public FormContentIconButton configureWorkstationButton;
   public SettlementWorkstationConfigForm workstationConfigForm;
   public boolean openWorkstationConfig;

   public SettlementObjectStatusFormManager(SettlementDependantContainer var1, SettlementContainerObjectStatusManager var2, FormSwitcher var3, FormComponent var4, Client var5) {
      this.container = var1;
      this.manager = var2;
      this.switcher = var3;
      this.defaultForm = var4;
      this.client = var5;
      if (var2.canSettlementStorageConfigure) {
         var1.onEvent(SettlementBasicsEvent.class, (var1x) -> {
            this.updateConfigureButtons();
         });
         var1.onEvent(SettlementOpenStorageConfigEvent.class, (var2x) -> {
            var2.isSettlementStorage = true;
            if (this.openStorageConfig) {
               this.setupConfigStorage(var2x);
            }

            this.updateConfigureButtons();
         });
         var1.onEvent(SettlementSingleStorageEvent.class, (var1x) -> {
            this.updateConfigureButtons();
         });
         var1.onEvent(SettlementStorageChangeAllowedEvent.class, (var1x) -> {
            if (this.storageConfigForm != null && this.storageConfigForm.tile.x == var1x.tileX && this.storageConfigForm.tile.y == var1x.tileY) {
               if (var1x.isItems) {
                  Item[] var2 = var1x.items;
                  int var3 = var2.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                     Item var5 = var2[var4];
                     this.storageConfigForm.filter.setItemAllowed(var5, var1x.allowed);
                     this.storageConfigForm.filterForm.updateAllButtons();
                  }
               } else {
                  ItemCategoriesFilter.ItemCategoryFilter var6 = this.storageConfigForm.filter.getItemCategory(var1x.category.id);
                  var6.setAllowed(var1x.allowed);
                  this.storageConfigForm.filterForm.updateButtons(var1x.category);
               }
            }

         });
         var1.onEvent(SettlementStorageLimitsEvent.class, (var1x) -> {
            if (this.storageConfigForm != null && this.storageConfigForm.tile.x == var1x.tileX && this.storageConfigForm.tile.y == var1x.tileY) {
               if (var1x.isItems) {
                  this.storageConfigForm.filter.setItemAllowed(var1x.item, var1x.limits);
                  this.storageConfigForm.filterForm.updateAllButtons();
               } else {
                  ItemCategoriesFilter.ItemCategoryFilter var2 = this.storageConfigForm.filter.getItemCategory(var1x.category.id);
                  var2.setMaxItems(var1x.maxItems);
                  this.storageConfigForm.filterForm.updateButtons(var1x.category);
               }
            }

         });
         var1.onEvent(SettlementStoragePriorityLimitEvent.class, (var1x) -> {
            if (this.storageConfigForm != null && this.storageConfigForm.tile.x == var1x.tileX && this.storageConfigForm.tile.y == var1x.tileY) {
               if (var1x.isPriority) {
                  this.storageConfigForm.updatePrioritySelect(var1x.priority);
               } else {
                  this.storageConfigForm.filter.limitMode = var1x.limitMode;
                  this.storageConfigForm.filter.maxAmount = var1x.limit;
                  this.storageConfigForm.updateLimitMode();
                  this.storageConfigForm.updateLimitInput();
               }
            }

         });
         var1.onEvent(SettlementStorageFullUpdateEvent.class, (var1x) -> {
            if (this.storageConfigForm != null && this.storageConfigForm.tile.x == var1x.tileX && this.storageConfigForm.tile.y == var1x.tileY) {
               this.storageConfigForm.updatePrioritySelect(var1x.priority);
               this.storageConfigForm.filter.readPacket(new PacketReader(var1x.filterContent));
               this.storageConfigForm.updateLimitInput();
               this.storageConfigForm.filterForm.updateAllButtons();
            }

         });
      }

      if (var2.canSettlementWorkstationConfigure) {
         var1.onEvent(SettlementSingleWorkstationsEvent.class, (var1x) -> {
            this.updateConfigureButtons();
         });
         var1.onEvent(SettlementOpenWorkstationEvent.class, (var2x) -> {
            var2.isSettlementWorkstation = true;
            if (this.openWorkstationConfig) {
               this.setupConfigWorkstation(var2x);
            }

            this.updateConfigureButtons();
         });
         var1.onEvent(SettlementWorkstationEvent.class, (var2x) -> {
            if (this.workstationConfigForm != null && var3.isCurrent(this.workstationConfigForm) && this.workstationConfigForm.tile.x == var2x.tileX && this.workstationConfigForm.tile.y == var2x.tileY) {
               this.workstationConfigForm.setRecipes(var2x.recipes);
            }

         });
         var1.onEvent(SettlementWorkstationRecipeUpdateEvent.class, (var2x) -> {
            if (this.workstationConfigForm != null && var3.isCurrent(this.workstationConfigForm) && this.workstationConfigForm.tile.x == var2x.tileX && this.workstationConfigForm.tile.y == var2x.tileY) {
               this.workstationConfigForm.onRecipeUpdate(var2x);
            }

         });
         var1.onEvent(SettlementWorkstationRecipeRemoveEvent.class, (var2x) -> {
            if (this.workstationConfigForm != null && var3.isCurrent(this.workstationConfigForm) && this.workstationConfigForm.tile.x == var2x.tileX && this.workstationConfigForm.tile.y == var2x.tileY) {
               this.workstationConfigForm.onRecipeRemove(var2x);
            }

         });
      }

   }

   public void onStorageConfigBack() {
      this.switcher.makeCurrent(this.defaultForm);
   }

   public void onWorkstationConfigBack() {
      this.switcher.makeCurrent(this.defaultForm);
   }

   public boolean addStorageConfigButton(Form var1, int var2, int var3) {
      if (this.manager.canSettlementStorageConfigure) {
         this.configureStorageButton = (FormContentIconButton)var1.addComponent(new FormContentIconButton(var2, var3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_storage_add, new GameMessage[0]) {
            public GameTooltips getTooltips(PlayerMob var1) {
               StringTooltips var2;
               if (!this.isActive()) {
                  if (!SettlementObjectStatusFormManager.this.manager.foundSettlement) {
                     return new StringTooltips(Localization.translate("ui", "settlementnotfound"));
                  } else {
                     var2 = new StringTooltips(Localization.translate("ui", "settlementispriv"));
                     var2.add(Localization.translate("ui", "settlementprivatetip"), (GameColor)GameColor.LIGHT_GRAY, 400);
                     return var2;
                  }
               } else if (SettlementObjectStatusFormManager.this.manager.isSettlementStorage) {
                  return new StringTooltips(Localization.translate("ui", "settlementconfigurestorage"));
               } else {
                  var2 = new StringTooltips(Localization.translate("ui", "settlementaddstorage"));
                  var2.add(Localization.translate("ui", "settlementstoragetip"), (GameColor)GameColor.LIGHT_GRAY, 400);
                  return var2;
               }
            }
         });
         this.configureStorageButton.onClicked((var1x) -> {
            this.openStorageConfig = true;
            this.manager.openSettlementStorageConfig.runAndSend();
         });
         this.configureStorageButton.setCooldown(500);
         this.updateConfigureButtons();
         return true;
      } else {
         return false;
      }
   }

   public boolean addWorkstationConfigButton(Form var1, int var2, int var3) {
      if (this.manager.canSettlementWorkstationConfigure) {
         this.configureWorkstationButton = (FormContentIconButton)var1.addComponent(new FormContentIconButton(var2, var3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_storage_add, new GameMessage[0]) {
            public GameTooltips getTooltips(PlayerMob var1) {
               StringTooltips var2;
               if (!this.isActive()) {
                  if (!SettlementObjectStatusFormManager.this.manager.foundSettlement) {
                     return new StringTooltips(Localization.translate("ui", "settlementnotfound"));
                  } else {
                     var2 = new StringTooltips(Localization.translate("ui", "settlementispriv"));
                     var2.add(Localization.translate("ui", "settlementprivatetip"), (GameColor)GameColor.LIGHT_GRAY, 400);
                     return var2;
                  }
               } else if (SettlementObjectStatusFormManager.this.manager.isSettlementWorkstation) {
                  return new StringTooltips(Localization.translate("ui", "settlementconfigureworkstation"));
               } else {
                  var2 = new StringTooltips(Localization.translate("ui", "settlementaddworkstation"));
                  var2.add(Localization.translate("ui", "settlementworkstationtip"), (GameColor)GameColor.LIGHT_GRAY, 400);
                  return var2;
               }
            }
         });
         this.configureWorkstationButton.onClicked((var1x) -> {
            this.openWorkstationConfig = true;
            this.manager.openWorkstationConfig.runAndSend();
         });
         this.configureWorkstationButton.setCooldown(500);
         this.updateConfigureButtons();
         return true;
      } else {
         return false;
      }
   }

   public void addConfigButtonRow(Form var1, FormFlow var2, int var3, int var4) {
      if (this.addWorkstationConfigButton(var1, var2.next() - 24, var3)) {
         var2.next(26 * var4);
      }

      if (this.addStorageConfigButton(var1, var2.next() - 24, var3)) {
         var2.next(26 * var4);
      }

   }

   public void setupConfigStorage(final SettlementOpenStorageConfigEvent var1) {
      ObjectEntity var3 = this.manager.level.entityManager.getObjectEntity(var1.tileX, var1.tileY);
      Inventory var4 = null;
      GameMessage var5 = this.manager.level.getObjectName(var1.tileX, var1.tileY);
      ItemCategoriesFilter var2;
      if (var3 instanceof OEInventory) {
         var2 = var1.getFilter((OEInventory)var3);
         var5 = ((OEInventory)var3).getInventoryName();
         var4 = ((OEInventory)var3).getInventory();
      } else {
         var2 = new ItemCategoriesFilter(false);
      }

      int var6 = this.manager.subscribeStorage.subscribe(new Point(var1.tileX, var1.tileY));
      SettlementStorageConfigForm var7 = (SettlementStorageConfigForm)this.switcher.addComponent(new SettlementStorageConfigForm("storageConfig", 500, 350, new Point(var1.tileX, var1.tileY), this.client, var4, var5, var2, var1.priority) {
         public void onItemsChanged(Item[] var1x, boolean var2) {
            SettlementObjectStatusFormManager.this.manager.changeAllowedStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onItemLimitsChanged(Item var1x, ItemCategoriesFilter.ItemLimits var2) {
            SettlementObjectStatusFormManager.this.manager.changeLimitsStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1x, boolean var2) {
            SettlementObjectStatusFormManager.this.manager.changeAllowedStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter var1x, int var2) {
            SettlementObjectStatusFormManager.this.manager.changeLimitsStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onFullChange(ItemCategoriesFilter var1x, int var2) {
            SettlementObjectStatusFormManager.this.manager.fullUpdateSettlementStorage.runAndSend(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onPriorityChange(int var1x) {
            SettlementObjectStatusFormManager.this.manager.priorityLimitStorage.runAndSendPriority(var1.tileX, var1.tileY, var1x);
         }

         public void onLimitChange(ItemCategoriesFilter.ItemLimitMode var1x, int var2) {
            SettlementObjectStatusFormManager.this.manager.priorityLimitStorage.runAndSendLimit(var1.tileX, var1.tileY, var1x, var2);
         }

         public void onRemove() {
            SettlementObjectStatusFormManager.this.manager.removeStorage.runAndSend(var1.tileX, var1.tileY);
            SettlementObjectStatusFormManager.this.onStorageConfigBack();
         }

         public void onBack() {
            SettlementObjectStatusFormManager.this.onStorageConfigBack();
         }
      }, (var2x, var3x) -> {
         if (!var3x) {
            this.switcher.removeComponent(var2x);
            this.storageConfigForm = null;
            this.manager.subscribeStorage.unsubscribe(var6);
         }

      });
      this.switcher.makeCurrent(var7);
      this.storageConfigForm = var7;
      this.storageConfigForm.setPosFocus();
      this.openStorageConfig = false;
   }

   public void setupConfigWorkstation(final SettlementOpenWorkstationEvent var1) {
      SettlementWorkstationLevelObject var2 = null;
      GameObject var3 = this.manager.level.getObject(var1.tileX, var1.tileY);
      String var4 = var3.getDisplayName();
      if (var3 instanceof SettlementWorkstationObject) {
         var2 = new SettlementWorkstationLevelObject(this.manager.level, var1.tileX, var1.tileY);
      }

      if (var2 != null) {
         int var5 = this.manager.subscribeWorkstation.subscribe(new Point(var1.tileX, var1.tileY));
         SettlementWorkstationConfigForm var6 = (SettlementWorkstationConfigForm)this.switcher.addComponent(new SettlementWorkstationConfigForm("workstationConfig", 400, 240, new Point(var1.tileX, var1.tileY), this.client, new StaticMessage(var4), this.container.client.playerMob, var2, var1.recipes) {
            public void onSubmitRemove(int var1x) {
               SettlementObjectStatusFormManager.this.manager.removeWorkstationRecipe.runAndSend(var1.tileX, var1.tileY, var1x);
            }

            public void onSubmitUpdate(int var1x, SettlementWorkstationRecipe var2) {
               SettlementObjectStatusFormManager.this.manager.updateWorkstationRecipe.runAndSend(var1.tileX, var1.tileY, var1x, var2);
            }

            public void onRemove() {
               SettlementObjectStatusFormManager.this.manager.removeWorkstation.runAndSend(var1.tileX, var1.tileY);
               SettlementObjectStatusFormManager.this.onWorkstationConfigBack();
            }

            public void onBack() {
               SettlementObjectStatusFormManager.this.onWorkstationConfigBack();
            }
         }, (var2x, var3x) -> {
            if (!var3x) {
               this.manager.subscribeWorkstation.unsubscribe(var5);
               this.switcher.removeComponent(var2x);
               this.workstationConfigForm = null;
            }

         });
         this.switcher.makeCurrent(var6);
         this.workstationConfigForm = var6;
         this.workstationConfigForm.setPosFocus();
         this.openWorkstationConfig = false;
      }

   }

   protected void updateConfigureButtons() {
      if (this.configureStorageButton != null) {
         this.configureStorageButton.setActive(this.manager.hasSettlementAccess && this.manager.canSettlementStorageConfigure);
         if (this.manager.isSettlementStorage) {
            this.configureStorageButton.setIcon(Settings.UI.container_storage_config);
            this.configureStorageButton.color = ButtonColor.BASE;
         } else {
            this.configureStorageButton.setIcon(Settings.UI.container_storage_add);
            this.configureStorageButton.color = this.configureStorageButton.isActive() ? ButtonColor.GREEN : ButtonColor.BASE;
            if (this.storageConfigForm != null && this.switcher.isCurrent(this.storageConfigForm)) {
               this.onStorageConfigBack();
            }
         }
      }

      if (this.configureWorkstationButton != null) {
         this.configureWorkstationButton.setActive(this.manager.hasSettlementAccess && this.manager.canSettlementWorkstationConfigure);
         if (this.manager.isSettlementWorkstation) {
            this.configureWorkstationButton.setIcon(Settings.UI.container_storage_config);
            this.configureWorkstationButton.color = ButtonColor.BASE;
         } else {
            this.configureWorkstationButton.setIcon(Settings.UI.container_storage_add);
            this.configureWorkstationButton.color = this.configureWorkstationButton.isActive() ? ButtonColor.GREEN : ButtonColor.BASE;
            if (this.workstationConfigForm != null && this.switcher.isCurrent(this.workstationConfigForm)) {
               this.onWorkstationConfigBack();
            }
         }
      }

   }

   public void updateButtons() {
      this.updateConfigureButtons();
   }

   public void onWindowResized() {
      if (this.storageConfigForm != null) {
         this.storageConfigForm.setPosFocus();
      }

      if (this.workstationConfigForm != null) {
         this.workstationConfigForm.setPosFocus();
      }

   }
}
