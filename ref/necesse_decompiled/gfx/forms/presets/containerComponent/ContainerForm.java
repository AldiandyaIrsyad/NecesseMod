package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.inventory.container.Container;

public class ContainerForm<T extends Container> extends Form implements ContainerComponent<T> {
   protected T container;
   protected Client client;

   public ContainerForm(Client var1, int var2, int var3, T var4) {
      super("focus", var2, var3);
      this.client = var1;
      this.container = var4;
      this.container.form = this;
   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this);
   }

   public T getContainer() {
      return this.container;
   }

   public boolean shouldOpenInventory() {
      return true;
   }
}
