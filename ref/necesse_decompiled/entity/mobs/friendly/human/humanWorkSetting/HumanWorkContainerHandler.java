package necesse.entity.mobs.friendly.human.humanWorkSetting;

import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.inventory.container.mob.ShopContainer;

public abstract class HumanWorkContainerHandler<T> {
   public final ShopContainer container;
   public final ShopContainer.ContainerWorkSetting<T> setting;

   public HumanWorkContainerHandler(ShopContainer var1, ShopContainer.ContainerWorkSetting<T> var2) {
      this.container = var1;
      this.setting = var2;
   }

   public abstract boolean setupWorkForm(ShopContainerForm<?> var1, DialogueForm var2);
}
