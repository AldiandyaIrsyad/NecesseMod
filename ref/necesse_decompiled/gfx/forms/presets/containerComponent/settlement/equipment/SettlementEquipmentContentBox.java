package necesse.gfx.forms.presets.containerComponent.settlement.equipment;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemPreview;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.SavedFormContentBoxScroll;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentFiltersForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.settlement.data.SettlementSettlerEquipmentFilterData;
import necesse.inventory.item.Item;
import necesse.inventory.item.SettlerWeaponItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementEquipmentContentBox extends FormContentBox {
   public static SavedFormContentBoxScroll lastScroll = new SavedFormContentBoxScroll();
   public final SettlementEquipmentForm<?> equipmentsForm;
   public ArrayList<SettlementEquipmentForm.PasteButton> pasteButtons = new ArrayList();
   public int contentHeight;

   public SettlementEquipmentContentBox(SettlementEquipmentForm<?> var1, int var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.equipmentsForm = var1;
   }

   public void updateEquipmentsContent() {
      this.clearComponents();
      this.pasteButtons.clear();
      FormFlow var1 = new FormFlow(0);
      boolean var2 = false;
      int var3 = 0;
      Comparator var4 = Comparator.comparing((var0) -> {
         return var0.settler.getID();
      });
      var4 = var4.thenComparing((var0) -> {
         return var0.mobUniqueID;
      });
      this.equipmentsForm.settlers.sort(var4);
      Iterator var5 = this.equipmentsForm.settlers.iterator();

      while(var5.hasNext()) {
         SettlementSettlerEquipmentFilterData var6 = (SettlementSettlerEquipmentFilterData)var5.next();
         SettlerMob var7 = var6.getSettlerMob(this.equipmentsForm.client.getLevel());
         if (var7 != null) {
            Mob var8 = var7.getMob();
            if (var8 instanceof HumanMob) {
               final HumanMob var9 = (HumanMob)var8;
               var2 = true;
               int var10 = var1.next(32);
               byte var11 = 24;
               int var12 = this.getWidth() - var11 - this.getScrollBarWidth() - 2;
               FormContentIconButton var13 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var12, var10 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_storage_config, new GameMessage[]{new LocalMessage("ui", "settlerfilterequipment")}) {
                  public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
                     this.setActive((Boolean)var9.selfManageEquipment.get());
                     super.draw(var1, var2, var3);
                  }
               });
               var13.onClicked((var3x) -> {
                  EquipmentFiltersForm var4 = new EquipmentFiltersForm("changeequipmentfilter", 408, 300, var8, var6.equipmentFilter, () -> {
                     return var6.preferArmorSets;
                  }, this.equipmentsForm.client) {
                     public void onSetPreferArmorSets(boolean var1x) {
                        var2.preferArmorSets = var1x;
                        SettlementEquipmentContentBox.this.equipmentsForm.container.setSettlerEquipmentFilter.runAndSendPreferArmorSets(var1.getUniqueID(), var1x);
                     }

                     public void onItemsChanged(Item[] var1x, boolean var2x) {
                        SettlementEquipmentContentBox.this.equipmentsForm.container.setSettlerEquipmentFilter.runAndSendChange(var1.getUniqueID(), ItemCategoriesFilterChange.itemsAllowed(var1x, var2x));
                     }

                     public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1x, boolean var2x) {
                        SettlementEquipmentContentBox.this.equipmentsForm.container.setSettlerEquipmentFilter.runAndSendChange(var1.getUniqueID(), ItemCategoriesFilterChange.categoryAllowed(var1x, var2x));
                     }

                     public void onFullChange(ItemCategoriesFilter var1x) {
                        SettlementEquipmentContentBox.this.equipmentsForm.container.setSettlerEquipmentFilter.runAndSendChange(var1.getUniqueID(), ItemCategoriesFilterChange.fullChange(var1x));
                     }

                     public void onButtonPressed() {
                        SettlementEquipmentContentBox.this.equipmentsForm.makeCurrent(SettlementEquipmentContentBox.this.equipmentsForm.mainForm);
                     }

                     public void onWindowResized() {
                        super.onWindowResized();
                        ContainerComponent.setPosInventory(this);
                     }
                  };
                  this.equipmentsForm.addAndMakeCurrentTemporary(var4);
                  var4.onWindowResized();
               });
               var12 -= 24;
               FormContentIconButton var14 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var12, var10 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}) {
                  public boolean isActive() {
                     return super.isActive() && (Boolean)var9.selfManageEquipment.get();
                  }
               });
               var14.onClicked((var4x) -> {
                  EquipmentFiltersForm.EquipmentFilterData var5 = (EquipmentFiltersForm.EquipmentFilterData)this.equipmentsForm.listClipboard.getValue();
                  if (var5 != null) {
                     SaveData var6x = new SaveData("");
                     var5.filter.addSaveData(var6x);
                     var6.preferArmorSets = var5.preferArmorSets;
                     var6.equipmentFilter.applyLoadData(var6x.toLoadData());
                     this.equipmentsForm.container.setSettlerEquipmentFilter.runAndSendPreferArmorSets(var8.getUniqueID(), var6.preferArmorSets);
                     this.equipmentsForm.container.setSettlerEquipmentFilter.runAndSendChange(var8.getUniqueID(), ItemCategoriesFilterChange.fullChange(var6.equipmentFilter));
                     var14.setActive(false);
                  }

               });
               var14.setupDragPressOtherButtons("equipmentPasteButton");
               this.pasteButtons.add(new SettlementEquipmentForm.PasteButton(var14, () -> {
                  return var6.preferArmorSets;
               }, var6.equipmentFilter));
               var12 -= 24;
               FormContentIconButton var15 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var12, var10 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}) {
                  public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
                     this.setActive((Boolean)var9.selfManageEquipment.get());
                     super.draw(var1, var2, var3);
                  }
               });
               var15.onClicked((var2x) -> {
                  EquipmentFiltersForm.EquipmentFilterData var3 = new EquipmentFiltersForm.EquipmentFilterData(var6.preferArmorSets, var6.equipmentFilter);
                  Screen.putClipboard(var3.getSaveData().getScript());
                  this.equipmentsForm.listClipboard.forceUpdate();
               });
               var12 -= 24;
               FormContentIconToggleButton var16 = (FormContentIconToggleButton)this.addComponent(new FormContentIconToggleButton(var12, var10 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.button_checked_20, Settings.UI.button_escaped_20, new GameMessage[]{new LocalMessage("ui", "settlerselfmanagequipment")}) {
                  public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
                     this.setToggled((Boolean)var9.selfManageEquipment.get());
                     super.draw(var1, var2, var3);
                  }
               });
               var16.onToggled((var3x) -> {
                  var9.selfManageEquipment.set(((FormButtonToggle)var3x.from).isToggled());
                  this.equipmentsForm.container.setSettlerSelfManageEquipment.runAndSend(var8.getUniqueID(), ((FormButtonToggle)var3x.from).isToggled());
               });
               var16.setupDragToOtherButtons("selfManageEquipment");
               var12 -= 4;
               var12 -= 32;
               this.addArmorItemPreview(var9, var12, var10, 5, ArmorItem.ArmorType.FEET, true);
               var12 -= 32;
               this.addArmorItemPreview(var9, var12, var10, 4, ArmorItem.ArmorType.CHEST, true);
               var12 -= 32;
               this.addArmorItemPreview(var9, var12, var10, 3, ArmorItem.ArmorType.HEAD, true);
               var12 -= 4;
               this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, var12, var10 + 4, 24, false));
               var12 -= 4;
               var12 -= 32;
               this.addArmorItemPreview(var9, var12, var10, 2, ArmorItem.ArmorType.FEET, false);
               var12 -= 32;
               this.addArmorItemPreview(var9, var12, var10, 1, ArmorItem.ArmorType.CHEST, false);
               var12 -= 32;
               this.addArmorItemPreview(var9, var12, var10, 0, ArmorItem.ArmorType.HEAD, false);
               var12 -= 4;
               this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, var12, var10 + 4, 24, false));
               var12 -= 4;
               var12 -= 32;
               this.addWeaponItemPreview(var9, var12, var10, 6);
               String var17 = var7.getSettlerName();
               this.addComponent(new FormSettlerIcon(5, var10, var6.settler, var8, this.equipmentsForm.containerForm));
               byte var18 = 37;
               int var19 = var12 - var18;
               FontOptions var20 = new FontOptions(16);
               this.addComponent(new FormLabel(GameUtils.maxString(var17, var20, var19), var20, -1, var18, var10, var19));
               FontOptions var21 = new FontOptions(12);
               this.addComponent(new FormLabel(GameUtils.maxString(var6.settler.getGenericMobName(), var21, var19), var21, -1, var18, var10 + 16));
            }
         } else {
            ++var3;
         }
      }

      this.equipmentsForm.listClipboard.forceUpdate();
      this.equipmentsForm.listClipboard.onUpdate((EquipmentFiltersForm.EquipmentFilterData)this.equipmentsForm.listClipboard.getValue());
      if (!var2) {
         this.alwaysShowVerticalScrollBar = false;
         var1.next(16);
         this.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "settlersnosettlers", new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 16));
      } else {
         this.alwaysShowVerticalScrollBar = true;
      }

      if (var3 > 0) {
         this.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementoutsidesettlers", new Object[]{"count", var3}), new FontOptions(16), -1, 10, 0), 5));
      }

      this.contentHeight = Math.max(var1.next(), 70);
      this.equipmentsForm.updateSize();
      if (!this.equipmentsForm.settlers.isEmpty()) {
         lastScroll.load(this);
      }

   }

   private void addArmorItemPreview(final HumanMob var1, int var2, int var3, final int var4, final ArmorItem.ArmorType var5, final boolean var6) {
      final GameTexture var7;
      if (var5 == ArmorItem.ArmorType.HEAD) {
         var7 = var6 ? Settings.UI.inventoryslot_icon_hat : Settings.UI.inventoryslot_icon_helmet;
      } else if (var5 == ArmorItem.ArmorType.CHEST) {
         var7 = var6 ? Settings.UI.inventoryslot_icon_shirt : Settings.UI.inventoryslot_icon_chestplate;
      } else if (var5 == ArmorItem.ArmorType.FEET) {
         var7 = var6 ? Settings.UI.inventoryslot_icon_shoes : Settings.UI.inventoryslot_icon_boots;
      } else {
         var7 = null;
      }

      this.addComponent(new FormItemPreview(var2, var3, 32, (Item)null) {
         public InventoryItem getDrawItem(PlayerMob var1x) {
            return var1.equipmentInventory.getItem(var4);
         }

         public void drawEmpty(TickManager var1x, PlayerMob var2, Rectangle var3) {
            if (var7 != null) {
               var7.initDraw().color(Settings.UI.inactiveTextColor).draw(this.getX(), this.getY());
            }

         }

         public boolean allowControllerFocus() {
            return true;
         }

         public void addTooltips(InventoryItem var1x, PlayerMob var2) {
            ListGameTooltips var3 = new ListGameTooltips();
            if (var1x != null) {
               if (var1x.item.isArmorItem()) {
                  GameBlackboard var4x = (new GameBlackboard()).set("isCosmeticSlot", var6).set("equippedMob", var1).set("setBonus", var1.equipmentBuffManager.getSetBonusBuffTooltip(new GameBlackboard()));
                  var3.add((Object)var1x.item.getTooltips(var1x, var2, var4x));
               } else {
                  var3.add(Localization.translate("ui", "settlercantuseitem"));
               }
            } else if (var5 == ArmorItem.ArmorType.HEAD) {
               var3.add(Localization.translate("itemtooltip", (var6 ? "cosmetic" : "") + "headslot"));
            } else if (var5 == ArmorItem.ArmorType.CHEST) {
               var3.add(Localization.translate("itemtooltip", (var6 ? "cosmetic" : "") + "chestslot"));
            } else if (var5 == ArmorItem.ArmorType.FEET) {
               var3.add(Localization.translate("itemtooltip", (var6 ? "cosmetic" : "") + "feetslot"));
            }

            Screen.addTooltip(var3, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
         }
      });
   }

   private void addWeaponItemPreview(final HumanMob var1, int var2, int var3, final int var4) {
      this.addComponent(new FormItemPreview(var2, var3, 32, (Item)null) {
         public InventoryItem getDrawItem(PlayerMob var1x) {
            return var1.equipmentInventory.getItem(var4);
         }

         public void drawEmpty(TickManager var1x, PlayerMob var2, Rectangle var3) {
            Settings.UI.inventoryslot_icon_weapon.initDraw().color(Settings.UI.inactiveTextColor).draw(this.getX(), this.getY());
         }

         public boolean allowControllerFocus() {
            return true;
         }

         public void addTooltips(InventoryItem var1x, PlayerMob var2) {
            ListGameTooltips var3 = new ListGameTooltips();
            if (var1x != null) {
               if (var1x.item instanceof SettlerWeaponItem) {
                  GameBlackboard var4x = (new GameBlackboard()).set("perspective", var1);
                  var3.add((Object)var1x.item.getTooltips(var1x, (PlayerMob)null, var4x));
               } else {
                  var3.add(Localization.translate("ui", "settlercantuseitem"));
               }
            } else {
               var3.add(Localization.translate("itemtooltip", "weaponslot"));
            }

            Screen.addTooltip(var3, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
         }
      });
   }

   public void updatePasteButtons(EquipmentFiltersForm.EquipmentFilterData var1) {
      Iterator var2 = this.pasteButtons.iterator();

      while(var2.hasNext()) {
         SettlementEquipmentForm.PasteButton var3 = (SettlementEquipmentForm.PasteButton)var2.next();
         var3.updateActive(var1);
      }

   }

   public void dispose() {
      lastScroll.save(this);
      super.dispose();
   }
}
