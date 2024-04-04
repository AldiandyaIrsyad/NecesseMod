package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.SRLatchLogicGateContainer;

public class SRLatchLogicGateContainerForm<T extends SRLatchLogicGateContainer> extends LogicGateContainerForm<T> {
   public SRLatchLogicGateContainerForm(Client var1, T var2) {
      super(var1, 400, 160, var2);
      this.addComponent(new FormLocalLabel(var2.entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
      this.addWireCheckboxes(10, 40, new LocalMessage("ui", "rsactivate"), var2.entity, (var0) -> {
         return var0.activateInputs;
      }, var2.setActivateInputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 130, 45, 100, false));
      this.addWireCheckboxes(140, 40, new LocalMessage("ui", "rsreset"), var2.entity, (var0) -> {
         return var0.resetInputs;
      }, var2.setResetInputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 260, 45, 100, false));
      this.addWireCheckboxes(270, 40, new LocalMessage("ui", "wireoutputs"), var2.entity, (var0) -> {
         return var0.wireOutputs;
      }, var2.setOutputs);
   }
}
