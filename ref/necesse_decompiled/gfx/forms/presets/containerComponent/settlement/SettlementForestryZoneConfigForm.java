package necesse.gfx.forms.presets.containerComponent.settlement;

import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.events.SettlementForestryZoneUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.level.gameObject.ForestrySaplingObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.settlementData.zones.SettlementForestryZone;

public class SettlementForestryZoneConfigForm extends FormSwitcher implements WorkZoneConfigComponent {
   public SettlementAssignWorkForm<?> assignWork;
   public SettlementForestryZone zone;
   public Form configForm;
   protected ConfirmationForm deleteConfirm;
   public FormLabelEdit label;
   public FormContentIconButton renameButton;
   public LocalMessage renameTip;
   public FormCheckBox choppingAllowedCheckbox;
   public FormCheckBox replantChoppedDownTreesCheckbox;
   public FormDropdownSelectionButton<Integer> autoPlantSaplingDropdown;

   public SettlementForestryZoneConfigForm(SettlementAssignWorkForm<?> var1, SettlementForestryZone var2, Runnable var3) {
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
      this.choppingAllowedCheckbox = (FormCheckBox)this.configForm.addComponent((FormLocalCheckBox)var4.nextY(new FormLocalCheckBox("ui", "forestryzoneallowchopping", 5, 0, true, this.configForm.getWidth() - 10)));
      this.choppingAllowedCheckbox.onClicked((var2x) -> {
         var2.setChoppingAllowed(((FormCheckBox)var2x.from).checked);
         var1.container.forestryZoneConfig.runAndSendSetAllowChopping(var2.getUniqueID(), ((FormCheckBox)var2x.from).checked);
      });
      var4.next(10);
      this.replantChoppedDownTreesCheckbox = (FormCheckBox)this.configForm.addComponent((FormLocalCheckBox)var4.nextY(new FormLocalCheckBox("ui", "forestryzonereplant", 5, 0, true, this.configForm.getWidth() - 10)));
      this.replantChoppedDownTreesCheckbox.onClicked((var2x) -> {
         var2.setReplantChoppedDownTrees(((FormCheckBox)var2x.from).checked);
         var1.container.forestryZoneConfig.runAndSendSetReplantTrees(var2.getUniqueID(), ((FormCheckBox)var2x.from).checked);
      });
      LinkedList var7 = new LinkedList();
      Iterator var8 = ObjectRegistry.getObjects().iterator();

      GameObject var9;
      while(var8.hasNext()) {
         var9 = (GameObject)var8.next();
         if (var9 instanceof ForestrySaplingObject && ((ForestrySaplingObject)var9).getForestryResultObjectStringID() != null) {
            var7.add(var9);
         }
      }

      var4.next(5);
      this.configForm.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel("ui", "forestryzoneplantcustom", new FontOptions(16), -1, 5, 0, this.configForm.getWidth() - 10)));
      var4.next(5);
      this.autoPlantSaplingDropdown = (FormDropdownSelectionButton)this.configForm.addComponent((FormDropdownSelectionButton)var4.nextY(new FormDropdownSelectionButton(40, 0, FormInputSize.SIZE_24, ButtonColor.BASE, this.configForm.getWidth() - 80)));
      var8 = var7.iterator();

      while(var8.hasNext()) {
         var9 = (GameObject)var8.next();
         this.autoPlantSaplingDropdown.options.add(var9.getID(), var9.getLocalization());
      }

      this.autoPlantSaplingDropdown.onSelected((var2x) -> {
         var2.setAutoPlantSaplingID((Integer)var2x.value);
         var1.container.forestryZoneConfig.runAndSendSetAutoPlantSapling(var2.getUniqueID(), (Integer)var2x.value);
      });
      this.updateConfigForm();
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
      this.assignWork.container.onEvent(SettlementForestryZoneUpdateEvent.class, (var1) -> {
         if (var1.uniqueID == this.zone.getUniqueID()) {
            this.zone.setChoppingAllowed(var1.choppingAllowed);
            this.zone.setReplantChoppedDownTrees(var1.replantChoppedDownTrees);
            this.zone.setAutoPlantSaplingID(var1.autoPlantSaplingID);
            this.updateConfigForm();
         }
      }, () -> {
         return !this.isDisposed();
      });
   }

   public void updateConfigForm() {
      this.choppingAllowedCheckbox.checked = this.zone.isChoppingAllowed();
      this.replantChoppedDownTreesCheckbox.checked = this.zone.replantChoppedDownTrees();
      int var1 = this.zone.getAutoPlantSaplingID();
      if (var1 != -1) {
         GameObject var2 = ObjectRegistry.getObject(var1);
         this.autoPlantSaplingDropdown.setSelected(var1, var2.getLocalization());
      } else {
         this.autoPlantSaplingDropdown.setSelected(-1, new LocalMessage("ui", "selectbutton"));
      }

      this.autoPlantSaplingDropdown.setActive(!this.zone.replantChoppedDownTrees());
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
