package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.CounterLogicGateContainer;

public class CounterLogicGateContainerForm<T extends CounterLogicGateContainer> extends LogicGateContainerForm<T> {
   public CounterLogicGateContainerForm(Client var1, T var2) {
      super(var1, 660, 160, var2);
      this.addComponent(new FormLocalLabel(var2.entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
      this.addWireCheckboxes(10, 40, new LocalMessage("ui", "counterinc"), var2.entity, (var0) -> {
         return var0.incInputs;
      }, var2.setIncInputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 130, 45, 100, false));
      this.addWireCheckboxes(140, 40, new LocalMessage("ui", "counterdec"), var2.entity, (var0) -> {
         return var0.decInputs;
      }, var2.setDecInputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 260, 45, 100, false));
      this.addWireCheckboxes(270, 40, new LocalMessage("ui", "rsreset"), var2.entity, (var0) -> {
         return var0.resetInputs;
      }, var2.setResetInputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 390, 45, 100, false));
      this.addWireCheckboxes(400, 40, new LocalMessage("ui", "wireoutputs"), var2.entity, (var0) -> {
         return var0.wireOutputs;
      }, var2.setOutputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 520, 45, 100, false));
      this.addComponent(new FormLocalLabel("ui", "countermax", new FontOptions(20), -1, 530, 40));
      FormSlider var3 = ((FormSlider)this.addComponent(new FormSlider("", 530, 65, var2.entity.getMaxValue(), 1, 256, 100))).onGrab((var1x) -> {
         if (!var1x.grabbed) {
            var2.setMaxValue.runAndSend(((FormSlider)var1x.from).getValue());
         }

      });
      var3.onScroll((var1x) -> {
         var2.setMaxValue.runAndSend(((FormSlider)var1x.from).getValue());
      });
      var3.setValue(var2.entity.getMaxValue());
      var3.drawValueInPercent = false;
   }
}
