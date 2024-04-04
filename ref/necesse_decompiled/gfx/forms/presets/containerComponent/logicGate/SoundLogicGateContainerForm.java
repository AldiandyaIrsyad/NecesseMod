package necesse.gfx.forms.presets.containerComponent.logicGate;

import necesse.engine.GameEventListener;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormHorizontalIntScroll;
import necesse.gfx.forms.components.FormHorizontalScroll;
import necesse.gfx.forms.components.lists.FormStringSelectList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.logicGate.SoundLogicGateContainer;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.gameLogicGate.entities.SoundLogicGateEntity;

public class SoundLogicGateContainerForm<T extends SoundLogicGateContainer> extends LogicGateContainerForm<T> {
   private final GameEventListener<LogicGateEntity.ApplyPacketEvent> applyListener;
   public FormStringSelectList sounds;
   public FormHorizontalIntScroll semitone;

   public SoundLogicGateContainerForm(Client var1, final T var2) {
      super(var1, 400, 160, var2);
      this.addComponent(new FormLocalLabel(var2.entity.getLogicGate().getLocalization(), new FontOptions(20), -1, 4, 4));
      this.sounds = (FormStringSelectList)this.addComponent(new FormStringSelectList(0, 40, 160, this.getHeight() - 40, SoundLogicGateEntity.getSoundNames()));
      this.sounds.setSelected(var2.entity.sound);
      this.sounds.onSelect((var2x) -> {
         this.playTest();
         var2.setSound.runAndSend(var2x.index);
      });
      this.addComponent(new FormLocalLabel("ui", "soundsemitone", new FontOptions(16), 0, 250, 50));
      this.semitone = (FormHorizontalIntScroll)this.addComponent(new FormHorizontalIntScroll(220, 75, 60, FormHorizontalScroll.DrawOption.value, new LocalMessage("ui", "soundsemitone"), var2.entity.semitone, -12, 12));
      this.semitone.onChanged((var2x) -> {
         this.playTest();
         var2.setSemitone.runAndSend((Integer)((FormHorizontalScroll)var2x.from).getValue());
      });
      this.applyListener = (GameEventListener)var2.entity.applyPacketEvents.addListener(new GameEventListener<LogicGateEntity.ApplyPacketEvent>() {
         public void onEvent(LogicGateEntity.ApplyPacketEvent var1) {
            SoundLogicGateContainerForm.this.sounds.setSelected(var2.entity.sound);
            SoundLogicGateContainerForm.this.semitone.setValue(var2.entity.semitone);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEvent(Object var1) {
            this.onEvent((LogicGateEntity.ApplyPacketEvent)var1);
         }
      });
   }

   public void playTest() {
      int var1 = this.sounds.getSelectedIndex();
      SoundLogicGateEntity.playSound(var1, (Integer)this.semitone.getValue(), ((SoundLogicGateContainer)this.container).entity.tileX * 32 + 16, ((SoundLogicGateContainer)this.container).entity.tileY * 32 + 16);
   }

   public void dispose() {
      super.dispose();
      this.applyListener.dispose();
   }
}
