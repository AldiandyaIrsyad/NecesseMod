package necesse.gfx.forms.presets.containerComponent.settlement;

import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.events.SettlementHusbandryZoneUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;

public class SettlementHusbandryZoneConfigForm extends FormSwitcher implements WorkZoneConfigComponent {
   public SettlementAssignWorkForm<?> assignWork;
   public SettlementHusbandryZone zone;
   public Form configForm;
   protected ConfirmationForm deleteConfirm;
   public FormLabelEdit label;
   public FormContentIconButton renameButton;
   public LocalMessage renameTip;
   public FormTextInput maxAnimalsInput;

   public SettlementHusbandryZoneConfigForm(SettlementAssignWorkForm<?> var1, SettlementHusbandryZone var2, Runnable var3) {
      this.assignWork = var1;
      this.zone = var2;
      this.deleteConfirm = (ConfirmationForm)this.addComponent(new ConfirmationForm("delete", 300, 200));
      this.configForm = (Form)this.addComponent(new Form(400, 200));
      FormFlow var4 = new FormFlow(5);
      FontOptions var5 = new FontOptions(20);
      this.label = (FormLabelEdit)this.configForm.addComponent((FormLabelEdit)var4.nextY(new FormLabelEdit("", var5, Settings.UI.activeTextColor, 5, 5, 10, 50)), -1000);
      this.label.onMouseChangedTyping((var1x) -> {
         this.runLabelUpdate();
      });
      this.label.onSubmit((var1x) -> {
         this.runLabelUpdate();
      });
      this.label.allowItemAppend = true;
      this.label.setParsers(OEInventoryContainerForm.getParsers(var5));
      this.label.setText(var2.getName().translate());
      FormFlow var6 = new FormFlow(this.configForm.getWidth() - 4);
      ((FormContentIconButton)this.configForm.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.RED, Settings.UI.container_storage_remove, new GameMessage[]{new LocalMessage("ui", "settlementdeletezone")}))).onClicked((var4x) -> {
         this.deleteConfirm.setupConfirmation((GameMessage)(new LocalMessage("ui", "settlementareadeleteconfirm", "zone", var2.getName().translate())), () -> {
            var1.container.deleteWorkZone.runAndSend(var2.getUniqueID());
            var3.run();
         }, () -> {
            this.makeCurrent(this.configForm);
         });
         this.makeCurrent(this.deleteConfirm);
      });
      this.renameTip = new LocalMessage("ui", "renamebutton");
      this.renameButton = (FormContentIconButton)this.configForm.addComponent(new FormContentIconButton(var6.next(-26) - 24, 4, FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.container_rename, new GameMessage[]{this.renameTip}));
      this.renameButton.onClicked((var1x) -> {
         this.label.setTyping(!this.label.isTyping());
         this.runLabelUpdate();
      });
      this.label.setWidth(var6.next() - 8);
      this.configForm.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel(var2.getAbstractName(), new FontOptions(12), -1, 5, 0, this.configForm.getWidth() - 10)));
      var4.next(10);
      this.configForm.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel("ui", "maxanimalsbeforeslaughter", new FontOptions(16), -1, 5, 0, this.configForm.getWidth() - 10)));
      var4.next(5);
      int var7 = Math.min(200, this.configForm.getWidth() - 20);
      this.maxAnimalsInput = (FormTextInput)this.configForm.addComponent((FormTextInput)var4.nextY(new FormTextInput(this.configForm.getWidth() / 2 - var7 / 2, 0, FormInputSize.SIZE_24, var7, 6)));
      this.updateConfigForm();
      this.maxAnimalsInput.rightClickToClear = true;
      this.maxAnimalsInput.setRegexMatchFull("(-?[0-9]+)?");
      this.maxAnimalsInput.onSubmit((var3x) -> {
         try {
            int var4;
            if (this.maxAnimalsInput.getText().isEmpty()) {
               var4 = -1;
            } else {
               var4 = Math.max(Integer.parseInt(this.maxAnimalsInput.getText()), -1);
            }

            if (var4 != var2.getMaxAnimalsBeforeSlaughter()) {
               var2.setMaxAnimalsBeforeSlaughter(var4);
               this.updateConfigForm();
               var1.container.husbandryZoneConfig.runAndSendSetMaxAnimals(var2.getUniqueID(), var4);
            }
         } catch (NumberFormatException var5) {
            this.updateConfigForm();
         }

      });
      var4.next(20);
      ((FormLocalTextButton)this.configForm.addComponent((FormLocalTextButton)var4.nextY(new FormLocalTextButton("ui", "backbutton", 40, this.configForm.getHeight() / 2 - 12, this.configForm.getWidth() - 80, FormInputSize.SIZE_24, ButtonColor.BASE)))).onClicked((var1x) -> {
         var3.run();
      });
      var4.next(5);
      this.configForm.setHeight(var4.next());
      this.makeCurrent(this.configForm);
      this.onWindowResized();
   }

   protected void init() {
      super.init();
      this.assignWork.container.onEvent(SettlementWorkZoneNameEvent.class, (var1) -> {
         if (var1.uniqueID == this.zone.getUniqueID()) {
            if (!this.label.isTyping()) {
               this.zone.setName(var1.name);
               this.label.setText(this.zone.getName().translate());
            }

         }
      });
      this.assignWork.container.onEvent(SettlementHusbandryZoneUpdateEvent.class, (var1) -> {
         if (var1.uniqueID == this.zone.getUniqueID()) {
            this.zone.setMaxAnimalsBeforeSlaughter(var1.maxAnimalsBeforeSlaughter);
            this.updateConfigForm();
         }
      }, () -> {
         return !this.isDisposed();
      });
   }

   public void updateConfigForm() {
      if (!this.maxAnimalsInput.isTyping()) {
         if (this.zone.getMaxAnimalsBeforeSlaughter() >= 0) {
            this.maxAnimalsInput.setText(Integer.toString(this.zone.getMaxAnimalsBeforeSlaughter()), false);
         } else {
            this.maxAnimalsInput.setText("", false);
         }
      }

   }

   public void runLabelUpdate() {
      if (this.label.isTyping()) {
         this.renameButton.setIcon(Settings.UI.container_rename_save);
         this.renameTip = new LocalMessage("ui", "savebutton");
      } else {
         if (!this.label.getText().equals(this.zone.getName().translate())) {
            this.zone.setName(new StaticMessage(this.label.getText()));
            this.assignWork.container.renameWorkZone.runAndSend(this.zone.getUniqueID(), this.zone.getName());
         }

         this.renameButton.setIcon(Settings.UI.container_rename);
         this.renameTip = new LocalMessage("ui", "renamebutton");
         this.label.setText(this.zone.getName().translate());
      }

      this.renameButton.setTooltips(this.renameTip);
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosInventory(this.configForm);
   }
}
