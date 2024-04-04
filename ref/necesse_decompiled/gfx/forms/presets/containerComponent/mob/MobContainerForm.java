package necesse.gfx.forms.presets.containerComponent.mob;

import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.inventory.container.mob.MobContainer;

public class MobContainerForm<T extends MobContainer> extends ContainerForm<T> {
   protected Mob mob;

   public MobContainerForm(Client var1, int var2, int var3, T var4) {
      super(var1, var2, var3, var4);
      this.mob = var4.getMob();
   }
}
