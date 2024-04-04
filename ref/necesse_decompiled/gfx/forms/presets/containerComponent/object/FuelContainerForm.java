package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import java.util.function.Supplier;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.BooleanCustomAction;

public class FuelContainerForm extends Form {
   private FormLocalCheckBox keepRunningCheckbox;
   private Supplier<Boolean> keepRunningGetter;
   private Supplier<Float> fuelProgressGetter;

   public FuelContainerForm(Client var1, Container var2, int var3, int var4, GameTexture var5, boolean var6, BooleanCustomAction var7, Supplier<Boolean> var8, final Supplier<Float> var9) {
      super(120, 0);
      this.keepRunningGetter = var8;
      this.fuelProgressGetter = var9;
      byte var10 = 2;
      int var11 = var4 - var3 + 1;
      int var12 = (var11 + var10 - 1) / var10;
      this.setHeight(var12 * 40 + 60);
      if (!var6 && var7 != null) {
         this.keepRunningCheckbox = (FormLocalCheckBox)this.addComponent(new FormLocalCheckBox("ui", "fuelkeeprunning", 5, this.getHeight() - 10, (Boolean)var8.get(), this.getWidth() - 10));
         this.keepRunningCheckbox.onClicked((var1x) -> {
            var7.runAndSend(((FormCheckBox)var1x.from).checked);
         });
         Rectangle var13 = this.keepRunningCheckbox.getBoundingBox();
         this.keepRunningCheckbox.setPosition(this.getWidth() / 2 - var13.width / 2, this.keepRunningCheckbox.getY());
         this.setHeight(this.getHeight() + var13.height);
      }

      this.addComponent(new FormLocalLabel("ui", "fuel", new FontOptions(16), 0, this.getWidth() / 2, 5));
      this.addComponent(new FormCustomDraw(this.getWidth() / 2 - 40, 26, 80, Settings.UI.progressbar_small_empty.getHeight()) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            FormComponent.drawWidthComponent(new GameSprite(Settings.UI.progressbar_small_empty, 0, 0, Settings.UI.progressbar_small_empty.getHeight()), new GameSprite(Settings.UI.progressbar_small_empty, 1, 0, Settings.UI.progressbar_small_empty.getHeight()), this.getX(), this.getY(), this.width);
            float var4 = (Float)var9.get();
            int var5 = (int)(var4 * (float)this.width);
            FormComponent.drawWidthComponent(new GameSprite(Settings.UI.progressbar_small_full, 0, 0, Settings.UI.progressbar_small_full.getHeight()), new GameSprite(Settings.UI.progressbar_small_full, 1, 0, Settings.UI.progressbar_small_full.getHeight()), this.getX(), this.getY(), var5);
            if (this.isHovering()) {
               Screen.addTooltip(new StringTooltips((int)(var4 * 100.0F) + "%"), TooltipLocation.FORM_FOCUS);
            }

         }
      });

      for(int var20 = var3; var20 <= var4; ++var20) {
         int var14 = var20 - var3;
         int var15 = var14 % var10;
         int var16 = var14 / var10;
         int var17 = Math.min(var11 - var16 * var10, var10);
         int var18 = var17 * 20;
         FormContainerSlot var19 = new FormContainerSlot(var1, var2, var20, this.getWidth() / 2 + var15 * 40 - var18, var16 * 40 + 40);
         if (var5 != null) {
            var19.setDecal(var5);
         }

         this.addComponent(var19);
      }

   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      if (this.keepRunningCheckbox != null && this.keepRunningCheckbox.checked != (Boolean)this.keepRunningGetter.get()) {
         this.keepRunningCheckbox.checked = (Boolean)this.keepRunningGetter.get();
      }

      super.draw(var1, var2, var3);
   }
}
