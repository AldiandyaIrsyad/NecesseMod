package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.GameEventListener;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.BufferLogicGateContainer;
import necesse.level.gameLogicGate.entities.LogicGateEntity;

public class BufferLogicGateContainerForm<T extends BufferLogicGateContainer> extends LogicGateContainerForm<T> {
   private final GameEventListener<LogicGateEntity.ApplyPacketEvent> applyListener;

   public BufferLogicGateContainerForm(Client var1, final T var2) {
      super(var1, 400, 160, var2);
      this.addComponent(new FormLocalLabel(var2.entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 135, 45, 100, false));
      this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireinputs"), var2.entity, (var0) -> {
         return var0.wireInputs;
      }, var2.setInputs);
      this.addWireCheckboxes(150, 40, new LocalMessage("ui", "wireoutputs"), var2.entity, (var0) -> {
         return var0.wireOutputs;
      }, var2.setOutputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 265, 45, 100, false));
      this.addComponent(new FormLocalLabel("ui", "bufferticks", new FontOptions(20), -1, 280, 40));
      final FormSlider var3 = ((FormSlider)this.addComponent(new FormSlider("", 280, 65, var2.entity.delayTicks, 1, 200, 100))).onGrab((var1x) -> {
         if (!var1x.grabbed) {
            var2.setDelay.runAndSend(((FormSlider)var1x.from).getValue());
         }

      });
      var3.onScroll((var1x) -> {
         var2.setDelay.runAndSend(((FormSlider)var1x.from).getValue());
      });
      var3.setValue(var2.entity.delayTicks);
      var3.drawValueInPercent = false;
      this.addComponent(new FormLocalLabel(new LocalMessage("ui", "buffertip", new Object[]{"ticks", 20}), new FontOptions(12), -1, 280, var3.getY() + var3.getTotalHeight() + 5, 100));
      this.applyListener = (GameEventListener)var2.entity.applyPacketEvents.addListener(new GameEventListener<LogicGateEntity.ApplyPacketEvent>() {
         public void onEvent(LogicGateEntity.ApplyPacketEvent var1) {
            var3.setValue(var2.entity.delayTicks);
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
