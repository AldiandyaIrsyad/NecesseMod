package necesse.gfx.forms.presets;

import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.lists.FormScoreboardList;

public class ScoreboardForm extends Form {
   private FormScoreboardList list;

   public ScoreboardForm(String var1, Client var2) {
      super((String)var1, 10, 10);
      this.addComponent(this.list = new FormScoreboardList(0, 0, this.getWidth(), this.getHeight(), var2));
      this.drawBase = false;
      this.onWindowResized();
   }

   public void fixSize() {
      int var1 = Math.min(480, Math.max(40, Screen.getHudWidth() - 600));
      int var2 = Math.min(480, Math.max(40, Screen.getHudHeight() - 400));
      this.setWidth(var1);
      this.setHeight(var2);
      this.list.setWidth(var1);
      this.list.setHeight(var2);
   }

   public void slotChanged(int var1, ClientClient var2) {
      this.list.slotChanged(var1, var2);
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.fixSize();
      this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public boolean isMouseOver(InputEvent var1) {
      InputEvent var2 = this.getComponentList().offsetEvent(var1, false);
      return this.list.isMouseOver(var2);
   }
}
