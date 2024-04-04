package necesse.gfx.forms.presets.containerComponent.mob;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameUtils;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.lists.FormContainerShopList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.mob.ShopContainer;

public class ShopTradingForm<T extends ShopContainer> extends ContainerForm<T> {
   public final boolean isSell;

   public ShopTradingForm(Client var1, T var2, int var3, int var4, boolean var5, GameMessage var6, int var7, FormEventListener<FormInputEvent<FormButton>> var8) {
      super(var1, var3, var4, var2);
      this.isSell = var5;
      String var9 = MobRegistry.getLocalization(var2.humanShop.getID()).translate();
      String var10 = GameUtils.maxString(var9, new FontOptions(20), var3 - var7 - 10);
      this.addComponent(new FormLabel(var10, new FontOptions(20), -1, 5, 5));
      this.addComponent(new FormLocalLabel(new LocalMessage("ui", var5 ? "tradersell" : "traderbuy"), new FontOptions(16), -1, 5, 28));
      this.addComponent(new FormContainerShopList(0, 30, this.getWidth(), this.getHeight() - 30, var1, var2, var5));
      if (var6 != null) {
         FormLocalTextButton var11 = (FormLocalTextButton)this.addComponent(new FormLocalTextButton(var6, this.getWidth() - var7 - 4, 4, var7, FormInputSize.SIZE_20, ButtonColor.BASE));
         if (var8 != null) {
            var11.onClicked(var8);
         }
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      ContainerComponent.setPosFocus(this);
   }
}
