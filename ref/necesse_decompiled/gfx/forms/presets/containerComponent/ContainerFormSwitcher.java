package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.FormSwitcher;
import necesse.inventory.container.Container;

public abstract class ContainerFormSwitcher<T extends Container> extends FormSwitcher implements ContainerComponent<T> {
   protected T container;
   protected Client client;
   private boolean hidden;

   public ContainerFormSwitcher(Client var1, T var2) {
      this.client = var1;
      this.container = var2;
      this.container.form = this;
   }

   public void setHidden(boolean var1) {
      this.hidden = var1;
   }

   public boolean shouldDraw() {
      return super.shouldDraw() && !this.hidden;
   }

   public T getContainer() {
      return this.container;
   }

   public Client getClient() {
      return this.client;
   }
}
