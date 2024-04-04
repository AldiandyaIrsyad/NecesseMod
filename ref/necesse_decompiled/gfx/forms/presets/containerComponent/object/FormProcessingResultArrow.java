package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public abstract class FormProcessingResultArrow extends FormCustomDraw {
   public FormProcessingResultArrow(int var1, int var2) {
      super(var1, var2, 32, 32);
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      Settings.UI.processing_arrow_empty.initDraw().draw(this.getX(), this.getY());
      ResultArrowState var4 = this.getState(var2);
      if (var4 != null) {
         byte var5 = 2;
         byte var6 = 2;
         int var7 = Settings.UI.processing_arrow_full.getWidth() - (var5 + var6);
         Settings.UI.processing_arrow_full.initDraw().section(var5, var5 + (int)((float)var7 * var4.arrowProgress), 0, Settings.UI.processing_arrow_full.getHeight()).draw(this.getX() + var5, this.getY());
         if (this.isHovering() && var4.tooltips != null) {
            Screen.addTooltip(var4.tooltips, var4.tooltipsBackground, TooltipLocation.FORM_FOCUS);
         }
      }

   }

   public abstract ResultArrowState getState(PlayerMob var1);

   public static class ResultArrowState {
      public float arrowProgress;
      public GameTooltips tooltips;
      public GameBackground tooltipsBackground;

      public ResultArrowState(float var1, GameTooltips var2, GameBackground var3) {
         this.arrowProgress = var1;
         this.tooltips = var2;
         this.tooltipsBackground = var3;
      }

      public ResultArrowState(float var1, GameTooltips var2) {
         this(var1, var2, (GameBackground)null);
      }
   }
}
