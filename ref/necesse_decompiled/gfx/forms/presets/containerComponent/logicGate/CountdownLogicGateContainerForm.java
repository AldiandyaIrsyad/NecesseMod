package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.GameEventListener;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.events.FormCheckboxesEventHandler;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.CountdownLogicGateContainer;
import necesse.level.gameLogicGate.entities.CountdownLogicGateEntity;
import necesse.level.gameLogicGate.entities.LogicGateEntity;

public class CountdownLogicGateContainerForm<T extends CountdownLogicGateContainer> extends LogicGateContainerForm<T> {
   private final GameEventListener<LogicGateEntity.ApplyPacketEvent> applyListener;

   public CountdownLogicGateContainerForm(Client var1, final T var2) {
      super(var1, 660, 160, var2);
      this.addComponent(new FormLocalLabel(var2.entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
      this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireinputs"), var2.entity, (var0) -> {
         return var0.startInputs;
      }, var2.setStartInputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 135, 45, 100, false));
      this.addWireCheckboxes(150, 40, new LocalMessage("ui", "rsreset"), var2.entity, (var0) -> {
         return var0.resetInputs;
      }, var2.setResetInputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 265, 45, 100, false));
      this.addWireCheckboxes(280, 40, new LocalMessage("ui", "wireoutputs"), var2.entity, (var0) -> {
         return var0.wireOutputs;
      }, var2.setOutputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 395, 45, 100, false));
      FormCheckboxesEventHandler var3 = new FormCheckboxesEventHandler(this.addCheckboxList(410, 40, new LocalMessage("ui", "countdownrelaydirs"), 4, CountdownLogicGateEntity::getRelayDirName, var2.entity, (var0) -> {
         return var0.relayDirections;
      }));
      var3.onClicked((var2x) -> {
         var2.setRelayDirections.runAndSend(var3.getStates());
      });
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 535, 45, 100, false));
      FormFlow var4 = new FormFlow(40);
      this.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel("ui", "timerticks", new FontOptions(16), -1, 550, 95), 2));
      final FormTextInput var5 = (FormTextInput)this.addComponent((FormTextInput)var4.nextY(new FormTextInput(550, 65, FormInputSize.SIZE_24, 100, -1, 20), 5));
      var5.setRegexMatchFull("[0-9]+");
      var5.onSubmit((var2x) -> {
         try {
            var2.setCountdownTime.runAndSend(Integer.parseInt(var5.getText()));
         } catch (NumberFormatException var4) {
            var5.setText(String.valueOf(var2.entity.totalCountdownTime));
         }

      });
      var5.setText(String.valueOf(var2.entity.totalCountdownTime));
      this.addComponent((FormLocalLabel)var4.nextY(new FormLocalLabel(new LocalMessage("ui", "timertip", new Object[]{"ticks", 20}), new FontOptions(12), -1, 550, 95, 100), 5));
      this.applyListener = (GameEventListener)var2.entity.applyPacketEvents.addListener(new GameEventListener<LogicGateEntity.ApplyPacketEvent>() {
         public void onEvent(LogicGateEntity.ApplyPacketEvent var1) {
            var5.setText(String.valueOf(var2.entity.totalCountdownTime));
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEvent(Object var1) {
            this.onEvent((LogicGateEntity.ApplyPacketEvent)var1);
         }
      });
   }

   public void dispose() {
      super.dispose();
      this.applyListener.dispose();
   }
}
