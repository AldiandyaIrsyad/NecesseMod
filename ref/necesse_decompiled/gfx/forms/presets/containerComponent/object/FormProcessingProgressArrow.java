package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.SpacerGameTooltip;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormProcessingProgressArrow extends FormCustomDraw {
   protected ProcessingHelp help;

   public FormProcessingProgressArrow(int var1, int var2, ProcessingHelp var3) {
      super(var1, var2, 32, 32);
      this.help = var3;
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Settings.UI.processing_arrow_empty.initDraw().draw(this.getX(), this.getY());
      if (this.help.isProcessing()) {
         float var4 = this.help.getProcessingProgress();
         byte var5 = 2;
         byte var6 = 2;
         int var7 = Settings.UI.processing_arrow_full.getWidth() - (var5 + var6);
         Settings.UI.processing_arrow_full.initDraw().section(var5, var5 + (int)((float)var7 * var4), 0, Settings.UI.processing_arrow_full.getHeight()).draw(this.getX() + var5, this.getY());
         if (this.isHovering()) {
            ListGameTooltips var8 = new ListGameTooltips();
            GameTooltips var9 = this.help.getCurrentRecipeTooltip(var2);
            if (var9 != null) {
               var8.add((Object)var9);
               var8.add((Object)(new SpacerGameTooltip(5)));
            }

            var8.add((int)(var4 * 100.0F) + "%");
            if (var9 != null) {
               Screen.addTooltip(var8, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            } else {
               Screen.addTooltip(var8, TooltipLocation.FORM_FOCUS);
            }
         }
      } else if (this.isHovering()) {
         GameTooltips var10 = this.help.getCurrentRecipeTooltip(var2);
         if (var10 != null) {
            ListGameTooltips var11 = new ListGameTooltips(var10);
            if (this.help.needsFuel()) {
               var11.add((Object)(new StringTooltips(Localization.translate("ui", "needfuel"), GameColor.RED)));
            }

            Screen.addTooltip(var11, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
         }
      }

   }
}
