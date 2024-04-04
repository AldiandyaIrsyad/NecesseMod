package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.gfx.GameBackground;

public class BackgroundedGameTooltips implements GameTooltips {
   public final GameTooltips tooltips;
   public final GameBackground background;

   public BackgroundedGameTooltips(GameTooltips var1, GameBackground var2) {
      this.tooltips = var1;
      this.background = var2;
   }

   public int getHeight() {
      return this.tooltips.getHeight() + (this.background == null ? 0 : this.background.getContentPadding() * 2);
   }

   public int getWidth() {
      return this.tooltips.getWidth() + (this.background == null ? 0 : this.background.getContentPadding() * 2);
   }

   public int getDrawXOffset() {
      return this.tooltips.getDrawXOffset();
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      int var4 = 0;
      if (this.background != null) {
         var4 = this.background.getContentPadding();
         this.background.getDrawOptions(var1 - var4, var2 - var4, this.tooltips.getWidth() + var4 * 2, this.tooltips.getHeight() + var4 * 2).draw();
      }

      this.tooltips.draw(var1 + var4, var2 + var4, var3);
      if (this.background != null) {
         this.background.getEdgeDrawOptions(var1 - var4, var2 - var4, this.tooltips.getWidth() + var4 * 2, this.tooltips.getHeight() + var4 * 2).draw();
      }

   }

   public int getDrawOrder() {
      return this.tooltips.getDrawOrder();
   }
}
