package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerEnchantSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.item.EnchantingScrollContainer;

public class EnchantingScrollContainerForm<T extends EnchantingScrollContainer> extends ContainerForm<T> {
   public FormLocalTextButton enchantButton;

   public EnchantingScrollContainerForm(Client var1, T var2) {
      super(var1, 200, 300, var2);
      FormFlow var3 = new FormFlow(10);
      this.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel(ItemRegistry.getLocalization(ItemRegistry.getItemID("enchantingscroll")), new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 10));
      if (var2.enchantment != null && var2.scrollType != null) {
         this.addComponent((FormLocalLabel)var3.nextY(new FormLocalLabel((GameMessage)var2.scrollType.enchantTip.apply(var2.enchantment), new FontOptions(16), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 10));
      }

      this.addComponent((FormContainerEnchantSlot)var3.nextY(new FormContainerEnchantSlot(var1, var2, var2.ENCHANT_SLOT, this.getWidth() / 2 - 20, 10), 10));
      int var4 = Math.min(150, this.getWidth() - 20);
      this.enchantButton = (FormLocalTextButton)this.addComponent((FormLocalTextButton)var3.nextY(new FormLocalTextButton("ui", "mageconfirm", this.getWidth() / 2 - var4 / 2, 60, var4, FormInputSize.SIZE_24, ButtonColor.BASE), 10));
      this.enchantButton.onClicked((var1x) -> {
         var2.enchantButton.runAndSend();
      });
      this.setHeight(var3.next());
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.enchantButton.setActive(((EnchantingScrollContainer)this.container).canEnchant());
      super.draw(var1, var2, var3);
   }
}
