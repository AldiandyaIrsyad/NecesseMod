package necesse.gfx.forms.presets.containerComponent.logicGate;

import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.GameEventListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.events.FormCheckboxesEventHandler;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.Container;
import necesse.inventory.container.logicGate.WireSelectCustomAction;
import necesse.level.gameLogicGate.entities.LogicGateEntity;

public class LogicGateContainerForm<T extends Container> extends ContainerForm<T> {
   private ArrayList<GameEventListener<?>> listeners = new ArrayList();

   public LogicGateContainerForm(Client var1, int var2, int var3, T var4) {
      super(var1, var2, var3, var4);
   }

   public GameMessage getWireColorName(int var1) {
      return new LocalMessage("ui", "wire" + var1);
   }

   public <V extends LogicGateEntity> FormCheckBox[] addCheckboxList(int var1, int var2, GameMessage var3, int var4, Function<Integer, GameMessage> var5, final V var6, final Function<V, boolean[]> var7) {
      if (var3 != null) {
         this.addComponent(new FormLocalLabel(var3, new FontOptions(16), -1, var1, var2));
      }

      boolean[] var8 = (boolean[])var7.apply(var6);
      final FormCheckBox[] var9 = new FormCheckBox[var4];

      for(int var10 = 0; var10 < var4; ++var10) {
         var9[var10] = (FormCheckBox)this.addComponent(new FormLocalCheckBox((GameMessage)var5.apply(var10), var1, var2 + 20 * var10 + 25));
         var9[var10].checked = var8[var10];
      }

      this.listeners.add((GameEventListener)var6.applyPacketEvents.addListener(new GameEventListener<LogicGateEntity.ApplyPacketEvent>() {
         public void onEvent(LogicGateEntity.ApplyPacketEvent var1) {
            boolean[] var2 = (boolean[])var7.apply(var6);

            for(int var3 = 0; var3 < var9.length; ++var3) {
               var9[var3].checked = var2[var3];
            }

         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onEvent(Object var1) {
            this.onEvent((LogicGateEntity.ApplyPacketEvent)var1);
         }
      }));
      return var9;
   }

   public <V extends LogicGateEntity> FormCheckBox[] addWireCheckboxes(int var1, int var2, GameMessage var3, V var4, Function<V, boolean[]> var5) {
      return this.addCheckboxList(var1, var2, var3, 4, this::getWireColorName, var4, var5);
   }

   public <V extends LogicGateEntity> FormCheckboxesEventHandler addWireCheckboxesHandler(int var1, int var2, GameMessage var3, V var4, Function<V, boolean[]> var5) {
      return new FormCheckboxesEventHandler(this.addWireCheckboxes(var1, var2, var3, var4, var5));
   }

   public <V extends LogicGateEntity> void addWireCheckboxes(int var1, int var2, GameMessage var3, V var4, Function<V, boolean[]> var5, WireSelectCustomAction var6) {
      FormCheckboxesEventHandler var7 = this.addWireCheckboxesHandler(var1, var2, var3, var4, var5);
      var7.onClicked((var2x) -> {
         var6.runAndSend(var7.getStates());
      });
   }

   public void dispose() {
      super.dispose();
      this.listeners.forEach(GameEventListener::dispose);
   }
}
