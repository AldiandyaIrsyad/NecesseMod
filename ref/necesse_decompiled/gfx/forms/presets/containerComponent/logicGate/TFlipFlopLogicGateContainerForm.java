package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.TFlipFlopLogicGateContainer;

public class TFlipFlopLogicGateContainerForm<T extends TFlipFlopLogicGateContainer> extends LogicGateContainerForm<T> {
   public TFlipFlopLogicGateContainerForm(Client var1, T var2) {
      super(var1, 400, 160, var2);
      this.addComponent(new FormLocalLabel(var2.entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
      this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireinputs"), var2.entity, (var0) -> {
         return var0.wireInputs;
      }, var2.setInputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 135, 45, 100, false));
      this.addWireCheckboxes(150, 40, new LocalMessage("ui", "wireoutputs"), var2.entity, (var0) -> {
         return var0.wireOutputs1;
      }, var2.setOutputs1);
      this.addWireCheckboxes(270, 40, (GameMessage)null, var2.entity, (var0) -> {
         return var0.wireOutputs2;
      }, var2.setOutputs2);
   }
}
