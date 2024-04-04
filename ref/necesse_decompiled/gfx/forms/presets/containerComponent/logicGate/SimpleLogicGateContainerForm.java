package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.SimpleLogicGateContainer;

public class SimpleLogicGateContainerForm<T extends SimpleLogicGateContainer> extends LogicGateContainerForm<T> {
   public SimpleLogicGateContainerForm(Client var1, T var2) {
      super(var1, 400, 160, var2);
      this.addComponent(new FormLocalLabel(var2.entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 150, 45, 100, false));
      this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireinputs"), var2.entity, (var0) -> {
         return var0.wireInputs;
      }, var2.setInputs);
      this.addWireCheckboxes(165, 40, new LocalMessage("ui", "wireoutputs"), var2.entity, (var0) -> {
         return var0.wireOutputs;
      }, var2.setOutputs);
   }
}
