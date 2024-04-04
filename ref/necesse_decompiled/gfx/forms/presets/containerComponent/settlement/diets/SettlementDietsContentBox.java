package necesse.gfx.forms.presets.containerComponent.settlement.diets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.SavedFormContentBoxScroll;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.data.SettlementSettlerDietsData;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementDietsContentBox extends FormContentBox {
   public static SavedFormContentBoxScroll lastScroll = new SavedFormContentBoxScroll();
   public final SettlementDietsForm<?> dietsForm;
   public ArrayList<SettlementDietsForm.PasteButton> pasteButtons = new ArrayList();
   public int contentHeight;

   public SettlementDietsContentBox(SettlementDietsForm<?> var1, int var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.dietsForm = var1;
   }

   public void updateDietsContent() {
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
      this.dietsForm.settlers.sort(var4);
      Iterator var5 = this.dietsForm.settlers.iterator();

      while(var5.hasNext()) {
         SettlementSettlerDietsData var6 = (SettlementSettlerDietsData)var5.next();
         SettlerMob var7 = var6.getSettlerMob(this.dietsForm.client.getLevel());
         if (var7 != null) {
            Mob var8 = var7.getMob();
            if (var8 instanceof EntityJobWorker) {
               var2 = true;
               int var9 = var1.next(32);
               short var10 = 150;
               int var11 = this.getWidth() - var10 - this.getScrollBarWidth() - 2;
               ((FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "settlementchangediet", var11, var9 + 3, var10, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var3x) -> {
                  SettlementDietFilterForm var4 = new SettlementDietFilterForm("changediet", 408, 250, var8, var6.dietFilter, this.dietsForm.client) {
                     public void onItemsChanged(Item[] var1x, boolean var2) {
                        SettlementDietsContentBox.this.dietsForm.container.setSettlerDiet.runAndSendChange(var1.getUniqueID(), ItemCategoriesFilterChange.itemsAllowed(var1x, var2));
                     }

                     public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1x, boolean var2) {
                        SettlementDietsContentBox.this.dietsForm.container.setSettlerDiet.runAndSendChange(var1.getUniqueID(), ItemCategoriesFilterChange.categoryAllowed(var1x, var2));
                     }

                     public void onFullChange(ItemCategoriesFilter var1x) {
                        SettlementDietsContentBox.this.dietsForm.container.setSettlerDiet.runAndSendChange(var1.getUniqueID(), ItemCategoriesFilterChange.fullChange(var1x));
                     }

                     public void onButtonPressed() {
                        SettlementDietsContentBox.this.dietsForm.makeCurrent(SettlementDietsContentBox.this.dietsForm.mainForm);
                     }

                     public void onWindowResized() {
                        super.onWindowResized();
                        ContainerComponent.setPosInventory(this);
                     }
                  };
                  this.dietsForm.addAndMakeCurrentTemporary(var4);
                  var4.onWindowResized();
               });
               var11 -= 24;
               FormContentIconButton var12 = (FormContentIconButton)this.addComponent(new FormContentIconButton(var11, var9 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}));
               var12.onClicked((var4x) -> {
                  SettlementDietFilterForm.DietData var5 = (SettlementDietFilterForm.DietData)this.dietsForm.listClipboard.getValue();
                  if (var5 != null) {
                     SaveData var6x = new SaveData("");
                     var5.filter.addSaveData(var6x);
                     var6.dietFilter.applyLoadData(var6x.toLoadData());
                     this.dietsForm.container.setSettlerDiet.runAndSendChange(var8.getUniqueID(), ItemCategoriesFilterChange.fullChange(var6.dietFilter));
                     var12.setActive(false);
                  }

               });
               var12.setupDragPressOtherButtons("dietPasteButton");
               this.pasteButtons.add(new SettlementDietsForm.PasteButton(var12, var6.dietFilter));
               var11 -= 24;
               ((FormContentIconButton)this.addComponent(new FormContentIconButton(var11, var9 + 3, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((var2x) -> {
                  SettlementDietFilterForm.DietData var3 = new SettlementDietFilterForm.DietData(var6.dietFilter);
                  Screen.putClipboard(var3.getSaveData().getScript());
                  this.dietsForm.listClipboard.forceUpdate();
               });
               String var13 = var7.getSettlerName();
               this.addComponent(new FormSettlerIcon(5, var9, var6.settler, var8, this.dietsForm.containerForm));
               byte var14 = 37;
               int var15 = var11 - var14;
               FontOptions var16 = new FontOptions(16);
               this.addComponent(new FormLabel(GameUtils.maxString(var13, var16, var15), var16, -1, var14, var9, var15));
               FontOptions var17 = new FontOptions(12);
               this.addComponent(new FormLabel(GameUtils.maxString(var6.settler.getGenericMobName(), var17, var15), var17, -1, var14, var9 + 16));
            }
         } else {
            ++var3;
         }
      }

      this.dietsForm.listClipboard.forceUpdate();
      this.dietsForm.listClipboard.onUpdate((SettlementDietFilterForm.DietData)this.dietsForm.listClipboard.getValue());
      if (!var2) {
         this.alwaysShowVerticalScrollBar = false;
         var1.next(16);
         this.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "settlementnoeatingsettlers", new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 16));
      } else {
         this.alwaysShowVerticalScrollBar = true;
      }

      if (var3 > 0) {
         this.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementoutsidesettlers", new Object[]{"count", var3}), new FontOptions(16), -1, 10, 0), 5));
      }

      this.contentHeight = Math.max(var1.next(), 70);
      this.dietsForm.updateSize();
      if (!this.dietsForm.settlers.isEmpty()) {
         lastScroll.load(this);
      }

   }

   public void updatePasteButtons(SettlementDietFilterForm.DietData var1) {
      Iterator var2 = this.pasteButtons.iterator();

      while(var2.hasNext()) {
         SettlementDietsForm.PasteButton var3 = (SettlementDietsForm.PasteButton)var2.next();
         var3.updateActive(var1);
      }

   }

   public void dispose() {
      lastScroll.save(this);
      super.dispose();
   }
}
