package necesse.gfx.forms.presets.containerComponent.mob;

import necesse.engine.network.client.Client;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.inventory.container.mob.MobContainer;

public abstract class MobContainerFormSwitcher<T extends MobContainer> extends ContainerFormSwitcher<T> {
   protected Mob mob;

   public MobContainerFormSwitcher(Client var1, T var2) {
      super(var1, var2);
      this.mob = var2.getMob();
   }
}
