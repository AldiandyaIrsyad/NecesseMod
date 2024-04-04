package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.events.FormCheckboxesEventHandler;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.CountdownRelayLogicGateContainer;
import necesse.level.gameLogicGate.entities.CountdownLogicGateEntity;

public class CountdownRelayLogicGateContainerForm<T extends CountdownRelayLogicGateContainer> extends LogicGateContainerForm<T> {
   public CountdownRelayLogicGateContainerForm(Client var1, T var2) {
      super(var1, 400, 160, var2);
      this.addComponent(new FormLocalLabel(var2.entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
      this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireoutputs"), var2.entity, (var0) -> {
         return var0.wireOutputs;
      }, var2.setOutputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 135, 45, 100, false));
      FormCheckboxesEventHandler var3 = new FormCheckboxesEventHandler(this.addCheckboxList(150, 40, new LocalMessage("ui", "countdownrelaydirs"), 4, CountdownLogicGateEntity::getRelayDirName, var2.entity, (var0) -> {
         return var0.relayDirections;
      }));
      var3.onClicked((var2x) -> {
         var2.setRelayDirections.runAndSend(var3.getStates());
      });
   }
}
