package necesse.gfx.forms.presets.containerComponent.settlement;

import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;

public interface SettlementSubForm {
   void onSetCurrent(boolean var1);

   GameMessage getMenuButtonName();

   String getTypeString();

   default void onMenuButtonClicked(FormSwitcher var1) {
      var1.makeCurrent((FormComponent)this);
   }

   default SettlementToolHandler getToolHandler() {
      return null;
   }
}
