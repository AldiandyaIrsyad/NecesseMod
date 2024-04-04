package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.gfx.GameColor;

public class DefaultColoredGameTooltips implements GameTooltips {
   public final GameTooltips tooltips;
   public final Supplier<Color> defaultColor;

   public DefaultColoredGameTooltips(GameTooltips var1, Color var2) {
      this(var1, () -> {
         return var2;
      });
   }

   public DefaultColoredGameTooltips(GameTooltips var1, GameColor var2) {
      this(var1, var2.color);
   }

   public DefaultColoredGameTooltips(GameTooltips var1, Supplier<Color> var2) {
      this.tooltips = var1;
      this.defaultColor = var2;
   }

   public int getHeight() {
      return this.tooltips.getHeight();
   }

   public int getWidth() {
      return this.tooltips.getWidth();
   }

   public int getDrawXOffset() {
      return this.tooltips.getDrawXOffset();
   }

   public void draw(int var1, int var2, Supplier<Color> var3) {
      this.tooltips.draw(var1, var2, this.defaultColor);
   }

   public int getDrawOrder() {
      return this.tooltips.getDrawOrder();
   }
}
