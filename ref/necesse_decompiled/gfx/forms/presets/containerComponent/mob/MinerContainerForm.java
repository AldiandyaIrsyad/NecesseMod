package necesse.gfx.forms.presets.containerComponent.mob;

import necesse.engine.network.client.Client;
import necesse.inventory.container.mob.MinerContainer;

public class MinerContainerForm<T extends MinerContainer> extends ShopContainerForm<T> {
   public MinerContainerForm(Client var1, T var2) {
      super(var1, var2, 408, 170, 240);
   }
}
