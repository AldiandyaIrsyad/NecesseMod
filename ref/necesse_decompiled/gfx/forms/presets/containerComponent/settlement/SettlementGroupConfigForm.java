package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementGroupConfigForm extends Form {
   public final SettlementContainerForm<?> parent;
   public int maxHeight;
   public int headerHeight;
   public int contentHeight;
   protected FormLocalLabel header;
   protected FormContentIconButton allCheckbox;
   protected FormContentBox content;
   protected boolean allSelected;
   protected HashSet<Integer> selectedSettlers = new HashSet();

   public SettlementGroupConfigForm(int var1, int var2, SettlementContainerForm<?> var3) {
      super(var1, 300);
      this.maxHeight = var2;
      this.parent = var3;
      this.header = (FormLocalLabel)this.addComponent(new FormLocalLabel("ui", "settlementcommand", new FontOptions(20), 0, this.getWidth() / 2, this.headerHeight));
      this.headerHeight += 25;
      this.allCheckbox = (FormContentIconButton)this.addComponent(new FormContentIconButton(4, this.headerHeight, FormInputSize.SIZE_20, ButtonColor.BASE, (ButtonIcon)null, new GameMessage[0]));
      this.allCheckbox.onClicked((var2x) -> {
         if (this.allSelected) {
            this.selectedSettlers.clear();
            this.allCheckbox.setIcon((ButtonIcon)null);
         } else {
            Iterator var3x = var3.settlers.iterator();

            while(var3x.hasNext()) {
               SettlementSettlerBasicData var4 = (SettlementSettlerBasicData)var3x.next();
               this.selectedSettlers.add(var4.mobUniqueID);
            }

            this.allCheckbox.setIcon(Settings.UI.button_checked_20);
         }

         this.updateSettlers();
      });
      this.headerHeight += 22;
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 0, this.headerHeight, this.getWidth(), true));
      this.content = (FormContentBox)this.addComponent(new FormContentBox(0, this.headerHeight, this.getWidth(), this.getHeight() - this.headerHeight));
      ((SettlementContainer)var3.getContainer()).onEvent(SettlementSettlerBasicsEvent.class, (var1x) -> {
         this.updateSettlers();
      });
   }

   public void updateSettlers() {
      this.content.clearComponents();
      FormFlow var1 = new FormFlow();
      this.addSettlers(var1, this.parent.settlers);
      this.updateAllSelected();
      if (this.parent.settlers.isEmpty()) {
         this.content.alwaysShowVerticalScrollBar = false;
         var1.next(16);
         this.content.addComponent((FormLocalLabel)var1.nextY(new FormLocalLabel("ui", "settlersnosettlers", new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 16));
      } else {
         this.content.alwaysShowVerticalScrollBar = true;
      }

      this.contentHeight = Math.max(var1.next(), 70);
      this.updateSize();
   }

   public void updateSize() {
      this.setHeight(Math.min(this.maxHeight, this.content.getY() + this.contentHeight + this.headerHeight));
      this.content.setContentBox(new Rectangle(0, 0, this.content.getWidth(), this.contentHeight));
      this.content.setWidth(this.getWidth());
      this.content.setHeight(this.getHeight() - this.content.getY());
      ContainerComponent.setPosInventory(this);
   }

   public void updateAllSelected() {
      this.allSelected = !this.parent.settlers.isEmpty() && this.parent.settlers.stream().allMatch((var1) -> {
         return this.selectedSettlers.contains(var1.mobUniqueID);
      });
      this.allCheckbox.setIcon(this.allSelected ? Settings.UI.button_checked_20 : null);
   }

   private void addSettlers(FormFlow var1, List<SettlementSettlerBasicData> var2) {
      var1.next(2);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         SettlementSettlerBasicData var4 = (SettlementSettlerBasicData)var3.next();
         SettlerMob var5 = var4.getSettlerMob(this.parent.getClient().getLevel());
         if (var5 != null) {
            byte var6 = 22;
            FormFlow var7 = new FormFlow(4);
            FormContentIconButton var8 = (FormContentIconButton)this.content.addComponent(new FormContentIconButton(var7.next(24), var1.next() + 1, FormInputSize.SIZE_20, ButtonColor.BASE, (ButtonIcon)null, new GameMessage[0]));
            if (this.selectedSettlers.contains(var4.mobUniqueID)) {
               var8.setIcon(Settings.UI.button_checked_20);
            }

            var8.onClicked((var3x) -> {
               if (this.selectedSettlers.contains(var4.mobUniqueID)) {
                  this.selectedSettlers.remove(var4.mobUniqueID);
                  var8.setIcon((ButtonIcon)null);
               } else {
                  this.selectedSettlers.add(var4.mobUniqueID);
                  var8.setIcon(Settings.UI.button_checked_20);
               }

               this.updateAllSelected();
            });
            this.content.addComponent(new FormLabel(var5.getSettlerName() + " (" + var5.getSettler().getGenericMobName() + ")", new FontOptions(16), -1, var7.next(), var1.next() + 3, this.content.getWidth() - var7.next() - this.content.getScrollBarWidth()));
            var1.next(var6);
         } else {
            GameLog.warn.println("Could not find settler mob with id " + var4.mobUniqueID);
         }
      }

      var1.next(2);
   }
}
