package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.GameEventListener;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormHorizontalIntScroll;
import necesse.gfx.forms.components.FormHorizontalScroll;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.SensorLogicGateContainer;
import necesse.level.gameLogicGate.entities.LogicGateEntity;

public class SensorLogicGateContainerForm<T extends SensorLogicGateContainer> extends LogicGateContainerForm<T> {
   private final GameEventListener<LogicGateEntity.ApplyPacketEvent> applyListener;

   public SensorLogicGateContainerForm(Client var1, final T var2) {
      super(var1, 400, 160, var2);
      this.addComponent(new FormLocalLabel(var2.entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
      this.addWireCheckboxes(10, 40, new LocalMessage("ui", "wireoutputs"), var2.entity, (var0) -> {
         return var0.wireOutputs;
      }, var2.setOutputs);
      this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 150, 45, 100, false));
      this.addComponent(new FormLocalLabel(new LocalMessage("ui", "sensorlabel"), new FontOptions(20), -1, 160, 40));
      final FormCheckBox var3 = ((FormLocalCheckBox)this.addComponent(new FormLocalCheckBox("ui", "sensorplayers", 160, 65, var2.entity.players))).onClicked((var1x) -> {
         var2.setPlayers.runAndSend(((FormCheckBox)var1x.from).checked);
      });
      final FormCheckBox var4 = ((FormLocalCheckBox)this.addComponent(new FormLocalCheckBox("ui", "sensorhostile", 160, 85, var2.entity.hostileMobs))).onClicked((var1x) -> {
         var2.setHostileMobs.runAndSend(((FormCheckBox)var1x.from).checked);
      });
      final FormCheckBox var5 = ((FormLocalCheckBox)this.addComponent(new FormLocalCheckBox("ui", "sensorpassive", 160, 105, var2.entity.passiveMobs))).onClicked((var1x) -> {
         var2.setPassiveMobs.runAndSend(((FormCheckBox)var1x.from).checked);
      });
      final FormHorizontalScroll var6 = ((FormHorizontalIntScroll)this.addComponent(new FormHorizontalIntScroll(160, 125, 100, FormHorizontalScroll.DrawOption.valueOnHover, new LocalMessage("ui", "sensorrange"), var2.entity.range, 1, 5))).onChanged((var1x) -> {
         var2.setRange.runAndSend((Integer)((FormHorizontalScroll)var1x.from).getValue());
      });
      this.applyListener = (GameEventListener)var2.entity.applyPacketEvents.addListener(new GameEventListener<LogicGateEntity.ApplyPacketEvent>() {
         public void onEvent(LogicGateEntity.ApplyPacketEvent var1) {
            var3.checked = var2.entity.players;
            var4.checked = var2.entity.hostileMobs;
            var5.checked = var2.entity.passiveMobs;
            var6.setValue(var2.entity.range);
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
