package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class MidArrowComparingTooltips implements GameTooltips {
   public int height;
   public FontOptions fontOptions;

   public MidArrowComparingTooltips(int var1) {
      this.fontOptions = (new FontOptions(Settings.tooltipTextSize)).outline();
      this.height = var1;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return 8;
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      int var4 = 0;

      while(true) {
         int var5 = FontManager.bit.getHeightCeil('>', this.fontOptions);
         var4 += var5;
         if (var4 > this.height) {
            return;
         }

         FontManager.bit.drawChar((float)(var1 - 4), (float)(var2 + var4 - var5), '>', this.fontOptions);
      }
   }

   public int getDrawOrder() {
      return 0;
   }
}
