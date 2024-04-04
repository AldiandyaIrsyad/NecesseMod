package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.inventory.container.Container;

public abstract class ContainerFormList<T extends Container> extends FormComponentList implements ContainerComponent<T> {
   protected T container;
   protected Client client;

   public ContainerFormList(Client var1, T var2) {
      this.client = var1;
      this.container = var2;
      this.container.form = this;
   }

   public Client getClient() {
      return this.client;
   }

   public T getContainer() {
      return this.container;
   }
}
